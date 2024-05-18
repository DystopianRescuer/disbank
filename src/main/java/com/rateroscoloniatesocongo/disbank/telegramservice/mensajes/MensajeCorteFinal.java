package com.rateroscoloniatesocongo.disbank.telegramservice.mensajes;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;

/**
 * El mensaje para el corte final del día
 */
public class MensajeCorteFinal extends Mensaje {

    static final String mensaje = "Tu corte final del dia de hoy:\n %s";
    final String infoCorte;

    MensajeCorteFinal(Asociado asociado, String infoCorte) {
        this.asociado = asociado;
        this.infoCorte = infoCorte;
    }

    @Override
    public String darMensaje() {
        return String.format(mensaje, infoCorte);
    }
}
