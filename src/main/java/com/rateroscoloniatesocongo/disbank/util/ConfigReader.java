package com.rateroscoloniatesocongo.disbank.util;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class ConfigReader {

    private static ConfigReader instance;

    private Properties config;
    private ConfigReader() {
        this.config = new Properties();
        InputStream archivo;

        // Carga el archivo de configutación
        try {
            // TODO ruta de config
            archivo = new FileInputStream("");
            config.load(archivo);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No se encontró el archivo de configuración");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigReader getInstance() {
        if(instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }


    public String getField(String key) {
        return this.config.getProperty(key);
    }
}
