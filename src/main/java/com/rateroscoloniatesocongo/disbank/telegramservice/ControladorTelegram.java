package com.rateroscoloniatesocongo.disbank.telegramservice;

import com.rateroscoloniatesocongo.disbank.bd.BaseDatos;
import com.rateroscoloniatesocongo.disbank.clipservice.excepciones.TransaccionNoRegistradaException;
import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.modelo.Cortador;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ConexionYaIniciadaException;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.SolicitudNoEncontradaException;
import com.rateroscoloniatesocongo.disbank.telegramservice.mensajes.Mensaje;
import com.rateroscoloniatesocongo.disbank.telegramservice.mensajes.MensajeFactory;
import com.rateroscoloniatesocongo.disbank.transacciones.CobroFactory;
import com.rateroscoloniatesocongo.disbank.transacciones.GestorTransacciones;
import com.rateroscoloniatesocongo.disbank.util.Avisador;
import com.rateroscoloniatesocongo.disbank.util.ConfigReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Intermediario entre la API de telegram y el resto del programa
 * <p>
 * Funciona como una capa de abstraccion que nos permite convertir todas las
 * instrucciones enfocadas a usuario del resto del programa en los métodos necesarios
 * de la API de Telegram para comunicarnos con los mismos.
 * <p>
 * Es asi mismo el que inicializa la parte estática de {@link VistaTelegram} y verifica que todo haya salido bien ahí
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
 * - chats : Un HashMap que relaciona Asociados con su respectiva VistaTelegram, para tener una forma rápida
 * de enviar mensajes a los Asociados desde partes del programa donde no nos interesa el chatID (practicamente el resto del programa)
 * - chatIds : Otra relacion, ahora entre las chatIDs y los Asociados, nos sirve para que el hilo daemon sepa de quién proviene cada
 * update que reciba, de manera rápida
 * - offset : La API de telegram ofrece una manera de recibir Updates sin repeticiones que se basa en esta
 * variable, inicializa en 0 y se va actualizando conforme el controlador recibe actualizaciones
 * - gestorTransacciones : El controlador guarda una referencia al gestor de transacciones, pues necesitamos
 * una comunicacion ambivalente entre ambos.
 * - daemon : El hilo daemon encargado de regularmente conseguir las updates de la API de telegram. Para motivos de debug solamente,
 * tiene su getter.
 * <p>
 * Asi mismo, tambien tiene su coleccion de mensajes rapidos como Strings finales, los cuales no sirven para otra cosa más que para
 * que el {@link DaemonTelegram} pueda usarlos en esos casos en los que, por la complejidad de la respuesta, no es necesario
 * crear un objeto Mensaje
 */
public class ControladorTelegram {

    private static ControladorTelegram instance;

    public final JSONObject getMe;
    private final HashMap<Asociado, VistaTelegram> chats;
    private final HashMap<String, Asociado> chatIds;
    private int offset;
    private final Thread daemon;

    //Mensajes rapidos
    public static final String noRegistroPendiente = "No te encuentras registrado como asociado, ve con el administrador de Disbank para más detalles";
    public static final String instruccionNoReconocida = "La instruccion enviada no es reconocida, escribe Comandos para la lista de comandos";

    /**
     * Singleton
     */
    private ControladorTelegram(String tokenBot) throws ConexionYaIniciadaException, ErrorEnConexionException {
        getMe = VistaTelegram.setTokenBot(tokenBot);
        chats = new HashMap<>();
        offset = 0;
        chatIds = new HashMap<>();
        daemon = new DaemonTelegram(this);
        daemon.setDaemon(true);
        daemon.start();
    }

    //Metodos para el resto del backend

    /**
     * Obtiene la instancia singleton del controlador. La crea con el token del ConfigReader si no existe
     * <p>
     * Metodo para el backend
     *
     * @return la instancia singleton del controlador
     * @throws ErrorEnConexionException cuando existe un error en la conexion a Telegram
     */
    public static ControladorTelegram getInstance() throws ErrorEnConexionException {
        if (instance != null) return instance;

        try {
            instance = new ControladorTelegram(ConfigReader.getField("telegram.key"));
        } catch (ConexionYaIniciadaException e) {
            Avisador.mandarErrorFatal(e.getMessage());
        }

        return instance;
    }

