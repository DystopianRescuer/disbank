package com.rateroscoloniatesocongo.disbank.transacciones;

import java.util.Optional;

/**
 *  Las clases que implementen esta interfaz deben de poder dar todos los siguientes metodos, los cuales sirven para construir la
 *  peticion a la API de Clip.
 *  */
public interface Cobro {

    String getCantidad();

    String getAPI();

    String getID();

    // Aquí se generaliza la respuesta que puede (o no) producir un request con las implementaciones de los cobros, por default no devuelve respuesta
    default Optional<String> getRespuestaKey() {
        return Optional.empty();
    }

    String getBody();

}
