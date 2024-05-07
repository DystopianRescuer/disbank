import com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.VistaTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ConexionYaIniciadaException;
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
    public TestAPITelegram(){
        controladorTelegram = ControladorTelegram.getInstance();
    }

    /**
     * Para probar si el metodo setToken solo se puede usar una vez
     */
    @Test public void TestSetToken(){
        String tokenBot = VistaTelegram.getTokenBot();
        try{
            VistaTelegram.setTokenBot("a");
        }catch(Exception e){
            Assert.assertTrue(e.getClass() == ConexionYaIniciadaException.class);
        }
        Assert.assertEquals(tokenBot, VistaTelegram.getTokenBot());

    }
}
