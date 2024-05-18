package com.rateroscoloniatesocongo.disbank.bd;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.SolicitudNoEncontradaException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Iterator;


/**
 * Proyecto 2 del curso Modelado y Programacion
 * Clase BaseDatos que modela la base de datos de disbank
 *
 * @author Rateros Colonia Tesocongo
 * @version Version 1.
 */
public class BaseDatos {
    // Cada cosa de aquí es estática

    /**
     * Lista de asociados registrados
     */
    public static final ObservableList<Asociado> asociados = FXCollections.observableArrayList();
    /**
     * Map que relaciona el ChatId con su Asociado
     */
    public static final HashMap<String, Asociado> chatIdAsociado = new HashMap<>();
    //Para tener claro cual es el nuevo asociado pendiente de asignacion de chatID
    public static int asociadoPendiente;

    /**
     * Busca y regresa un asociado mediante su chatId
     *
     * @param chatId el id del chat del asociado a buscar
     * @return asociado buscado o null si no esta registrado.
     */
    public static Asociado buscarPorChatId(String chatId) {
        return chatIdAsociado.get(chatId);
    }

    /**
     * Le asocia su chatId a un asociado registrado por el admin
     *
     * @param idAsociado id del asociado (int)
     * @param chatId     chatId que se le asignara al Asociado
     */
    public static void setChatId(int idAsociado, String chatId) {
        for (Asociado a : asociados) {
            if (a.getId() == idAsociado) {
                a.setChatId(chatId);
                chatIdAsociado.put(chatId, a);
                return;
            }
        }
    }

    public static ObservableList<Asociado> getAsociados() {
        return asociados;
    }

    /**
     * Regresa un iterador para iterar a los asociados
     *
     * @return iterator.
     */
    public static Iterator<Asociado> getIterador() {
        return asociados.iterator();
    }

    /**
     * Metodo que agrega un asociado a la bd.
     *
     * @param nombre          nombre del Asociado
     * @param cuenta          numero de cuenta del Asociado
     * @param banco           nombre del banco del Asociado
     * @param nombreComercio  nombre de su local/puesto
     * @param usuarioTelegram usuario de Telegram
     */
    public static void agregarAsociado(String nombre, String cuenta, String banco, String nombreComercio, String usuarioTelegram) {
        Asociado asociado = new Asociado(nombre, cuenta, banco, nombreComercio, usuarioTelegram);
        int index = asociados.isEmpty() ? 0 : asociados.indexOf(asociados.getLast()) + 1;
        asociado.setId(index);
        asociados.add(asociado.getId(), asociado);
        setAsociadoPendiente(asociado.getId());
    }

    /**
     * Borra el asociado de la bd.
     *
     * @param a asociado que se quiere borrar. (Asociado)
     */
    public static void borrarAsociado(Asociado a) {
        chatIdAsociado.remove(a.getChatId());
        asociados.remove(a.getId());
    }

    public static int getAsociadoPendiente() throws SolicitudNoEncontradaException {
        if (asociadoPendiente == -1) {
            throw new SolicitudNoEncontradaException("No hay solicitud de registro pendiente");
        }
        return asociadoPendiente;
    }

    public static void setAsociadoPendiente(int asociadoPendiente) {
        BaseDatos.asociadoPendiente = asociadoPendiente;
    }

}
