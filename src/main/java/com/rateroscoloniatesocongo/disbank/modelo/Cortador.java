package com.rateroscoloniatesocongo.disbank.modelo;

import com.rateroscoloniatesocongo.disbank.bd.BaseDatos;
import com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram;
import com.rateroscoloniatesocongo.disbank.clipservice.ControladorClip;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;
import com.rateroscoloniatesocongo.disbank.util.Avisador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Cortador {
    /** Unica instancia de ControladorTelegram */
    private ControladorTelegram controladorTelegram;
    /** Unica instancia de ControladorClip */
    private final ControladorClip controladorClip;

    public Cortador() {
        try{
            controladorTelegram = ControladorTelegram.getInstance();
        }catch(ErrorEnConexionException e){
            Avisador.mandarErrorFatal("No se pudo establecer conexion con Telegram.");
        }
        controladorClip = ControladorClip.getInstance();
    }

    public void corteDiario(){
        //vaciar en txt las transacciones del dia
        // mensaje corte a los asociados via telegram
    }

    public void cortePersonal(Asociado asociado){
        //Enviar un corte preliminar al asociado invocado.
        //Solo fabrica el mensaje y envialo con enviarMensaje()
    }
}
