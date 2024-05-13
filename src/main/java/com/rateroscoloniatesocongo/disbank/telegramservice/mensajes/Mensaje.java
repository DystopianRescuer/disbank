package com.rateroscoloniatesocongo.disbank.telegramservice.mensajes;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;

/**
 *  Todos los objetos que implementen la interface Mensaje deben de implementar su propio darMensaje, pues al final del día
 *  todos los mensajes deben de tener texto y se debe poder obtener de esta manera. Asi mismo, todo mensaje debe tener su
 *  destinatario.
 * <p>
 *  */
public abstract class Mensaje {

    Asociado asociado;

    public abstract String darMensaje();

    public Asociado getAsociado;
}
