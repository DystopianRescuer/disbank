package com.rateroscoloniatesocongo.disbank.transacciones;

import java.util.zip.ZipException;

public class CobroFisico implements Cobro {
    @Override
    public double getPrecio() {
        return 0;
    }

    @Override
    public String getBody() {
        return null;
    }

    @Override
    public void getPeticion() {

    }
}
