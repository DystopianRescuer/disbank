package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.clipservice.ControladorClip;
import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;

import java.util.LinkedList;
import java.util.List;
/**
 * Proyecto 2 del curso de Modelado y Programacion.
 * Clase GestorTransacciones.
 * @author Rateros colonia tesocongo.
 * @version Version 1.
 */
public class GestorTransacciones {

    private static GestorTransacciones instance;

    private final List<Transaccion> pendientes;
    private ControladorTelegram controladorTelegram;
    private final ControladorClip controladorClip;
    private boolean iniciado;

    private GestorTransacciones() {
        pendientes = new LinkedList<>();
        iniciado = false;
        try{
            controladorTelegram = ControladorTelegram.getInstance();
        }catch(ErrorEnConexionException e){
            //Placeholder
            //TODO: Desarrollar logica de este error
            System.out.println("No se pudo establecer conexion con Telegram.");
            System.exit(1);
        }
        controladorClip = ControladorClip.getInstance();
    }

    // Inicia el gestor de transacciones
    public void iniciar() {
        if(!iniciado) {
            // TBD
            iniciado = true;
        }
    }

    // TODO: hacer esto seguro, que no cualquiera pueda llamar
    public void detener() {

    }

    public String nuevaTransaccion(Asociado asociado, Cobro cobro) {
        // Crea el objeto transacción
        // Se la da a Clip y espera la respuesta positiva de este
        controladorClip.solicitarTransaccion();
        // Si esto ocurrió correctamente entonces registra la transacción en pendientes
        return "";
    }


    // TODO los argumentos que recibe
    public void actualizarEstado(Transaccion transaccion) {
        // Checa el estado de la transaccion
        // Si todo sale bien va y le avisa a telegram que todo salio bien para que le avise al usuario
        // Actualiza lo que deba actualizar en base de datos
        // Quita de su lista dependientes
        // Esto de momento, checar/pensar si falta algo más
    }

    public Transaccion getTransaccionPorId(String id) {
        return null;
    }


    public static GestorTransacciones getInstance() {
        if(instance == null) {
            instance = new GestorTransacciones();
        }
        return instance;
    }
}
