package com.rateroscoloniatesocongo.disbank.clipservice;

import com.rateroscoloniatesocongo.disbank.clipservice.excepciones.TransaccionNoRegistradaException;
import com.rateroscoloniatesocongo.disbank.fx_controllers.PanelControlController;
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
import java.util.concurrent.TimeUnit;

/**
 * Clase que abstrae la API de Clip para uso de todo el programa.
 * Permite solicitar nuevas transaccion y actualizar pendientes
 * Además activa el escuchador de updates
 */
public class ControladorClip {

    /**
     * Única instancia del controlador
     */
    private static ControladorClip instance;

    /**
     * Api Key de clip
     */
    private final String apikey;
    /**
     * El servicio de ejecución encargado de el escuchador de updates
     */
    private final ScheduledExecutorService executorService;
    /**
     * Nos dice si el escuchador está corriendo
     * Permite la pequeña optimización de no escuchar updates hasta que exista una transacción
     */
    private boolean escuchadorIniciado;

    /**
     * Constructor del Controlador Clip
     * @param apikey Api Key de clip
     */
    private ControladorClip(String apikey) {
        this.apikey = apikey;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.escuchadorIniciado = false;
        iniciarEscuchador();
    }

    /**
     * Método que hace la petición a la API de Clip para una nueva transacción
     *
     * @param transaccion el objeto Transacción que modela la transacción a solicitar
     * @return una posible respuesta por parte de la API
     * @throws IOException cuando haya algún problema de entrada/salida
     * @throws InterruptedException cuando la conexión sea interrumpida
     * @throws TransaccionNoRegistradaException cuando la API de clip de una respuesta negativa al intento
     */
    public Optional<String> solicitarTransaccion(Transaccion transaccion) throws IOException, InterruptedException, TransaccionNoRegistradaException {

        if (!escuchadorIniciado) {
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

        if (transaccion.getRespuestaKey().isPresent()) {
            return Optional.of(jsonResponse.getString(transaccion.getRespuestaKey().get()));
        }

        return Optional.empty();
    }

    /**
     * Método que actualiza el estado de una transacción, usada únicamente por el hilo de chequeo de updates
     * @param id el id de la Transacción a actualizar
     * @param estado el estado al que hay que actualizar dicha transacción
     */
    // Default para que solo pueda ser llamado dentro del package de clipservice
    // Solo por seguridad es sincronizado, aunque con la implemetación actual el pool de hilos solo tiene un hilo
    synchronized void actualizarTransaccion(String id, Transaccion.Estado estado) {
        System.out.println("Actualizando " + id + " en estado " + estado.toString());
        Transaccion transaccion = GestorTransacciones.getInstance().getTransaccionPorId(id);

        if (transaccion != null) {
            System.out.println("Objeto transaccion encontrado: " + transaccion);
            transaccion.setEstado(estado);
            GestorTransacciones.getInstance().actualizarEstado(transaccion);
            System.out.println("Terminando de actualizar");
            PanelControlController.refrescarTablaTransacciones();
        } else {
            System.out.println("Transaccion a actualizar no encontrada");
        }
    }

    /**
     * Método que inicia el escuchador de updates
     * Pone un programador (no sé como traducir Scheduler) a correr el Runnable cada 10 segundos
     */
    private void iniciarEscuchador() {
        GetClipUpdatesRunnable getClipUpdatesRunnable = new GetClipUpdatesRunnable();
        getClipUpdatesRunnable.eliminarTodoRequest();

        executorService.scheduleWithFixedDelay(getClipUpdatesRunnable, 10, 10, TimeUnit.SECONDS);
        escuchadorIniciado = true;
    }


    /**
     * @return la instancia Singleton del controlador
     */
    public static ControladorClip getInstance() {
        if (instance == null) {
            instance = new ControladorClip(ConfigReader.getField("clip.apikey"));
        }
        return instance;
    }
}
