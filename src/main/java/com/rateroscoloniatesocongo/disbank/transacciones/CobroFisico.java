package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.util.ConfigReader;

public class CobroFisico implements Cobro {

    private static final String API = "paymentrequest";

    private final int cantidad;
    private final String user;
    private final String referencia;
    private final String mensaje;

    public CobroFisico(int cantidad, String referencia, String mensaje) {
        this.cantidad = cantidad;
        this.referencia = referencia;
        this.user = ConfigReader.getField("clip.user");
        this.mensaje = mensaje;
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
