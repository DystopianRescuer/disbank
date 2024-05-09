package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;

public class Transaccion {

    public enum Estado {
<<<<<<< HEAD
        PENDIENTE, PAGADA, FALLIDA
=======
        PENDIENTE, FINALIZADA, FALLIDA
>>>>>>> c1f1444 (modificaciones en gestor y transaccion)
    }

    private Asociado asociado;
    private String id, log;
    private Cobro cobro;
    private Estado estado;

    public Transaccion(Asociado asociado, Cobro cobro) {
        this.asociado = asociado;
        this.cobro = cobro;
        this.estado = Estado.PENDIENTE;
    }

    public Asociado getAsociado() {
        return asociado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
