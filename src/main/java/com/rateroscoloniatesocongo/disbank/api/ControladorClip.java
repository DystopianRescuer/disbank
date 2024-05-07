package com.rateroscoloniatesocongo.disbank.api;

import com.rateroscoloniatesocongo.disbank.transacciones.Cobro;
import com.rateroscoloniatesocongo.disbank.util.ConfigReader;

public class ControladorClip {

    private static ControladorClip instance;

    private final String apikey;

    private ControladorClip(String apikey) {
        this.apikey = apikey;
    }

    // Hace la petición a la API de Clip para la nueva transacción, devuelve true si se registró correctamente
    private boolean solicitarTransaccion() {
        return false;
    }

    public static ControladorClip getInstance() {
        if(instance == null) {
            instance = new ControladorClip(ConfigReader.getInstance().getField("clip.apikey"));
        }
        return instance;
    }
}
