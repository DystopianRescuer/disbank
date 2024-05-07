package com.rateroscoloniatesocongo.disbank.telegramservice;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ConexionYaIniciadaException;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;
import com.rateroscoloniatesocongo.disbank.transacciones.GestorTransacciones;
import com.rateroscoloniatesocongo.disbank.util.ConfigReader;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Intermediario entre la API de telegram y el resto del programa
 * <p>
 * Funciona como una capa de abstraccion que nos permite convertir todas las
 * instrucciones enfocadas a usuario del resto del programa en los métodos necesarios
 * de la API de Telegram para comunicarnos con los mismos.
 * <p>
 * Debido a que la clase guarda la información relacional entre los usuarios y sus chatIds,
 * necesitamos que sea una clase Singleton.
 * <p>
 * Su constructor recibe como parámetro el token del bot que se va a utilizar durante la ejecución
 * del programa.
 * <p>
 * Trabaja estrechamente de la mano con el GestorTransacciones, pues estos dos son los que en principio
 * forman el puente de comunicación ambivalente entre la API de Telegram y el resto de la aplicación
 * <p>
 * Atributos:
 * <p>
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

    public final JSONObject getMe;
    private final String tokenBot;
    private HashMap<Asociado, VistaTelegram> chats;
    private int offset;
    private GestorTransacciones gestorTransacciones;

    /** Singleton */
    private ControladorTelegram(String tokenBot) throws ConexionYaIniciadaException, ErrorEnConexionException {
        this.tokenBot = tokenBot;
        getMe = VistaTelegram.setTokenBot(tokenBot);
        chats = new HashMap<>();
        offset = 0;
    }

    /**
     *  Obtiene la instancia singleton del controlador. La crea con el token del ConfigReader si no existe
     *
     *  @return la instancia singleton del controlador
     *
     *  @throws ErrorEnConexionException cuando existe un error en la conexion a Telegram
     *  */
    public static ControladorTelegram getInstance() throws ErrorEnConexionException{
        if(instance != null)
            return instance;
      
        try{
            instance = new ControladorTelegram(ConfigReader.getField("telegram.token"));
        }catch(ConexionYaIniciadaException e){
            //No hacer nada, no puede pasar
        }catch(ErrorEnConexionException e){
            throw e;
        }

        return instance;
    }

    /**
     * Da un objeto JSON con la informacion del bot al que estamos
     * conectados. Es el getMe de la info de Telegram
     *
     * @return objeto JSON con info del bot de Telegram
     */
    public JSONObject getGetMe() {
        return getMe;

    }
}
