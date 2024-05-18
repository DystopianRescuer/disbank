package com.rateroscoloniatesocongo.disbank.clipservice;

import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;
import com.rateroscoloniatesocongo.disbank.util.ConfigReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Clase que permite correr un chequeo de updates por parte del servicio Webhook, esto con el fin de recibir posibles actualizaciones a transacciones pendientes
 * En esencia cada vez que es solicitada su ejecución con el método run() pide todas las actualizaciones pendientes que haya, las procesa buscando posibles modificaciones
 * a las Transacciones pendientes registradas, finalmente las elimina.
 */
public class GetClipUpdatesRunnable implements Runnable {

    /**
     * El UUID del servicio de webhook.site, esto indentifica la cola de request por procesar pendientes y nos permite hacerle peticiones a su API
     */
    private static final String WEBHOOK_UUID = ConfigReader.getField("webhook.uuid");

    /**
     * Método que nos retorna todas las updates que hay en cola
     * @return las updates en cola
     * @throws IOException si hay un problema de entrada/salida
     * @throws InterruptedException si la conexión es interrumpida
     */
    private JSONArray getLastUpdates() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://webhook.site/token/" + WEBHOOK_UUID + "/requests"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return new JSONObject(response.body()).getJSONArray("data");
        }
        return null;
    }

    /**
     * Método que, dado el content de una actualización por parte de Clip, hace las actualizaciones necesarias a sus representaciones dentro del programa
     * @param jsonObject el content de una actualización por parte de Clip
     */
    private void checarClipUpdate(JSONObject jsonObject) {
        try {
            String idTransaccion = jsonObject.getString("merch_inv_id");
            System.out.println("Actualizando pago con id " + idTransaccion);
            ControladorClip.getInstance().actualizarTransaccion(idTransaccion,
                    jsonObject.getString("status").equals("PAID") ?
                            Transaccion.Estado.PAGADA : Transaccion.Estado.FALLIDA);
        } catch (JSONException e) {
            // Si esto pasa es porque el JSON no trae formato de respuesta de Clip, solo se ignora y el siguiente paso lo borrará
        }
    }

    /**
     * Método que elimina una update de la cola dado su uuid
     * @param toBeDeleted el uuid de la update a ser eliminada
     */
    private void eliminarUpdate(String toBeDeleted) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://webhook.site/token/" + WEBHOOK_UUID + "/request/" + toBeDeleted))
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
        }
        System.out.println("Request eliminado con id: " + toBeDeleted);
    }

    /**
     * Usado al principio de la ejecución del programa para eliminar toda la "basura" que pueda haber en el webhook
     */
    public void eliminarTodoRequest() {
        try {
            JSONArray jsonArray = getLastUpdates();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    eliminarUpdate(jsonArray.getJSONObject(i).getString("uuid"));
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            // Si hay algún JSON con mal formato sólo se ignora
        }
    }


    /**
     * Método principal de GetClipUpdates
     */
    @Override
    public void run() {
        System.out.println("GetClipUpdates ran");
        JSONObject fullRequest;
        try {
            JSONArray updates = getLastUpdates();
            if (updates != null) {
                for (int i = 0; i < updates.length(); i++) {
                    fullRequest = updates.getJSONObject(i);

                    // Pasa el mensaje de Clip al método que lo procesa
                    checarClipUpdate(new JSONObject(fullRequest.getString("content")));

                    // Una vez procesado, elimina el request de la cola de Webhook.site
                    eliminarUpdate(fullRequest.getString("uuid"));
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            // Si hay algún JSON con mal formato sólo se ignora
        }
    }

}
