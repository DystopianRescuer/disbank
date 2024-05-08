import java.io.OutputStream;

import com.rateroscoloniatesocongo.disbank.util.ConfigReader;

import org.junit.Assert;
import org.junit.Test;
import java.io.FileOutputStream;
import java.util.Properties;
import java.io.IOException;
import java.util.Random;

/**
 * Pruebas unitarias para el {@link com.rateroscoloniatesocongo.disbank.util.ConfigReader}
 */
public class TestConfigReader {

    Random random;

    public TestConfigReader(){
        random = new Random();
    }

    private String randomString(){
        return String.valueOf(random.nextFloat());
    }
    /**
     *  Verifica que no se pueda llamar antes de inicializar el ConfigReader
     *  */
    @Test
    public void TestGetField() {
        Assert.assertTrue(ConfigReader.getField("aa") == null);
    }

    /**
     *  Verifica que se esten obteniendo los campos adecuadamente
     *  */
    @Test
    public void TestGetField2() {
        //Valores aleatorios pero guardados
        String valor1 = randomString();
        String valor2 = randomString();
        try {
            OutputStream output = new FileOutputStream("test.properties");

            Properties prop = new Properties();

            prop.setProperty("1", valor1);
            prop.setProperty("2", valor2);

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        }

        ConfigReader.setRuta("test.properties");

        Assert.assertTrue(ConfigReader.getField("1").equals(valor1));
        Assert.assertTrue(ConfigReader.getField("2").equals(valor2));
        Assert.assertTrue(ConfigReader.getField("z") == null);
    }
}
