package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.clipservice.ControladorClip;
import com.rateroscoloniatesocongo.disbank.clipservice.excepciones.TransaccionNoRegistradaException;
import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.modelo.Cortador;
import com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;
import com.rateroscoloniatesocongo.disbank.telegramservice.mensajes.MensajeFactory;
import com.rateroscoloniatesocongo.disbank.util.Avisador;

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
        cortador.cortar();
        instance = null;
    }

    /**
     * Metodo para solicitar nueva transaccion a controladorClip
     *
     * @param asociado
     * @param cobro
     * @return
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
            // TODO
        }
        // Si esto ocurrió correctamente entonces registra la transacción en pendientes
        pendientes.add(transaccion);
        try{
            controladorTelegram.enviarMensaje(MensajeFactory.nuevoMensaje("Cobro", asociado, transaccion));
        }catch (ErrorEnConexionException e){
            // TODO error en la terminal admin
        }
        return "";
    }

    /**
     * @param transaccion
     */
    public void actualizarEstado(Transaccion transaccion) {
        // Checa el estado de la transaccion
        // Si todo sale bien va y le avisa a telegram que todo salio bien para que le avise al usuario
        // Actualiza lo que deba actualizar en base de datos
        // Quita de su lista dependientes
        // Esto de momento, checar/pensar si falta algo más
    }

    /**
     * Metodo que busca y regresa una transaccion mediante su id.
     *
     * @param id id de la transaccion buscada. (String)
     * @return t. transaccion requerida. (Transaccion)
     */
    public Transaccion getTransaccionPorId(String id) {
        for (Transaccion t : pendientes) {
            if (t.getId().equals(id)) {
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
