package com.rateroscoloniatesocongo.disbank.bd;

import java.util.HashMap;
import java.util.Iterator;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.SolicitudNoEncontradaException;
import com.sun.javafx.collections.ImmutableObservableList;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Proyecto 2 del curso Modelado y Programacion
 * Clase BaseDatos que modela la base de datos de disbank
 * @author Rateros Colonia Tesocongo
 * @version Version 1.
 */
public class BaseDatos {
    // Cada cosa de aquí es estática

    /**Lista de asociados registrados*/
    public static ObservableList<Asociado> asociados;
    /**Map que relaciona el ChatId con su Asociado */
    public static HashMap<String, Asociado> chatIdAsociado = new HashMap<>();
    //Para tener claro cual es el nuevo asociado pendiente de asignacion de chatID
    public static int asociadoPendiente;

    private BaseDatos(){
        asociados = FXCollections.observableArrayList();
        chatIdAsociado = new HashMap<>();
    }

    public static void guarda(){

    }

    public static void carga(){

    }

    /**
     * Busca y regresa un asociado mediante su chatId
     * @param chatId
     * @return asociado buscado o null si no esta registrado.
     */
    public static Asociado buscarPorChatId(String chatId){
        if(chatIdAsociado.containsKey(chatId)){
            return chatIdAsociado.get(chatId);
        }
        return null;
    }

    /**
     * Le asocia su chatId a un asociado registrado por el admin
     * @param idAsociado id del asociado (int)
     * @param chatId chatId que se le asignara al Asociado
     * @return true si existe el asociado con id = idAsociado, 
     *         false en otro caso
     */
    public static boolean setChatId(int idAsociado, String chatId){
        for(Asociado a : asociados){
            if(a.getId() == idAsociado){
                a.setChatId(chatId);
                chatIdAsociado.put(chatId, a);
                return true;
            }
        }
        return false;
    }

    public static ObservableList<Asociado> getAsociados() {
        return asociados;
    }

    /**
     * Regresa un iterador para iterar a los asociados
     * @return iterator.
     */
    public static Iterator<Asociado> getIterador(){
        return asociados.iterator();
    }

    /**
     * Metodo que agrega un asociado a la bd.
     * @param a. asociado que se agregara. (Asociado)
     */
    public static void agregarAsociado(String nombre, String cuenta, String banco, String nombreComercio, String usuarioTelegram){
        Asociado asociado = new Asociado(nombre, cuenta, banco, nombreComercio, usuarioTelegram);
        asociados.add(asociado);
    }

    /**
     * Borra el asociado de la bd.
     * @param a asociado que se quiere borrar. (Asociado)
     */
    public static void borrarAsociado(Asociado a){
        chatIdAsociado.remove(a.getChatId());
        asociados.remove(a); 
    }

    public static int getAsociadoPendiente() throws SolicitudNoEncontradaException{
        if (asociadoPendiente == 0){
            throw new SolicitudNoEncontradaException("No hay solicitud de registro pendiente");
        }
        return asociadoPendiente;
    }

    public static void setAsociadoPendiente(int asociadoPendiente) {
        BaseDatos.asociadoPendiente = asociadoPendiente;
    }

}
