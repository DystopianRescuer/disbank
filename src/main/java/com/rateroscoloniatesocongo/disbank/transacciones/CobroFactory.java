package com.rateroscoloniatesocongo.disbank.transacciones;

/**
 * Factory para generar cobros.
 *
 * En la version actual solo hay dos tipos de cobro, pero le quise dar extensibilidad al proyecto con este factory.
 */
public class CobroFactory {

    public static Cobro generaCobro(String tipo, double cantidad){
        switch(tipo){
            case "Fisico":
                return new CobroFisico(cantidad, "Cobro");
            case "Link":
                return new CobroLink(cantidad, "Cobro");
        }
    }
}
