package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.util.ConfigReader;

import java.util.UUID;

public class CobroFisico implements Cobro {

    private static final String API = "paymentrequest";

    private final double cantidad;
    private final String user;
    private final String mensaje, ID;

    public CobroFisico(double cantidad, String mensaje) {
        this.cantidad = cantidad;
        this.user = ConfigReader.getField("clip.user");
        this.mensaje = mensaje;
        this.ID = UUID.randomUUID().toString();
    }

    @Override
    public String getCantidad() {
        return "$" + cantidad + " MXN";
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getAPI() {
        return API;
    }

    @Override
    public String getBody() {
        return String.format("{\"amount\":%f,\"assigned_user\":\"%s\",\"reference\":\"%s\",\"message\":\"%s\"}", this.cantidad, this.user, this.ID, this.mensaje);
    }
}
