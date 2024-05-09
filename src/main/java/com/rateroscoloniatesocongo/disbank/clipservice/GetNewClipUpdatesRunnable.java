package com.rateroscoloniatesocongo.disbank.clipservice;

import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;
import com.rateroscoloniatesocongo.disbank.util.ConfigReader;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GetNewClipUpdatesRunnable implements Runnable {

    private static final String UUID = ConfigReader.getField("webhook.uuid");

    // TODO: hacerlo recursivo, no es necesario ya que no puede haber varias actualizaciones al mismo tiempo, solo hay una terminal, pero se vuelve más robusto a futuro
    private HttpResponse<String> peticionUltimoRequest() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://webhook.site/token/" + UUID + "/request/latest/"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200) {
            return response;
        }
        return null;
    }

    private void checarClipUpdate(JSONObject jsonObject) {
        ControladorClip.getInstance().actualizarTransaccion(jsonObject.getString("transaction_id"),
                jsonObject.getJSONObject("payment_request_detail").getString("status_description").equals("Completed") ?
                Transaccion.Estado.PAGADA : Transaccion.Estado.FALLIDA);
    }

    private void eliminarRequest(String toBeDeleted) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://webhook.site/token/" + UUID + "/request/" + toBeDeleted))
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        // Pide el último request, si consigue un código 200 entonces lo guarda y pide a la API eliminar ese request y acaba la ejecución
        HttpResponse<String> response;
        JSONObject fullRequest;
        try {
            response = peticionUltimoRequest();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(response != null) {
            fullRequest = new JSONObject(response.body());

            // Pasa el mensaje de Clip al método que lo procesa
            checarClipUpdate(fullRequest.getJSONObject("content"));

            // Una vez procesado, elimina el request de la cola de Webhook.site
            eliminarRequest(fullRequest.getString("uuid"));
        }
    }

}
