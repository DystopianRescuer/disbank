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

public class GetClipUpdatesRunnable implements Runnable {

    private static final String WEBHOOK_UUID = ConfigReader.getField("webhook.uuid");

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

    private void checarClipUpdate(JSONObject jsonObject) {
        try {
            String idTransaccion = jsonObject.getString("merch_inv_id");
            System.out.println("Actualizando pago con id " + idTransaccion);
            ControladorClip.getInstance().actualizarTransaccion(idTransaccion,
                    jsonObject.getString("status").equals("PAID") ?
                            Transaccion.Estado.PAGADA : Transaccion.Estado.FALLIDA);
            System.out.println("Termino de checarClipUpdate");
        } catch (JSONException e) {
            // Si esto pasa es porque el JSON no trae formato de respuesta de Clip, solo se ignora y el siguiente paso lo borrará
        }
    }

    private void eliminarRequest(String toBeDeleted) {
        System.out.println("Eliminando request con id" + toBeDeleted);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://webhook.site/token/" + WEBHOOK_UUID + "/request/" + toBeDeleted))
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .build();
        System.out.println("Request eliminado con id" + toBeDeleted);
        try {
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void eliminarTodoRequest() {
        try {
            JSONArray jsonArray = getLastUpdates();
            if(jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    eliminarRequest(jsonArray.getJSONObject(i).getString("uuid"));
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            e.printStackTrace(System.out);
        }
    }

    @Override
    public void run() {
        System.out.println("Inicio run Clip Updates Checker");
        // Pide el último request, si consigue un código 200 entonces lo guarda y pide a la API eliminar ese request y acaba la ejecución
        HttpResponse<String> response;
        JSONObject fullRequest;

        try {
            JSONArray updates = getLastUpdates();
            if(updates != null) {
                for (int i = 0; i < updates.length(); i++){
                    fullRequest = updates.getJSONObject(i);

                    // Pasa el mensaje de Clip al método que lo procesa
                    checarClipUpdate(new JSONObject(fullRequest.getString("content")));

                    // Una vez procesado, elimina el request de la cola de Webhook.site
                    eliminarRequest(fullRequest.getString("uuid"));
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            e.printStackTrace(System.out);
        }
        System.out.println("Saliendo del run");
    }

}
