package com.rateroscoloniatesocongo.disbank.api;

import com.rateroscoloniatesocongo.disbank.transacciones.Cobro;
import com.rateroscoloniatesocongo.disbank.util.ConfigReader;

public class ControladorClip {

    private static ControladorClip instance;

    private final String apikey;

    private ControladorClip(String apikey) {
        this.apikey = apikey;
    }

    public static ControladorClip getInstance() {
        if(instance == null) {
            instance = new ControladorClip(ConfigReader.getField("clip.apikey"));
        }
        return instance;
    }
}
