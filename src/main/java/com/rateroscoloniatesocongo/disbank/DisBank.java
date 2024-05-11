package com.rateroscoloniatesocongo.disbank;

import com.rateroscoloniatesocongo.disbank.util.ConfigReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DisBank extends Application {
    public static final String RUTA_CONFIG = "config/config.properties";

    public static void main(String[] args) {
        ConfigReader.setRuta(RUTA_CONFIG);
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("panelcontrol.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Disbank - Panel de Control");
        stage.setScene(scene);
        stage.show();
    }
}