    /**
     * Envia un mensaje con las especificaciones dadas desde el objeto Mensaje
     * <p>
     * En situaciones optimas, este es el unico metodo necesario para enviar mensajes a los asociados desde cualquier parte
     * del programa, excepto en algunos casos donde no fue considerado conveniente por la obvia inmediatez de algunos eventos
     * cuyos mensajes se pueden manejar ahi mismo (hay algunos ejemplos en {@link DaemonTelegram})
     * <p>
     * Metodo para el backend y para el mismo controlador
     *
     * @throws ErrorEnConexionException si existe un error en la conexion a Telegram
     */
    public void enviarMensaje(Mensaje mensaje) throws ErrorEnConexionException {
        VistaTelegram enviador = buscarVistaTelegram(mensaje.getAsociado());
        enviador.enviarMensaje(mensaje.darMensaje());
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

    //Metodos debug

    public Thread getDaemon() {
        return daemon;
    }

    public static void matarInstancia(){
        if(instance != null){
            instance.daemon.interrupt();
            ControladorTelegram.instance = null;
        }
    }

    //Metodos para el hilo daemon

    /**
     * Genera una nueva transaccion con los detalles dados y la envía al GestorTransacciones para esperar su respuesta
     *
     * @param cobro       cantidad monetaria a cobrar
     * @param tipoDeCobro tipo de cobro a pedir (terminal o link)
     * @param asociado    el asociado al cual está vinculado el cobro (de quien proviene la solicitud)
     */
    protected void generarNuevaTransaccion(double cobro, String tipoDeCobro, Asociado asociado) {
        try {
            GestorTransacciones.getInstance().nuevaTransaccion(asociado, CobroFactory.generaCobro(tipoDeCobro, cobro));
        } catch (TransaccionNoRegistradaException e) {
            try {
                buscarVistaTelegram(asociado).enviarMensaje("No se pudo registrar la transacción");
            } catch (ErrorEnConexionException e1) {
                Avisador.mandarErrorFatal(e1.getMessage());
            }
        }
    }

    /**
     * Registra una nueva interaccion con un chatID que no se encuentra registrado en los chats actuales.
     * <p>
     * Saca el asociado vinculado a dicho chatID y le crea una VistaTelegram para el resto de la ejecución, hasta su corte
     * <p>
     */
    protected void registrarNuevaInteraccion(String chatID) {
        Asociado asociado = BaseDatos.buscarPorChatId(chatID);

        if (asociado != null) {
            chats.put(asociado, new VistaTelegram(chatID));
            chatIds.put(chatID, asociado);
        }
    }


    /**
     * Realiza el corte personal del asociado registrado al chatID.
     * <p>
     * Esto solamente significa, hasta el momento, que dejamos de guardar su VistaTelegram, por motivos de optimizacion
     */
    protected void cortePersonal(Asociado asociado) {
        Cortador cortador = new Cortador();
        cortador.cortePersonal(asociado);
        chats.remove(asociado);
        chatIds.remove(asociado.getChatId());
    }

    /**
     * Le da la lista de comandos al usuario que la solicita
     *
     * @param asociado que la solicita
     */
    protected void darComandos(Asociado asociado) {
        try {
            Mensaje mensaje = MensajeFactory.nuevoMensaje("Comandos", asociado);
            System.out.println(mensaje.getAsociado());
            System.out.println(mensaje.darMensaje());
            enviarMensaje(mensaje);
        } catch (ErrorEnConexionException e) {
            e.printStackTrace();
            Avisador.mandarErrorFatal(e.getMessage());
        }
    }

    /**
     * Manda solicitud de ayuda del chatID que la solicita
     *
     * @param asociado que la solicita
     */
    protected void pedirAyuda(Asociado asociado) {
        asociado.recibiendoAyuda = true;
        //TODO: Implementar chat personal con el admin
    }

    protected void mensajeAyuda(Asociado asociado, String mensaje) {
        //TODO: IDEM
        try {
            buscarVistaTelegram(asociado).enviarMensaje(mensaje);
        } catch (ErrorEnConexionException e) {
            Avisador.mandarErrorFatal(e.getMessage());
        }
    }

    /**
     * Retorna un arreglo JSON con las actualizaciones recibidas del bot desde la ultima llamada al metodo
     * <p>
     * Está destinado para ser usado desde el hilo daemon encargado del controlador.
     *
     * @return ArregloJSON con las actualizaciones recibidas del bot desde la ultima llamada al metodo
     * @throws ErrorEnConexionException si no se pudo establecer la conexión adecuadamente
     */
    protected JSONArray getUpdates() throws ErrorEnConexionException {
        JSONArray respuesta;
        respuesta = VistaTelegram.recibirActualizacion(offset);
        if (respuesta.isEmpty()) return null;
        JSONObject ultimoRecibido = respuesta.getJSONObject(respuesta.length() - 1);
        offset = ultimoRecibido.getInt("update_id") + 1;

        return respuesta;
    }

    /**
     * Busca el asociado vinculado a ese chatID. Necesitamos que el hilo daemon acceda al resto de metodos con esta información
     * <p>
     * Está destinado para ser usado desde el hilo daemon del controlador
     *
     * @return Asociado de la lista de chats que está vinculado a ese chatID
     */
    protected Asociado buscarAsociado(String chatID) {
        return chatIds.get(chatID);
    }

    protected void registrarNuevoAsociado(String chatID) throws SolicitudNoEncontradaException {
        BaseDatos.setChatId(BaseDatos.getAsociadoPendiente(), chatID);
        BaseDatos.setAsociadoPendiente(-1);
    }

    //Metodos privados

    /**
     * Retorna la vista telegram vinculada al asociado.
     *
     * @param asociado El asociado a usar como criterio de busqueda
     * @return VistaTelegram de la lista de chats que está vinculado a ese Asociado
     */
    private VistaTelegram buscarVistaTelegram(Asociado asociado) {
        return chats.get(asociado);
    }
}
