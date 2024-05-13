package com.rateroscoloniatesocongo.disbank.clipservice;

import com.rateroscoloniatesocongo.disbank.clipservice.excepciones.TransaccionNoRegistradaException;
import com.rateroscoloniatesocongo.disbank.transacciones.Cobro;
import com.rateroscoloniatesocongo.disbank.transacciones.GestorTransacciones;
import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;
import com.rateroscoloniatesocongo.disbank.util.ConfigReader;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ControladorClip {

    private static ControladorClip instance;

    private final String apikey;

    private ControladorClip(String apikey) {
        this.apikey = apikey;
        iniciarEscuchador();
    }

    // Hace la petición a la API de Clip para la nueva transacción
    public Optional<String> solicitarTransaccion(Transaccion transaccion) throws IOException, InterruptedException, TransaccionNoRegistradaException {
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
        JSONObject jsonResponse = new JSONObject(response.body());

        // Body del response del request
        String body = response.body();
        if (response.statusCode() != 200) {
            throw new TransaccionNoRegistradaException("Respuesta: " + response.statusCode() + ". Response body: " + body);
        }

        if(cobro.getRespuestaKey().isPresent()) {
            return Optional.of(jsonResponse.getString(cobro.getRespuestaKey().get()));
        }

        return Optional.empty();
    }

    // Default para que solo pueda ser llamado dentro del package de clipservice
    // Solo por seguridad es sincronizado, aunque con la implemetación actual el pool de hilos solo tiene un hilo
    synchronized void actualizarTransaccion(String id, Transaccion.Estado estado) {
        Transaccion transaccion = GestorTransacciones.getInstance().getTransaccionPorId(id);
        transaccion.setEstado(estado);
        GestorTransacciones.getInstance().actualizarEstado(transaccion);
    }

    // Pone a un Scheduler a correr cada 10 segundos un hilo que checa si hay actualizaciones
    private void iniciarEscuchador() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(GetClipUpdatesRunnable::new, 10, TimeUnit.SECONDS);
    }

    public static ControladorClip getInstance() {
        if(instance == null) {
            instance = new ControladorClip(ConfigReader.getField("clip.apikey"));
        }
        return instance;
    }
}
