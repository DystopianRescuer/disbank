package com.rateroscoloniatesocongo.disbank.telegramservice;

import java.util.HashMap;

/**
 * Intermediario entre la API de telegram y el resto del programa
 *
 * Funciona como una capa de abstraccion que nos permite convertir todas las
 * instrucciones enfocadas a usuario del resto del programa en los métodos necesarios
 * de la API de Telegram para comunicarnos con los mismos.
 *
 * Debido a que la clase guarda la información relacional entre los usuarios y sus chatIds,
 * necesitamos que sea una clase Singleton.
 *
 * Su constructor recibe como parámetro el token del bot que se va a utilizar durante la ejecución
 * del programa.
 *
 * Trabaja estrechamente de la mano con el GestorTransacciones, pues estos dos son los que en principio
 * forman el puente de comunicación ambivalente entre la API de Telegram y el resto de la aplicación
 *
 * Atributos:
 *
 * - tokenBot : El token del bot que se usará para enviar y recibir mensajes. Se utiliza principalmente
 * para inicializar el ambito estático de {@link VistaTelegram}
 * - chats : Un HashMap que relaciona Clientes con su respectiva VistaTelegram, para tener una forma rápida
 * de enviar mensajes a los Asociados
 * - offset : La API de telegram ofrece una manera de recibir Updates sin repeticiones que se basa en esta
 * variable, inicializa en 0 y se va actualizando conforme el controlador recibe actualizaciones
 * - gestorTransacciones : El controlador guarda una referencia al gestor de transacciones, pues necesitamos
 * una comunicacion ambivalente entre ambos.
 *
 */
public class ControladorTelegram {

    private static ControladorTelegram instance;

    private final String tokenBot;
    private HashMap<Asociado, VistaTelegram> chats;
    private int offset;
    private GestorTransacciones gestorTransacciones;

    /** Singleton */
    private ControladorTelegram(String tokenBot){
        this.tokenBot = tokenBot;
        VistaTelegram.setTokenBot(tokenBot);
        chats = new HashMap<>();
        offset = 0;
    }

    /**
     *  Obtiene la instancia singleton del controlador. La crea con el token del config si no existe
     *
     *  @return la instancia singleton del controlador
     *  */
    public static ControladorTelegram getInstance(){
        if(instance != null)
            return instance;

        instance = new ControladorTelegram(ConfigReader.getField(telegram.token));

        return instance;
    }
}