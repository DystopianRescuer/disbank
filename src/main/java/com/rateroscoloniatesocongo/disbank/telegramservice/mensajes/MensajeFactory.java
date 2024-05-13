package com.rateroscoloniatesocongo.disbank.telegramservice.mensajes;

import java.util.Optional;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;

import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;


/**
 * Factory para crear mensajes
 *
 * Aprovechando las facilidades que nos ofrece el patron Factory, decidimos realizar la creacion de los objetos Mensaje
 * que recibe el {@link com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram} con este patron de diseño
 *
 * Existen varios tipos de mensaje que se pueden enviar al usuario, y dependiendo de los mismos se realiza una manera distinta de
 * construccion del mensaje:
 *
 * - Cobro : Para cobros con tarjeta o por terminal, dependiendo de la modalidad se envía una ID de transaccion y (Opcional)un link
 *           de cobro
 *
 * - Comandos : Para enviarle al asociado la lista de comandos que puede utilizar
 *
 * - CorteFinal : Para enviarle al asociado al final del día sus ventas realizadas y el estado final de las mismas, ya para su cobro
 *
 * - CortePersonal : Un corte rápido, meramente informativo que se le da al asociado al final de su labor de ventas
 *
 * - Estado : Para enviarle al usuario el estado de una transaccion, principalmente cuando fallan o son realizadas con exito
 *
 * - Registro : Le envia al nuevo Asociado un mensaje de exito en su registro.
 *
 * Existen tres metodos de mismo nombre pero con distintos argumentos, para acomodarse a los requisitos de cada posible Mensaje,
 * , esto para darle posible flexibilidad a futuro al código
 *
 * Todos los metodos retornan un null si no son llamados con los argumentos correctos
 *
 */
public class MensajeFactory {

    public static Mensaje nuevoMensaje(String tipo, Asociado asociado, Object contenido){

        switch(tipo){
            case "Cobro":
                try{
                    Transaccion transaccion = (Transaccion)contenido;
                    return new MensajeCobro(asociado, transaccion.getLink(), transaccion.getId());
                }catch(ClassCastException e){
                    //Se va a default
                }
            default:
                return null;
        }
    }

    public static Mensaje nuevoMensaje(String tipo, Asociado asociado){

        switch(tipo){
            case "Comandos":
                return new MensajeComandos(asociado);
            case "Registro":
                return new MensajeRegistro(asociado);
            default:
                return null;
        }
    }

    public static Mensaje nuevoMensaje(String tipo, Asociado asociado, String contenido){

        switch(tipo){
            case "CorteFinal":
                return new MensajeCorteFinal(asociado, contenido);
            case "CortePersonal":
                return new MensajeCortePersonal(asociado, contenido);
            default:
                return null;
        }

    }
}
