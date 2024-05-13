package com.rateroscoloniatesocongo.disbank.telegramservice.mensajes;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;

import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;


/**
 * Factory para crear mensajes
 * <p>
 * Aprovechando las facilidades que nos ofrece el patron Factory, decidimos realizar la creacion de los objetos Mensaje
 * que recibe el {@link com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram} con este patron de diseño
 * <p>
 * Existen varios tipos de mensaje que se pueden enviar al usuario, y dependiendo de los mismos se realiza una manera distinta de
 * construccion del mensaje:
 * <p>
 * - Cobro : Para cobros con tarjeta o por terminal, dependiendo de la modalidad se envía una ID de transaccion y (Opcional)un link
 *           de cobro
 * <p>
 * - Comandos : Para enviarle al asociado la lista de comandos que puede utilizar
 * <p>
 * - CorteFinal : Para enviarle al asociado al final del día sus ventas realizadas y el estado final de las mismas, ya para su cobro
 * <p>
 * - CortePersonal : Un corte rápido, meramente informativo que se le da al asociado al final de su labor de ventas
 * <p>
 * - Estado : Para enviarle al usuario el estado de una transaccion, principalmente cuando fallan o son realizadas con exito
 * <p>
 * - Registro : Le envia al nuevo Asociado un mensaje de exito en su registro.
 * <p>
 * Existen tres metodos de mismo nombre pero con distintos argumentos, para acomodarse a los requisitos de cada posible Mensaje,
 * , esto para darle posible flexibilidad a futuro al código
 * <p>
 * Todos los metodos retornan un null si no son llamados con los argumentos correctos
 *
 */
public class MensajeFactory {

    public static Mensaje nuevoMensaje(String tipo, Asociado asociado, Object contenido){

        if (tipo.equals("Cobro")) {
            try {
                Transaccion transaccion = (Transaccion) contenido;
                return new MensajeCobro(asociado, transaccion.getLink(), transaccion.getId());
            } catch (ClassCastException e) {
                //Se va a default
            }
        }
        return null;
    }

    public static Mensaje nuevoMensaje(String tipo, Asociado asociado){

        return switch (tipo) {
            case "Comandos" -> new MensajeComandos(asociado);
            case "Registro" -> new MensajeRegistro(asociado);
            default -> null;
        };
    }

    public static Mensaje nuevoMensaje(String tipo, Asociado asociado, String contenido){

        return switch (tipo) {
            case "CorteFinal" -> new MensajeCorteFinal(asociado, contenido);
            case "CortePersonal" -> new MensajeCortePersonal(asociado, contenido);
            default -> null;
        };

    }
}
