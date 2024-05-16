package com.rateroscoloniatesocongo.disbank.transacciones;

/**
 * Factory para generar cobros.
 * <p> <p>
 * En la version actual solo hay dos tipos de cobro, pero le quise dar extensibilidad al proyecto con este factory.
 */
public class CobroFactory {

    public static Cobro generaCobro(String tipo, double cantidad){
        return switch (tipo) {
            case "terminal" -> new CobroFisico(cantidad, "Cobro");
            case "Link" -> new CobroLink(cantidad, "Cobro");
            default -> throw new IllegalArgumentException(tipo + " no es un cobro válido");
        };
    }
}
