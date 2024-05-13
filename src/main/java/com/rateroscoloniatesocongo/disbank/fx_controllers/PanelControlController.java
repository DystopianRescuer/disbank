package com.rateroscoloniatesocongo.disbank.fx_controllers;

import com.rateroscoloniatesocongo.disbank.transacciones.GestorTransacciones;
import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;
import com.rateroscoloniatesocongo.disbank.util.Avisador;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class PanelControlController {

    @FXML
    public Button botonCorte;
    @FXML
    public TableView<Transaccion> tablaTransacciones;
    @FXML
    public VBox panelRegistroAsociado, panelListaAsociados, panelInfo;
    @FXML
    public TextField searchBar;
    @FXML
    public TextField nombre, clabe, telegramUser, nombreComercio, banco;
    @FXML
    public VBox registrandoUsuario;


    @FXML
    public void onInfoButtonClick(ActionEvent actionEvent) {
        panelInfo.toFront();
    }

    @FXML
    public void onRegistroAsociadoClick(ActionEvent actionEvent) {
        panelRegistroAsociado.toFront();
    }

    @FXML
    public void onListaAsociadosClick(ActionEvent actionEvent) {
        panelListaAsociados.toFront();
    }

    @FXML
    public void onSearchBarUpdate(ActionEvent actionEvent) {
        if(estaEnBlanco(searchBar)) {
            // Pone el normal
        } else {
            // Pone los resultados de búsqueda
        }
    }

    @FXML
    public void onCorteRequest(ActionEvent actionEvent) {
        // Crea un cortador y lo pone a chambear
        botonCorte.setDisable(true);
        GestorTransacciones.getInstance().detener();
        Avisador.mandarAviso("Corte de día realizado.");
    }

    @FXML
    public void onRegistrarUsuarioAttempt(ActionEvent actionEvent) {
        if(estaEnBlanco(nombre) || estaEnBlanco(clabe) || estaEnBlanco(banco) || estaEnBlanco(nombreComercio) || estaEnBlanco(telegramUser)) {
            Avisador.mandarAviso("Debes llenar todos los campos");
            return;
        }
        registrandoUsuario.setVisible(true);
    }

    private boolean estaEnBlanco(TextField textField) {
        return textField == null || textField.getText().isBlank();
    }

}
