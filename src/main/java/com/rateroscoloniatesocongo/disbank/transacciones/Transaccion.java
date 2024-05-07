package com.rateroscoloniatesocongo.disbank.transacciones;

import java.util.Optional;

public class Transaccion {

    private String id, estado;
    private Cobro cobro;

    private Optional<String> log;

    Transaccion() {
    }
}
