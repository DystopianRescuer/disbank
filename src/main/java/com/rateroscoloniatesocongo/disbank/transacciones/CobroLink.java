package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.util.ConfigReader;

import java.util.Optional;
import java.util.UUID;

public class CobroLink implements Cobro {

    private static final String API = "checkout", CURRENCY = "MXN";

    private final double cantidad;
    private final String mensaje, urlDefault, urlSuccess, urlError, ID;

    public CobroLink(double cantidad, String mensaje) {
        this.cantidad = cantidad;
        this.mensaje = mensaje;
        this.ID = UUID.randomUUID().toString();

        this.urlDefault = ConfigReader.getField("clip.url.default");
        this.urlSuccess = ConfigReader.getField("clip.url.success");
        this.urlError = ConfigReader.getField("clip.url.error");
    }

    @Override
    public String getCantidad() {
        return "$" + cantidad + " " + CURRENCY;
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
    public Optional<String> getRespuestaKey() {
        return Optional.of("qr_image_url");
    }

    @Override
    public String getBody() {
        return String.format("{\"amount\":%f,\"currency\":\"%s\"," +
                        "\"purchase_description\":\"%s\"," +
                        "\"redirection_url\":{\"success\":\"%s\"," +
                        "\"error\":\"%s\"," +
                        "\"default\":\"%s\"}," +
                        "\"metadata\":{\"me_reference_id\":\"%s\"},\"override_settings\":{\"currency\":{\"show_currency_code\":true}," +
                        "\"payment_method\":[\"CARD\"],\"locale\":\"es-MX\",\"enable_tip\":false}}",
                this.cantidad, CURRENCY, this.mensaje, urlSuccess, urlError, urlDefault, ID);
    }
}
