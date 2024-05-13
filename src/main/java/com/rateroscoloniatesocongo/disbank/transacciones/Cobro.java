package com.rateroscoloniatesocongo.disbank.transacciones;

import java.util.Optional;

public interface Cobro {

    String getAPI();

    String getID();

    // Aquí se generaliza la respuesta que puede (o no) producir un request con las implementaciones de los cobros, por default no devuelve respuesta
    default Optional<String> getRespuestaKey() {
        return Optional.empty();
    }

    String getBody();

}
