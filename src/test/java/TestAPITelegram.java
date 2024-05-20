import com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.VistaTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ConexionYaIniciadaException;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;
import com.rateroscoloniatesocongo.disbank.util.ConfigReader;

import org.junit.Test;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;


import java.util.Scanner;

/**
 * Pruebas unitarias para los metodos de la {@link com.rateroscoloniatesocongo.disbank.telegramservice.VistaTelegram}
 * y ControladorTelegram
 * <p>
 * 1. TestSetTokenBot
 * 2. TestRecibirActualizacion
 * 3.
 */
public class TestAPITelegram {

    VistaTelegram vistaTelegram;
    ControladorTelegram controladorTelegram;

    private void nullearTodo(){
        vistaTelegram = null;
        controladorTelegram = null;
        ControladorTelegram.matarInstancia();
        VistaTelegram.resetStatico();
    }

    /**
     * Inicializa ControladorTelegram para cada prueba
     * <p>
     *  La vista ejemplo se construye con el numero guardado en NumeroTest.txt
     *  Y el ControladorTelegram se construye con el token de la configuracion
     *  de la aplicacion
     *  */
    private void setUpReal() {
        nullearTodo();

        ConfigReader.setRuta("config/config.properties");
        try{
            controladorTelegram = ControladorTelegram.getInstance();
        }catch(ErrorEnConexionException e){
            Assert.fail();
        }

    }

    private void iniciarConfigReader(){
        ConfigReader.setRuta("config/config.properties");
    }

    private void vaciarUpdates(){
        iniciarConfigReader();
        try{
            VistaTelegram.setTokenBot(ConfigReader.getField("telegram.key"));
        }catch(Exception e){
            Assert.fail();
        }

        JSONArray updates= null;
        try{
            updates = VistaTelegram.recibirActualizacion(0);
        }catch(Exception e){
            Assert.fail();
        }

        JSONObject ultimoUpdate = updates.getJSONObject(updates.length()-1);

        try{
            VistaTelegram.recibirActualizacion(ultimoUpdate.getInt("update_id"));
        }catch(Exception e){
            Assert.fail();
        }
    }

    /**
     * Para probar si el metodo setToken funciona correctamente
     *
     */
    @Test public void TestSetTokenSuccess() {
        iniciarConfigReader();
        nullearTodo();
        try{
            VistaTelegram.setTokenBot(ConfigReader.getField("telegram.key"));
        }catch(Exception e){
            Assert.fail();

        }
        String tokenBot = ConfigReader.getField("telegram.key");
        Assert.assertEquals(tokenBot, VistaTelegram.getTokenBot());
    }

    /**
     *  Para probar que el metodo setToken solo se puede usar una vez
     *  */
    @Test public void TestSetTokenAlreadyInitialized(){
        iniciarConfigReader();
        nullearTodo();
        TestSetTokenSuccess();
        try{
            VistaTelegram.setTokenBot("a");
            Assert.fail();
        }catch(ConexionYaIniciadaException e){
        }catch(ErrorEnConexionException e){
            Assert.fail();
        }
    }

    /**
     *  Que el metodo setToken lanza la excepcion adecuada cuando se inicializa con un token incorrecto
     *  */
    @Test public void TestSetTokenErrorEnConexion(){
        nullearTodo();
        try{
            VistaTelegram.setTokenBot("a");
            Assert.fail();
        }catch(Exception e){
            Assert.assertSame(e.getClass(), ErrorEnConexionException.class);
        }
    }
    /**
     *  Verifica que la conexion con el bot es correcta, verificando si el getMe es el adecuado
     *  */
    @Test public void TestGetMe() {
        setUpReal();
        //Verifica que la conexion con el bot es correcta
        JSONObject getMeInterno = new JSONObject("{\"ok\": true,\"result\": {\"id\": 7119940309,\"is_bot\": true,\"first_name\": \"RateritoDeTesocongo\",\"username\": \"DisBankBot\",\"can_join_groups\": true,\"can_read_all_group_messages\": false,\"supports_inline_queries\":false,\"can_connect_to_business\": false}}");
        Assert.assertEquals(getMeInterno.toString(), controladorTelegram.getGetMe().toString());
    }

    /**
     *  Verifica que la conexion con el bot funciona para recibir mensajes
     *  */
    @Test public void TestRecibirMensajes(){
        Scanner standby = new Scanner(System.in);
        iniciarConfigReader();
        nullearTodo();
        vaciarUpdates();
        System.out.println("Envia la letra a");
        standby.next();
        JSONArray update=null;
        try{
            update = VistaTelegram.recibirActualizacion(0);
        }catch(Exception e){
            Assert.fail();
        }
        System.out.println(update);
        String mensaje = update.getJSONObject(update.length()-1).getJSONObject("message").getString("text");
        Assert.assertTrue("a".equals(mensaje));

        System.out.println("Envia la letra b");
        standby.next();
        update=null;
        try{
            update = VistaTelegram.recibirActualizacion(0);
        }catch(Exception e){
            Assert.fail();
        }
        System.out.println(update);
        mensaje = update.getJSONObject(update.length()-1).getJSONObject("message").getString("text");
        Assert.assertTrue("b".equals(mensaje));

        System.out.println("Envia la letra c");
        standby.next();
        update=null;
        try{
            update = VistaTelegram.recibirActualizacion(0);
        }catch(Exception e){
            Assert.fail();
        }
        System.out.println(update);
        mensaje = update.getJSONObject(update.length()-1).getJSONObject("message").getString("text");
        Assert.assertTrue("c".equals(mensaje));
    }

    /**
     *  Verifica que la funcion de offset está purgando las updates correctamente
     *  */
    @Test public void TestRecibirMensajesOffset(){
        //Pendiente
        //Enviar un mensaje y
        //Probar que colocando el offset correctamente, la siguiente vez que pidamos una update esta está vacia
        //luego, mandar tres mensajes seguidos y verificar que se capture un arreglo de updates de dicha longitud
    }

    /**
     *  Verifica que la funcion de enviar mensajes funciona correctamente
     *  */
    @Test public void TestEnviarMensajesOffset(){
        //Pendiente
        //Pedir que se envie un mensaje de contenido aleatorio, capturarlo con getUpdate e intentar enviar un mensaje de vuelta
        //con el mismo contenido del recibido. Verificar integridad del JSONObject que genera enviarMensaje()
    }

    /**
     *  Verifica que no podamos crear objetos de VistaTelegram con malos chatIDs
     *  */
    @Test public void TestBadVistaTelegram(){

    }


}
