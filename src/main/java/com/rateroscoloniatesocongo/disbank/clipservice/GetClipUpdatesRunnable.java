package com.rateroscoloniatesocongo.disbank.clipservice;

import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;
import com.rateroscoloniatesocongo.disbank.util.ConfigReader;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GetClipUpdatesRunnable implements Runnable {

    private static final String WEBHOOK_UUID = ConfigReader.getField("webhook.uuid");

    private HttpResponse<String> peticionUltimoRequest() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://webhook.site/token/" + WEBHOOK_UUID + "/request/latest/"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        System.out.println(request);

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        if(response.statusCode() == 200) {
            return response;
        }
        return null;
    }

    private void checarClipUpdate(JSONObject jsonObject) {
        System.out.println("fefrrferf");
        String idTransaccion = jsonObject.getString("merch_inv_id");
        if(!idTransaccion.isBlank()) {
            ControladorClip.getInstance().actualizarTransaccion(idTransaccion,
                    jsonObject.getJSONObject("payment_request_detail").getString("status_description").equals("Completed") ?
                            Transaccion.Estado.PAGADA : Transaccion.Estado.FALLIDA);
        }
    }

    private void eliminarRequest(String toBeDeleted) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://webhook.site/token/" + WEBHOOK_UUID + "/request/" + toBeDeleted))
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
        System.out.println("Inicio run Clip Updates Checker");
        // Pide el último request, si consigue un código 200 entonces lo guarda y pide a la API eliminar ese request y acaba la ejecución
        HttpResponse<String> response;
        JSONObject fullRequest;

        try {
            while ((response = peticionUltimoRequest()) != null) {
                fullRequest = new JSONObject(response.body());
                System.out.println("Request = " + fullRequest);

                // Pasa el mensaje de Clip al método que lo procesa
//                checarClipUpdate(fullRequest.getJSONObject("content"));

                // Una vez procesado, elimina el request de la cola de Webhook.site
//                eliminarRequest(fullRequest.getString("uuid"));
                System.out.println("eliminar request");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("hola");
            throw new RuntimeException(e);
        }
    }

}
