package com.rateroscoloniatesocongo.disbank;

import com.rateroscoloniatesocongo.disbank.transacciones.GestorTransacciones;
import com.rateroscoloniatesocongo.disbank.util.ConfigReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DisBank extends Application {

    /**
     * Ruta del archivo de configuración
     */
    public static final String RUTA_CONFIG = "config/config.properties";

    /**
     * Método main del programa
     * Se estabale la ruta de configuración
     * Se iniciar el gestor de transacciones
     * Se lanza el dashboard
     * @param args argumentos del programa (ignorados)
     */
    public static void main(String[] args) {
        ConfigReader.setRuta(RUTA_CONFIG);
        GestorTransacciones.getInstance().iniciar();
        launch();
    }

    /**
     * Método start de JavaFX
     * @param stage el escenario donde se refleja el dashboard
     * @throws IOException si hay problemas al leer el FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("panelcontrol.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Disbank - Panel de Control");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(-1);
    }
}
