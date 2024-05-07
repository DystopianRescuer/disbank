package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.util.ConfigReader;

public class CobroFisico implements Cobro {

    private static final String API = "paymentrequest";
    private int cantidad;
    private String user, referencia, mensaje;

    public CobroFisico(int cantidad, String referencia, String body) {
        this.cantidad = cantidad;
        this.referencia = referencia;
        this.user = ConfigReader.getInstance().getField("clip.user");
        this.mensaje = "";
    }


    @Override
    public String getAPI() {
        return API;
    }

    @Override
    public String getBody() {
        return String.format("{\"amount\":%d,\"assigned_user\":\"%s\",\"reference\":\"%s\",\"message\":\"%s\"}", this.cantidad, this.user, this.referencia, this.mensaje);
    }
}
