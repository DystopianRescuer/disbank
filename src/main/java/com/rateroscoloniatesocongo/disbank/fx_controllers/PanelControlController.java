package com.rateroscoloniatesocongo.disbank.fx_controllers;

import com.rateroscoloniatesocongo.disbank.bd.BaseDatos;
import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.transacciones.GestorTransacciones;
import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;
import com.rateroscoloniatesocongo.disbank.util.Avisador;
import com.rateroscoloniatesocongo.disbank.util.RemoteMessagePassing;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

/**
 * Controlador de la vista del panel de control del programa
 * Se encarga de toda la lógica detrás de los botones, gráficas, tablas y funcionalidades de lo que el usuario
 * ve durante la ejecución del programa.
 */
public class PanelControlController {

    /**
     * Referencia estática hacia la tabla de transacciones, necesaria para que desde fuera de la clase pueda solicitarse su refresh
     */
    private static TableView<Transaccion> transacciones;


    /**
     * Todos los componentes de los que sus referencias son necesarias para el correcto funcionamiento del programa
     * Vienen directamente de su declaración en la vista, por eso su etiqueta
     */
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
    public Button eliminarAsociadoBoton, conectarTerminalButton;
    @FXML
    public TableColumn<Transaccion, String> columnaIdTransaccion, columnaAsociadoTransaccion, columnaTipoTransaccion, columnaEstadoTransaccion, columnaCantidadTransaccion;
    @FXML
    public TableColumn<Asociado, String> columnaIdAsociado, columnaNombreAsociado, columnaPuestoAsociado;
    @FXML
    public PieChart ventasTipo, ventasEstado;

    /**
     * Método que determina que sucede cuando se presiona el botón de Información
     * Manda el panel de información al frente
     *
     * @param actionEvent la accion realizada
     */
    @FXML
    public void onInfoButtonClick(ActionEvent actionEvent) {
        panelInfo.toFront();
    }

    /**
     * Método que determina que sucede cuando se presiona el botón de Información
     * Manda el panel de registro de asociado al frente
     * @param actionEvent la acción realizada
     */
    @FXML
    public void onRegistroAsociadoClick(ActionEvent actionEvent) {
        panelRegistroAsociado.toFront();
    }


    /**
     * Método que determina que sucede cuando se presiona el botón de Información
     * Manda el panel de lista de asociados al frente
     * @param actionEvent la acción realizada
     */
    @FXML
    public void onListaAsociadosClick(ActionEvent actionEvent) {
        panelListaAsociados.toFront();
    }


    /**
     * Método que determina que sucede cuando hay un cambio en la barra de búsqueda
     * Esto queda como una funcionalidad pendiente
     * @param actionEvent la acción realizada
     */
    @FXML
    public void onSearchBarUpdate(ActionEvent actionEvent) {
        if (estaEnBlanco(searchBar)) {
            // Pone el normal
        } else {
            // Pone los resultados de búsqueda
        }
    }

    /**
     * Método que determina que sucede cuando se presiona el botón de corte
     * @param actionEvent la acción realizada
     */
    @FXML
    public void onCorteRequest(ActionEvent actionEvent) {
        // Crea un cortador y lo pone a chambear
        botonCorte.setDisable(true);
        GestorTransacciones.getInstance().detener();
        Avisador.mandarAviso("Corte del día realizado.");
    }

    /**
     * Método que determina que sucede cuando se presiona el botón de registrar asociado
     * @param actionEvent la acción realizada
     */
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

    /**
     * Inicialización del controlador
     */
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

        // Define la referencia estática de la tabla de transacciones
        PanelControlController.transacciones = this.tablaTransacciones;
    }

    /**
     * Método auxiliar para determinar si un TextField está en blanco
     * @param textField el TextField a revisar
     * @return true si está en blanco, false si no
     */
    private boolean estaEnBlanco(TextField textField) {
        return textField == null || textField.getText().isBlank();
    }

    /**
     * Método público que permite refrescar la tabla de transacciones sin exponer la referencia a esta.
     */
    public static void refrescarTablaTransacciones() {
        transacciones.refresh();
    }

    public void onConectarTerminalAttempt(ActionEvent actionEvent) {
        conectarTerminalButton.setDisable(true);
        String ip = JOptionPane.showInputDialog("Introduce la IP de la terminal");
        try {
            RemoteMessagePassing<String> server = new RemoteMessagePassing<>(new Socket(ip, 8080));
            server.send("Conexión exitosa");
            Avisador.setIP(ip);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Hubo un problema para conectar con la terminal");
            conectarTerminalButton.setDisable(false);
        }
    }
}
