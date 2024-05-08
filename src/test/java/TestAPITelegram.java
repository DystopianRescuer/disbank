import com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.VistaTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ConexionYaIniciadaException;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;
import com.rateroscoloniatesocongo.disbank.util.ConfigReader;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 *  Pruebas unitarias para los metodos de la {@link com.rateroscoloniatesocongo.disbank.telegramservice.VistaTelegram}
 *  y ControladorTelegram
 *
 *  1. TestSetTokenBot
 *  2. TestRecibirActualizacion
 *  3.
 *  */
public class TestAPITelegram {

    VistaTelegram vistaTelegram;
    ControladorTelegram controladorTelegram;

    /**
     *  Inicializa ControladorTelegram para cada prueba
     *
     *  La vista ejemplo se construye con el numero guardado en NumeroTest.txt
     *  Y el ControladorTelegram se construye con el token de la configuracion
     *  de la aplicacion
     *  */
    public TestAPITelegram() throws ConexionYaIniciadaException, ErrorEnConexionException {
        ConfigReader.setRuta("config.properties");
        controladorTelegram = ControladorTelegram.getInstance();

    }

    /**
     * Para probar si el metodo setToken solo se puede usar una vez
     */
    @Test public void TestSetToken() {
        //Verifica que no se puede cambiar el token en tiempo de ejecucion
        String tokenBot = VistaTelegram.getTokenBot();
        try{
            VistaTelegram.setTokenBot("a");
        }catch(Exception e){
            Assert.assertTrue(e.getClass() == ConexionYaIniciadaException.class);
        }
        Assert.assertEquals(tokenBot, VistaTelegram.getTokenBot());
    }

    @Test public void TestGetMe() {
        //Verifica que la conexion con el bot es correcta
        JSONObject getMeInterno = new JSONObject("{ \"ok\": true, \"result\": {\"id\": 7119940309, \"is_bot\": true, \"first_name\": \"RateritoDeTesocongo\", \"username\": \"DisBankBot\", \"can_join_groups\": true, \"can_read_all_group_messages\": false, \"supports_inline_queries\": false, \"can_connect_to_business\": false} ");
        Assert.assertTrue(getMeInterno.equals(controladorTelegram.getGetMe()));
    }
}
