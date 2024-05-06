package com.rateroscoloniatesocongo.disbank.api;

import com.rateroscoloniatesocongo.disbank.transacciones.Cobro;

public class ControladorClip {

    private static ControladorClip instance;

    private ControladorClip(String token) {

    }

    public static ControladorClip getInstance() {
        if(instance == null) {
            instance = new ControladorClip();
        }
        return instance;
    }
}
