package com.rateroscoloniatesocongo.disbank.util;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Properties;

public class ConfigReader {

    private static String ruta;

    public static void setRuta(String ruta) {
        ConfigReader.ruta = ruta;
    }

    public static String getField(String key) {
        if(ruta != null) {
            return cargarConfig(ruta).getProperty(key);
        }
        return null;
    }

    private static Properties cargarConfig(String ruta) {
        Properties config = new Properties();
        InputStream archivo;

        // Carga el archivo de configutación
        try {
            archivo = new FileInputStream(ruta);
            config.load(archivo);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No se encontró el archivo de configuración");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return config;
    }
}
