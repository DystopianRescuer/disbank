package com.rateroscoloniatesocongo.disbank.telegramservice.mensajes;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion.Estado;

/**
 * Le da el estado de una transaccion al cliente, para esto se construye con la ID de la transaccion y el estado de la misma
 */
public class MensajeEstado extends Mensaje{

    private final String mensaje;

    public static final String transaccionExitosa = "La transaccion con id %s ha sido efectuada exitosamente";
    public static final String transaccionPendiente = "La transaccion con id %s sigue pendiente";
    public static final String transaccionFallida = "La transaccion con id %s ha fallado. Contacta a un cobrador para más detalles.";


    MensajeEstado(Asociado asociado, String idTransaccion, Estado estadoTransaccion){
        this.asociado = asociado;
        String tmp = switch (estadoTransaccion) {
            case PENDIENTE -> transaccionPendiente;
            case PAGADA -> transaccionExitosa;
            case FALLIDA -> transaccionFallida;
        };
        this.mensaje = String.format(tmp, idTransaccion);
    }

    @Override
    public String darMensaje() {
        return mensaje;
    }
}
