package com.rateroscoloniatesocongo.disbank.clipservice.excepciones;

public class TransaccionNoRegistradaException extends Exception {

    public TransaccionNoRegistradaException() {
    }

    public TransaccionNoRegistradaException(String message) {
        super(message);
    }
}
