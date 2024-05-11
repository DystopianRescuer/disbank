package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.util.ConfigReader;

public class CobroLink implements Cobro {

    private static final String API = "checkout", CURRENCY = "MXN";

    private final int cantidad;
    private final String user;
    private final String referencia;
    private final String mensaje;
    private final String urlDefault;
    private final String urlSuccess;
    private final String urlError;

    public CobroLink(int cantidad, String referencia, String mensaje) {
        this.cantidad = cantidad;
        this.referencia = referencia;
        this.user = ConfigReader.getField("clip.user");
        this.mensaje = mensaje;

        this.urlDefault = ConfigReader.getField("clip.url.default");
        this.urlSuccess = ConfigReader.getField("clip.url.success");
        this.urlError = ConfigReader.getField("clip.urs.error");
    }

    @Override
    public String getAPI() {
        return API;
    }

    @Override
    public String getBody() {
        return String.format("{\"amount\":%d,\"currency\":\"%s\",\"purchase_description\":\"%s\"," +
                "\"redirection_url\":{\"success\":\"%s\"," +
                "\"error\":\"%s\"," +
                "\"default\":\"%s\"}}", this.cantidad, CURRENCY, this.mensaje, urlSuccess, urlError, urlDefault);
    }
}
