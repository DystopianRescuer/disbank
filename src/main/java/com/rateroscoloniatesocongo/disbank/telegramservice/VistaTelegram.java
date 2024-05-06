package com.rateroscoloniatesocongo.disbank.telegramservice;

import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ConexionYaIniciadaException;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;


/**
 * Clase encargada de la parte de vista del servicio del bot de Telegram
 *
 * Cada chat activo con un asociado y el bot de telegram deberá tener una instancia de esta clase activa, la cual funcionará como intercambio inicial de información entre los chats
 * y la aplicación.
 *
 * Posee los siguientes atributos:
 * - tokenBot :  El token para establecer la comunicación con el bot de telegram. Es una variable estática que deberá setearse una vez al inicio del programa, y no
 *               debe cambiar durante la ejecución del mismo.
 * - chatId   :  Cada instancia de esta clase tendrá asociado un chatId al cual enviará mensajes con el metodo enviarMensaje(). Tampoco debe cambiar durante la vida de la instancia,
 *               por lo que, también es una variable final.
 *
 * De metodos, es una clase relativamente simple, pues su responsabilidad no es otra que enviar y recibir mensajes al bot de telegram. Y justamente, los métodos que existen son
 * para realizar este cometido
 *
 */
public class VistaTelegram {

    public static final String protocoloHTTP = "POST";
    public static final String tipoDeRequest = "application/json";

    private static String tokenBot;
    private final String chatId;

    /**
     *  Crea una instancia asociada a un chat especificado por chatId.
     *  La instancia creada no cambiará de chat a lo largo de la ejecución.
     *
     *  @param chatId el id del chat asociado a la instancia creada
     *  */
    public VistaTelegram(String chatId){
        this.chatId = chatId;
    }

    /**
     *  Ajusta el tokenBot al token especificado y prueba la conexión inicial
     *
     *  @param token  el token del bot al que nos vamos a conectar
     *
     *  @throws ConexionYaIniciadaException si se intenta cambiar de token en tiempo de ejecución. Esto no deberia
     *          de ser posible, pues nos interesa tener uno y solo un bot de telegram para nuestro programa
     *  @throws ErrorEnConexionException si algo malo ocurre durante el inicio de la conexion inicial
     *
     *  @return un getMe del bot, para verificar que la conexion se puede iniciar de manera correcta y que es con el bot
     *          buscado.
     *  */
    public static JSONObject setTokenBot(String token) throws ConexionYaIniciadaException, ErrorEnConexionException{
        if(tokenBot != null)
            throw new ConexionYaIniciadaException();

        tokenBot = token;
        URL url;
        HttpURLConnection conexion;
        JSONObject answer;
        try{
            url = (new URI("https://api.telegram.org/"+tokenBot+"/getMe")).toURL();
            conexion = (HttpURLConnection)url.openConnection();
            conexion.setRequestMethod(protocoloHTTP);
            conexion.setRequestProperty("Content-Type", tipoDeRequest);
            int respuesta = conexion.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));

            String line;
            StringBuilder response = new StringBuilder();

            while((line = reader.readLine()) != null){
                response.append(line);
            }
            reader.close();

            answer = new JSONObject(response.toString());
        }catch(Exception e){
            throw new ErrorEnConexionException(e.getMessage());
        }

        return answer;

    }

    /**
     *  Recibe un arreglo de Updates del bot de telegram segun el offset dado
     *
     *  Es estatico porque no necesitamos que todas las instancias posean este metodo de manera particular
     *  Está configurado para utilizar la feature de long polling que nos ofrece la API de telegram (porque no se
     *  usar webhooks pero pues hay que ser un poquito buena onda con el spam a las APIs)
     *
     *  @param offset   El offset con el que ejecutaremos el metodo de getUpdates del bot. Este parametro debe ser recalculado en
     *                  cada llamada, para evitar recibir Updates duplicadas en cada llamada
     *
     *  @return un arreglo de objetos Update con todos los datos que
     *  */
    public static JSONArray recibirActualizacion(int offset){

    }

}