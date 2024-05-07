package com.rateroscoloniatesocongo.disbank.transacciones;

import java.util.Optional;

public class Transaccion {

    private String id, estado;
    private Cobro cobro;

    private Optional<String> log;

    Transaccion() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Cobro getCobro() {
        return cobro;
    }

    public void setCobro(Cobro cobro) {
        this.cobro = cobro;
    }

    public Optional<String> getLog() {
        return log;
    }

    public void setLog(Optional<String> log) {
        this.log = log;
    }
}
