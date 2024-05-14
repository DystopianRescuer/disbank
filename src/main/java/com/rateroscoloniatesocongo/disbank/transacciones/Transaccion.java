package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import java.util.Optional;

public class Transaccion {

    public enum Estado {
        PENDIENTE, PAGADA, FALLIDA;
    }

    private final Asociado asociado;
    private String log;
    private final Cobro cobro;
    private Estado estado;
    private Optional<String> link;
    private String id;

    public Optional<String> getLink() {
        return link;
    }

    public void setLink(Optional<String> link) {
        this.link = link;
    }

    public Transaccion(Asociado asociado, Cobro cobro) {
        this.asociado = asociado;
        this.cobro = cobro;
        this.estado = Estado.PENDIENTE;
        this.id = cobro.getID();
    }

    public Asociado getAsociado() {
        return asociado;
    }

    public String getId() {
        return this.id;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Cobro getCobro() {
        return cobro;
    }

    public boolean esLogVacio() {
        return log == null;
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
}
