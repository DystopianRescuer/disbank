package com.rateroscoloniatesocongo.disbank.fx_controllers;

import com.rateroscoloniatesocongo.disbank.bd.BaseDatos;
import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.transacciones.GestorTransacciones;
import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;
import com.rateroscoloniatesocongo.disbank.util.Avisador;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class PanelControlController {

    private static TableView<Transaccion> transacciones;

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
    @FXML
    public TableColumn<Transaccion, String> columnaIdTransaccion, columnaAsociadoTransaccion, columnaTipoTransaccion, columnaEstadoTransaccion, columnaCantidadTransaccion;
    @FXML
    public TableColumn<Asociado, String> columnaIdAsociado, columnaNombreAsociado, columnaPuestoAsociado;
    public PieChart ventasTipo, ventasEstado;

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
        Avisador.mandarAviso("Corte del día realizado.");
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
        Avisador.mandarAviso("Usuario registrado exitosamente");

        nombre.clear();
        clabe.clear();
        banco.clear();
        nombreComercio.clear();
        telegramUser.clear();
    }

    @FXML
    public void initialize() {
        // Inicialización de la tabla de transacciones
        columnaIdTransaccion.setCellValueFactory(columnaEstadoTransaccion -> new SimpleStringProperty(columnaEstadoTransaccion.getValue().getIdTransaccion()));
        columnaAsociadoTransaccion.setCellValueFactory(columnaAsociadoTransaccion -> new SimpleStringProperty(columnaAsociadoTransaccion.getValue().getAsociado().getNombre()));
        columnaTipoTransaccion.setCellValueFactory(columnaTipoTransaccion -> new SimpleStringProperty(columnaTipoTransaccion.getValue().getTipoCobro()));
        columnaEstadoTransaccion.setCellValueFactory(columnaEstadoTransaccion -> new SimpleStringProperty(columnaEstadoTransaccion.getValue().getEstado().toString()));
        columnaCantidadTransaccion.setCellValueFactory(columnaCantidadTransaccion -> new SimpleStringProperty(columnaCantidadTransaccion.getValue().getCantidad()));
        tablaTransacciones.setItems(GestorTransacciones.getInstance().getTransaccionesTotales());

        // Inicialización de la tabla de asociados
        columnaIdAsociado.setCellValueFactory(columnaIdAsociado -> new SimpleIntegerProperty(columnaIdAsociado.getValue().getId()).asString());
        columnaNombreAsociado.setCellValueFactory(columnaNombreAsociado -> new SimpleStringProperty(columnaNombreAsociado.getValue().getNombre()));
        columnaPuestoAsociado.setCellValueFactory(columnaPuestoAsociado -> new SimpleStringProperty(columnaPuestoAsociado.getValue().getNombreComercio()));
        listaAsociados.setItems(BaseDatos.getAsociados());

        PanelControlController.transacciones = this.tablaTransacciones;
    }

    private boolean estaEnBlanco(TextField textField) {
        return textField == null || textField.getText().isBlank();
    }

    public static void refrescarTablaTransacciones() {
        transacciones.refresh();
    }

}
