package com.rateroscoloniatesocongo.disbank.telegramservice.mensajes;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;

/**
 * Mensaje para confirmarle el registro a un nuevo asociado.
 */
public class MensajeRegistro extends Mensaje {

    static final String mensaje = "Tu registro ha sido efectuado exitosamente. Tu id de asociado es: %d";

    MensajeRegistro(Asociado asociado) {
        this.asociado = asociado;
    }

    @Override
    public String darMensaje() {
        return String.format(mensaje, asociado.getId());
    }
}
