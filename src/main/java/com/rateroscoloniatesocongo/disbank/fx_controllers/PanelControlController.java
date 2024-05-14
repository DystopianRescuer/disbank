package com.rateroscoloniatesocongo.disbank.fx_controllers;

import com.rateroscoloniatesocongo.disbank.bd.BaseDatos;
import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.transacciones.GestorTransacciones;
import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;
import com.rateroscoloniatesocongo.disbank.util.Avisador;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class PanelControlController {

    @FXML
    public Button botonCorte;
    @FXML
    public TableView<Transaccion> tablaTransacciones;
    @FXML
    public TableView<Asociado> listaAsociados;
    @FXML
    public VBox panelRegistroAsociado, panelListaAsociados, panelInfo;
    @FXML
    public TextField searchBar;
    @FXML
    public TextField nombre, clabe, telegramUser, nombreComercio, banco;
    @FXML
    public VBox registrandoUsuario;
    @FXML
    public CheckBox comisionCheck;
    @FXML
    public Button eliminarAsociadoBoton;
    public TableColumn<Transaccion, String> columnaIdTransaccion, columnaAsociadoTransaccion, columnaTipoTransaccion, columnaEstadoTransaccion;
    public TableColumn<Asociado, String> columnaIdAsociado, columnaNombreAsociado, columnaPuestoAsociado;

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
        if (estaEnBlanco(searchBar)) {
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
        if (estaEnBlanco(nombre) || estaEnBlanco(clabe) || estaEnBlanco(banco) || estaEnBlanco(nombreComercio) || estaEnBlanco(telegramUser)) {
            Avisador.mandarAviso("Debes llenar todos los campos");
            return;
        }
        registrandoUsuario.setVisible(true);
        BaseDatos.agregarAsociado(nombre.getText(), clabe.getText(), banco.getText(), nombreComercio.getText(), telegramUser.getText());
        registrandoUsuario.setVisible(false);
    }

    @FXML
    public void initialize() {
        // Inicialización de la tabla de transacciones
        columnaIdTransaccion.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaAsociadoTransaccion.setCellValueFactory(new PropertyValueFactory<>("estado"));
        columnaTipoTransaccion.setCellValueFactory(new PropertyValueFactory<>(""));
        columnaEstadoTransaccion.setCellValueFactory(new PropertyValueFactory<>(""));

        // Ejemplo, implementalo así
        columnaEstadoTransaccion.setCellValueFactory(columnaEstadoTransaccion -> new SimpleStringProperty(columnaEstadoTransaccion.getValue().getLog()));
        tablaTransacciones.setItems(GestorTransacciones.getInstance().getTransaccionesTotales());

        // Inicialización de la tabla de asociados
        columnaIdAsociado.setCellValueFactory(new PropertyValueFactory<>(""));
        columnaNombreAsociado.setCellValueFactory(new PropertyValueFactory<>(""));
        columnaPuestoAsociado.setCellValueFactory(new PropertyValueFactory<>(""));
        listaAsociados.setItems(BaseDatos.getAsociados());
    }

    private boolean estaEnBlanco(TextField textField) {
        return textField == null || textField.getText().isBlank();
    }

}
