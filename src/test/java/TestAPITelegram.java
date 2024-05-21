import com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.VistaTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ConexionYaIniciadaException;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;
import com.rateroscoloniatesocongo.disbank.util.ConfigReader;

import org.junit.Test;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;

import java.util.Scanner;

import javax.swing.JOptionPane;

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

    @Before
    public void resetTelegram(){
        iniciarConfigReader();
        nullearTodo();
        vaciarUpdates();
        nullearTodo();
    }

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
        try{
            controladorTelegram = ControladorTelegram.getInstance();
        }catch(ErrorEnConexionException e){
            e.printStackTrace();
            Assert.fail();
        }

    }

    private void iniciarConfigReader(){
        ConfigReader.setRuta("config/config.properties");
    }

    private void vaciarUpdates(){
        iniciarVista();

        JSONArray updates= null;
        try{
            updates = VistaTelegram.recibirActualizacion(0);
        }catch(Exception e){
            e.printStackTrace();
            Assert.fail();
        }

        if(updates.length() == 0)
            return;

        JSONObject ultimoUpdate = updates.getJSONObject(updates.length()-1);

        try{
            VistaTelegram.recibirActualizacion(ultimoUpdate.getInt("update_id")+1);
        }catch(Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    private void iniciarVista(){
        try{
            VistaTelegram.setTokenBot(ConfigReader.getField("telegram.key"));
        }catch(Exception e){
            e.printStackTrace();
            Assert.fail();

        }
    }

    /**
     * Para probar si el metodo setToken funciona correctamente
     *
     */
    @Test public void TestSetTokenSuccess() {
        iniciarVista();
        String tokenBot = ConfigReader.getField("telegram.key");
        Assert.assertEquals(tokenBot, VistaTelegram.getTokenBot());
    }

    /**
     *  Para probar que el metodo setToken solo se puede usar una vez
     *  */
    @Test public void TestSetTokenAlreadyInitialized(){
        iniciarVista();
        try{
            VistaTelegram.setTokenBot("a");
            Assert.fail();
        }catch(ConexionYaIniciadaException e){
        }catch(ErrorEnConexionException e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     *  Que el metodo setToken lanza la excepcion adecuada cuando se inicializa con un token incorrecto
     *  */
    @Test public void TestSetTokenErrorEnConexion(){
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
        iniciarVista();
        System.out.println("Envia la letra a");
        JSONArray update=null;
        do{
            try{
                Thread.sleep(1000);
                update = VistaTelegram.recibirActualizacion(0);
            }catch(Exception e){
                e.printStackTrace();
                Assert.fail();
            }
        }while(update.length() == 0);
        System.out.println(update);
        String mensaje = update.getJSONObject(update.length()-1).getJSONObject("message").getString("text");
        int updateID = update.getJSONObject(update.length()-1).getInt("update_id");
        Assert.assertTrue("a".equals(mensaje));

        System.out.println("Envia la letra b");
        update=null;
        do{
            try{
                Thread.sleep(1000);
                update = VistaTelegram.recibirActualizacion(updateID+=1);
            }catch(Exception e){
                e.printStackTrace();
                Assert.fail();
            }
        }while(update.length() == 0);
        System.out.println(update);
        mensaje = update.getJSONObject(update.length()-1).getJSONObject("message").getString("text");
        Assert.assertTrue("b".equals(mensaje));

        System.out.println("Envia la letra c");
        update=null;
        do{
            try{
                Thread.sleep(1000);
                update = VistaTelegram.recibirActualizacion(updateID+=1);
            }catch(Exception e){
                e.printStackTrace();
                Assert.fail();
            }
        }while(update.length() == 0);
        System.out.println(update);
        mensaje = update.getJSONObject(update.length()-1).getJSONObject("message").getString("text");
        Assert.assertTrue("c".equals(mensaje));
    }

    /**
     *  Verifica que la funcion de offset está purgando las updates correctamente
     *  */
    @Test public void TestRecibirMensajesOffset(){
        iniciarVista();
        System.out.println("Envia un mensaje cualquiera");
        JSONArray update = null;
        do{
            try{
                Thread.sleep(1000);
                update = VistaTelegram.recibirActualizacion(0);
            }catch(Exception e){
                Assert.fail();
            }
        }while(update.length() == 0);
        System.out.println(update);
        int offset = update.getJSONObject(update.length()-1).getInt("update_id") + 1;
        try{
            update = VistaTelegram.recibirActualizacion(offset);
        }catch(Exception e){
            Assert.fail();
        }
        Assert.assertTrue(update.length() == 0);
        JOptionPane.showMessageDialog(null, "Envia tres mensajes y presiona ok");
        try{
            update = VistaTelegram.recibirActualizacion(offset);
        }catch(Exception e){
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertTrue(update.length() == 3);
    }

    /**
     *  Verifica que la funcion de enviar mensajes funciona correctamente
     *  */
    @Test public void TestEnviarMensajesOffset(){
        iniciarVista();
        System.out.println("Envia un mensaje cualquiera");
        JSONArray update = null;
        do{
            try{
                Thread.sleep(1000);
                update = VistaTelegram.recibirActualizacion(0);
            }catch(Exception e){
                Assert.fail();
            }
        }while(update.length() == 0);
        JSONObject message = update.getJSONObject(update.length()-1).getJSONObject("message");
        String echo = message.getString("text");
        int chatID = message.getJSONObject("chat").getInt("id");
        JSONObject respuesta = null;
        try{
            respuesta = new VistaTelegram(String.valueOf(chatID)).enviarMensaje(echo);
        }catch(Exception e){
            e.printStackTrace();
            Assert.fail();
        }

        Assert.assertEquals(echo, respuesta.getString("text"));
    }

    /**
     *  Verifica que no podamos crear objetos de VistaTelegram con malos chatIDs
     *  */
    @Test public void TestBadVistaTelegram(){

    }

    /**
     *  Prueba en conjunto todo lo visto previamente, y el performance del hilo daemon
     *  */
    @Test public void TestMarcoPolo() throws InterruptedException{
        setUpReal();
        for(int i=0; i<9; i++){
            System.out.println("Envia marco. Si no te llega \"polo\" verifica el codigo");
            Thread.sleep(1000);
        }
    }

}
