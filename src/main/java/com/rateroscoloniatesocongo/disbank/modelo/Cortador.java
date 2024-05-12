package com.rateroscoloniatesocongo.disbank.modelo;

import com.rateroscoloniatesocongo.disbank.bd.BaseDatos;

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
            mandarErrorFatal("No se pudo establecer conexion con Telegram.");
        }
        controladorClip = ControladorClip.getInstance();
    }

    public void cortar(){

    }

}
