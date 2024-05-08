package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.clipservice.ControladorClip;
import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;

import java.util.LinkedList;
import java.util.List;

public class GestorTransacciones {

    private static GestorTransacciones instance;

    private final List<Transaccion> pendientes;
    private ControladorTelegram controladorTelegram;
    private final ControladorClip controladorClip;
    private boolean iniciado;

    private GestorTransacciones() {
        pendientes = new LinkedList<>();
        try{
            controladorTelegram = ControladorTelegram.getInstance();
        }catch(ErrorEnConexionException e){
            //Placeholder
            //TODO: Desarrollar logica de este error
        }

        controladorClip = ControladorClip.getInstance();
    }

    // Inicia el gestor de transacciones
    public void iniciar() {
        if(!iniciado) {
            // TBD
        }
    }

    // TODO: hacer esto seguro, que no cualquiera pueda llamar
    public void detener() {

    }

    public String nuevaTransaccion(Asociado asociado, Cobro cobro) {
        // Crea el objeto transacción
        // Se la da a Clip y espera la respuesta positiva de este
        // Si esto ocurrió correctamente entonces registra la transacción en pendientes
        return "";
    }


    // TODO los argumentos que recibe
    public void actualizarEstado() {

    }


    public static GestorTransacciones getInstance() {
        if(instance == null) {
            instance = new GestorTransacciones();
        }
        return instance;
    }
}
