package com.rateroscoloniatesocongo.disbank.telegramservice.mensajes;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;

/**
 * Un mensaje de los comandos disponibles para el usuario. Se ve de la siguiente manera:
 *
 * Comandos disponibles:
 * - Iniciar ventas : Para iniciar tu dia de ventas y comenzar a generar tus solicitudes de cobro
 * - Cerrar ventas : Cuando termines tu dia de ventas, usa este comando para solicitar tu corte personal y ver tus ventas realizadas
 * - Comandos : Para abrir este menu
 * - Necesito ayuda : Dile a un representante de Disbank que necesitas ayuda personalizada. Vendrá a ayudarte
 * - Cobrar $n.nn con ... : Genera un nuevo cobro con la cantidad designada y la modalidad dada. Las modalidades disponibles por ahora son terminal y link
 *
 */
public class MensajeComandos extends Mensaje{

    MensajeComandos(Asociado asociado){
        this.asociado = asociado;
    }

    @Override
    public String darMensaje() {
        return String.format("%s\n %s\n %s\n %s\n %s\n %s\n",
                             "Comandos disponibles:",
                             "-Iniciar ventas : Para iniciar tu dia de ventas y comenzar a generar tus solicitudes de cobro",
                             "- Cerrar ventas : Cuando termines tu dia de ventas, usa este comando para solicitar tu corte personal y ver tus ventas realizadas",
                             "- Comandos : Para abrir este menu",
                             "- Necesito ayuda : Dile a un representante de Disbank que necesitas ayuda personalizada. Vendrá a ayudarte",
                             "- Cobrar $n.nn con ... : Genera un nuevo cobro con la cantidad designada y la modalidad dada. Las modalidades disponibles por ahora son terminal y link");
    }
}
