package com.rateroscoloniatesocongo.disbank.api;

import com.rateroscoloniatesocongo.disbank.transacciones.Cobro;
import com.rateroscoloniatesocongo.disbank.transacciones.CobroFisico;
import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;
import com.rateroscoloniatesocongo.disbank.util.ConfigReader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ControladorClip {

    private static ControladorClip instance;

    private final String apikey;

    private ControladorClip(String apikey) {
        this.apikey = apikey;
    }

    // Hace la petición a la API de Clip para la nueva transacción
    public void solicitarTransaccion(Transaccion transaccion) throws IOException, InterruptedException {

        // Cobro a procesar
        Cobro cobro = transaccion.getCobro();
        // Request a realizar
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-gw.payclip.com/" + cobro.getAPI()))
                .header("accept", "application/vnd.com.payclip.v2+json")
                .header("content-type", "application/json; charset=UTF-8")
                .header("x-api-key", this.apikey)
                .method("POST", HttpRequest.BodyPublishers.ofString(cobro.getBody()))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        // Body del response del request
        String body = response.body();
    }

    public static ControladorClip getInstance() {
        if(instance == null) {
            instance = new ControladorClip(ConfigReader.getField("clip.apikey"));
        }
        return instance;
    }
}
