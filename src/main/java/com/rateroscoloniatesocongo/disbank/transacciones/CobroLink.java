package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.util.ConfigReader;

public class CobroLink implements Cobro {

    private static final String API = "checkout", CURRENCY = "MXN";

    private int cantidad;
    private String user, referencia, mensaje;

    public CobroLink(int cantidad, String referencia) {
        this.cantidad = cantidad;
        this.referencia = referencia;
        this.user = ConfigReader.getField("clip.user");
        this.mensaje = "";
    }

    @Override
    public String getAPI() {
        return API;
    }

    @Override
    public String getBody() {
        return String.format("{\"amount\":%d,\"currency\":\"%s\",\"purchase_description\":\"%s\"," +
                "\"redirection_url\":{\"success\":\"https://my-website.com/redirection/success?me_reference_id=OID123456789\"," +
                "\"error\":\"https://my-website.com/redirection/error?me_reference_id=OID123456789\"," +
                "\"default\":\"https://my-website.com/redirection/default\"}}", this.cantidad, this.user, this.referencia, this.mensaje);
    }
}
