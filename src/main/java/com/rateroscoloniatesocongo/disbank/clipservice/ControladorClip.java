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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ControladorClip {

    private static ControladorClip instance;

    private final String apikey;
    private final ScheduledExecutorService executorService;
    private boolean escuchadorIniciado;

    private ControladorClip(String apikey) {
        this.apikey = apikey;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.escuchadorIniciado = false;
        iniciarEscuchador();
    }

    // Hace la petición a la API de Clip para la nueva transacción
    public Optional<String> solicitarTransaccion(Transaccion transaccion) throws IOException, InterruptedException, TransaccionNoRegistradaException {

        if(!escuchadorIniciado) {
            iniciarEscuchador();
        }

        // Request a realizar
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-gw.payclip.com/" + transaccion.getAPI()))
                .header("accept", "application/vnd.com.payclip.v2+json")
                .header("content-type", "application/json; charset=UTF-8")
                .header("x-api-key", this.apikey)
                .method("POST", HttpRequest.BodyPublishers.ofString(transaccion.getBody()))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonResponse = new JSONObject(response.body());

        // Body del response del request
        String body = response.body();
        if (response.statusCode() != 200) {
            throw new TransaccionNoRegistradaException("Respuesta: " + response.statusCode() + ". Response body: " + body);
        }

        if(transaccion.getRespuestaKey().isPresent()) {
            return Optional.of(jsonResponse.getString(transaccion.getRespuestaKey().get()));
        }

        return Optional.empty();
    }

    // Default para que solo pueda ser llamado dentro del package de clipservice
    // Solo por seguridad es sincronizado, aunque con la implemetación actual el pool de hilos solo tiene un hilo
    synchronized void actualizarTransaccion(String id, Transaccion.Estado estado) {
        System.out.println("Actualizando " + id + " en estado " + estado.toString());
        Transaccion transaccion = GestorTransacciones.getInstance().getTransaccionPorId(id);
        System.out.println("Objeto transaccion encontrado: " + transaccion);
        transaccion.setEstado(estado);
        GestorTransacciones.getInstance().actualizarEstado(transaccion);
        System.out.println("Terminando de actualizar");


    }

    // Pone a un Scheduler a correr cada 10 segundos un hilo que checa si hay actualizaciones
    private void iniciarEscuchador() {
        GetClipUpdatesRunnable getClipUpdatesRunnable = new GetClipUpdatesRunnable();
        getClipUpdatesRunnable.eliminarTodoRequest();

        executorService.scheduleWithFixedDelay(getClipUpdatesRunnable, 10, 10, TimeUnit.SECONDS);
        escuchadorIniciado = true;
    }

    public static ControladorClip getInstance() {
        if(instance == null) {
            instance = new ControladorClip(ConfigReader.getField("clip.apikey"));
        }
        return instance;
    }
}
