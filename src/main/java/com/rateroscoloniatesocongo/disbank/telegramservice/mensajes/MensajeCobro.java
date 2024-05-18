package com.rateroscoloniatesocongo.disbank.telegramservice.mensajes;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;

import java.util.Optional;

/**
 * Mensaje para notificarle al Asociado que su solicitud de cobro se ha realizado exitosamente, y notificarle el resultado
 * <p>
 * Si la transaccion por la que fue solicitada es de link de cobro, se creará el mensaje correspondiente para darle el link de cobro
 * al {@link Asociado}
 * <p>
 * Si la transaccion por la que fue solicitada fue de terminal, simplemente se le notifica la ID de la transaccion para posibles
 * aclaraciones
 */
public class MensajeCobro extends Mensaje {

    private final String idTransaccion;
    private final String link;

    private final String formatoLink = "La transaccion con el id: %s ha sido generada. \nEste es el link para cobrarla: %s";
    private final String formatoTerminal = "La transaccion con el id: %s ha sido generada. Dile a tu cliente que vaya con el cobrador y le diga el id de la misma";

    /**
     * Construye un mensaje de cobro con los siguientes datos:
     *
     * @param asociado      el asociado al que se debe enviar el mensaje con su cobro generado
     * @param link          un opcional que tiene contenido si el cobro generado es una transaccion de link de cobro, de otra manera
     *                      está vacío
     * @param idTransaccion la id unica de la transaccion
     */
    public MensajeCobro(Asociado asociado, Optional<String> link, String idTransaccion) {
        this.asociado = asociado;
        this.idTransaccion = idTransaccion;
        this.link = link.orElse(null);
    }

    @Override
    public String darMensaje() {
        if (link != null)
            return String.format(formatoLink, idTransaccion, link);

        return String.format(formatoTerminal, idTransaccion);
    }
}
