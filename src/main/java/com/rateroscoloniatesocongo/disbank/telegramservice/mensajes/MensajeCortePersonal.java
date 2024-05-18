package com.rateroscoloniatesocongo.disbank.telegramservice.mensajes;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;

/**
 * Para enviarle el corte personal al asociado.
 */
public class MensajeCortePersonal extends Mensaje {

    static final String mensaje = "Tu corte personal del dia de hoy:\n %s \nESTE CORTE ES PRELIMINAR. EL CORTE DEFINITIVO SE DA AL FINAL DEL DIA";
    final String infoCorte;

    MensajeCortePersonal(Asociado asociado, String infoCorte) {
        this.asociado = asociado;
        this.infoCorte = infoCorte;
    }

    @Override
    public String darMensaje() {
        return String.format(mensaje, infoCorte);
    }
}
