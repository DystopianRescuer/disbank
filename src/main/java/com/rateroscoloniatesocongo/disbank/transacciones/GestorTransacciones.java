package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.bd.BaseDatos;
import com.rateroscoloniatesocongo.disbank.clipservice.ControladorClip;
import com.rateroscoloniatesocongo.disbank.clipservice.excepciones.TransaccionNoRegistradaException;
import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.modelo.Cortador;
import com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;
import com.rateroscoloniatesocongo.disbank.telegramservice.mensajes.MensajeFactory;
import com.rateroscoloniatesocongo.disbank.util.Avisador;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Proyecto 2 del curso de Modelado y Programacion.
 * Clase GestorTransacciones.
 *
 * @author Rateros colonia tesocongo.
 * @version Version 1.
 */
public class GestorTransacciones {
    /**
     * Unica instancia de GestorTransacciones
     */
    private static GestorTransacciones instance;


    private ObservableList<Transaccion> transaccionesTotales;
    /**
     * Lista de transacciones pendientes
     */
    private final List<Transaccion> pendientes;
    /**
     * Unica instancia de ControladorTelegram
     */
    private ControladorTelegram controladorTelegram;
    /**
     * Unica instancia de ControladorClip
     */
    private final ControladorClip controladorClip;
    /**
     * Nos dice si ya fue iniciado o no
     */
    private boolean iniciado;

    /**
     * Inicializa atributos e instancia a ControladorTelegram y a ControladorClip
     */
    private GestorTransacciones() {
        transaccionesTotales = FXCollections.observableArrayList();
        pendientes = new LinkedList<>();
        iniciado = false;
        try {
            controladorTelegram = ControladorTelegram.getInstance();
        } catch (ErrorEnConexionException e) {
            Avisador.mandarErrorFatal("No se pudo establecer conexion con Telegram.");
        }
        controladorClip = ControladorClip.getInstance();
    }

    /**
     * Metodo que inicia al GestorTransacciones
     */
    public void iniciar() {
        if (!iniciado) {
            // TBD
            iniciado = true;
        }
    }

    /**
     * Metodo que detiene al GestorTransacciones y crea un Cortador para hacer el
     * corte del dia.
     */
    public void detener() {
        iniciado = false;
        Cortador cortador = new Cortador();
        cortador.corteDiario();
        instance = null;
    }

    /**
     * Metodo para solicitar nueva transaccion a controladorClip
     * Es ejecutado principalmente por el controladorTelegram. El cual es el que genera el Cobro. Luego, se construye el resto
     * del objeto Transaccion con ayuda del controladorClip.
     *
     * @param asociado el asociado que está solicitando la transaccion
     * @param cobro el objeto Cobro que compondrá a la nueva transaccion
     * @return Una
     */
    public String nuevaTransaccion(Asociado asociado, Cobro cobro) {
        // Crea el objeto transacción
        Transaccion transaccion = new Transaccion(asociado, cobro);
        // Se la da a Clip y espera la respuesta positiva de este
        try {
            transaccion.setLink(controladorClip.solicitarTransaccion(transaccion));
        } catch (TransaccionNoRegistradaException e) {
            //mandar mensaje a telegram de que no se pudo registrar la transaccion
            return "No se pudo registrar la transaccion."; // este es mientras hacemos el pquete
        } catch (IOException | InterruptedException e) {
            Avisador.mandarErrorFatal(e.getMessage());
        }
        // Si esto ocurrió correctamente entonces registra la transacción en pendientes
        pendientes.add(transaccion);
        try{
            controladorTelegram.enviarMensaje(MensajeFactory.nuevoMensaje("Cobro", asociado, transaccion));
        }catch (ErrorEnConexionException e) {
            Avisador.mandarErrorFatal(e.getMessage());
        }
        asociado.agregarTransaccionDia(transaccion);
        transaccionesTotales.add(transaccion);
        return "";
    }

    public ObservableList<Transaccion> getTransaccionesTotales() {
        return transaccionesTotales;
    }

    /**
     * @param transaccion
     */
    public void actualizarEstado(Transaccion transaccion) {
        try{
            controladorTelegram.enviarMensaje(MensajeFactory.nuevoMensaje("Estado", transaccion.getAsociado(), transaccion));
        }catch(ErrorEnConexionException e){
            Avisador.mandarErrorFatal(e.getMessage());

        }
        pendientes.remove(transaccion);
    }

    /**
     * Metodo que busca y regresa una transaccion mediante su id.
     *
     * @param id id de la transaccion buscada. (String)
     * @return t. transaccion requerida. (Transaccion)
     */
    public Transaccion getTransaccionPorId(String id) {
        for (Transaccion t : pendientes) {
            if (t.getIdTransaccion().equals(id)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Devuelve la unica instancia de GestorTransacciones.
     *
     * @return instance. (GestorTransacciones).
     */
    public static GestorTransacciones getInstance() {
        if (instance == null) {
            instance = new GestorTransacciones();
        }
        return instance;
    }
}
