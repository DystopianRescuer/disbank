package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;

import java.util.Optional;

public class Transaccion {

    public enum Estado {
        PENDIENTE, PAGADA, FALLIDA
    }

    private final Asociado asociado;
    private String log;
    private final Cobro cobro;
    private Estado estado;
    private Optional<String> link;
    private final String id;

    public Transaccion(Asociado asociado, Cobro cobro) {
        this.asociado = asociado;
        this.cobro = cobro;
        this.estado = Estado.PENDIENTE;
        this.id = cobro.getID();
    }

    public Optional<String> getLink() {
        return link;
    }

    public void setLink(Optional<String> link) {
        this.link = link;
    }

    public Asociado getAsociado() {
        return asociado;
    }

    public String getCantidad() {
        return cobro.getCantidad();
    }

    public String getIdTransaccion() {
        return this.id;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getAPI() {
        return cobro.getAPI();
    }

    public Optional<String> getRespuestaKey() {
        return cobro.getRespuestaKey();
    }

    public String getBody() {
        return cobro.getBody();
    }

    public boolean esLogVacio() {
        return log == null;
    }

    public String getTipoCobro() {
        return cobro.getClass().getSimpleName().replace("Cobro", "");
    }

    public String getLog() {
        return log;
    }

    public void addLog(String log) {
        if (esLogVacio()) {
            log = "";
        }
        this.log += log + "\n";
    }

    @Override
    public String toString() {
        return "ID: " + getIdTransaccion() + ": Transaccion por " + cobro.getCantidad() + "\n" +
                "Estado: " + estado;
    }


}
