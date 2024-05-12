package com.rateroscoloniatesocongo.disbank.modelo;

import com.rateroscoloniatesocongo.disbank.bd.BaseDatos;
import com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram;
import com.rateroscoloniatesocongo.disbank.clipservice.ControladorClip;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;
import com.rateroscoloniatesocongo.disbank.util.Avisador;

public class Cortador {
    /** Unica instancia de ControladorTelegram */
    private ControladorTelegram controladorTelegram;
    /** Unica instancia de ControladorClip */
    private ControladorClip controladorClip;
    /** Base de datos */
    private BaseDatos bd;

    public Cortador(){
        bd = new BaseDatos();
        try{
            controladorTelegram = ControladorTelegram.getInstance();
        }catch(ErrorEnConexionException e){
            Avisador.mandarErrorFatal("No se pudo establecer conexion con Telegram.");
        }
        controladorClip = ControladorClip.getInstance();
    }

    public void cortar(){

    }

}
