package com.rateroscoloniatesocongo.disbank.bd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.SolicitudNoEncontradaException;
import com.rateroscoloniatesocongo.disbank.util.Avisador;
import com.sun.javafx.collections.ImmutableObservableList;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import java.util.List;

@SuppressWarnings("unchecked")

/**
 * Proyecto 2 del curso Modelado y Programacion
 * Clase BaseDatos que modela la base de datos de disbank
 * @author Rateros Colonia Tesocongo
 * @version Version 1.
 */
public class BaseDatos {
    // Cada cosa de aquí es estática

    public static String rutaBD;
    /**Lista de asociados registrados*/
    public static ObservableList<Asociado> asociados;
    /**Map que relaciona el ChatId con su Asociado */
    public static HashMap<String, Asociado> chatIdAsociado;
    //Para tener claro cual es el nuevo asociado pendiente de asignacion de chatID
    public static int asociadoPendiente;

    public static String getRutaBD() {
        return rutaBD;
    }

    public static void setRutaBD(String rutaBD) {
        BaseDatos.rutaBD = rutaBD;
    }

    private static void guarda(){
        FileOutputStream fis;
        ObjectOutputStream ois= null;
        try{
            fis = new FileOutputStream(rutaBD);
            ois = new ObjectOutputStream(fis);
        }catch(IOException s){
            s.printStackTrace();
            Avisador.mandarError(s.getMessage());
        }

        try{
            ois.writeObject(asociados);
            ois.writeObject(chatIdAsociado);
        }catch(Exception e){
            e.printStackTrace();
            Avisador.mandarError(e.getMessage());
        }
    }

    private static void carga(){
        if(asociados != null || chatIdAsociado != null)
            return;
        FileInputStream fis;
        ObjectInputStream ois= null;
        try{
            fis = new FileInputStream(rutaBD);
            ois = new ObjectInputStream(fis);
        }catch(IOException s){
            s.printStackTrace();
            Avisador.mandarError(s.getMessage());
        }

        try{
            BaseDatos.asociados = (ObservableList<Asociado>) FXCollections.observableList((List<Asociado>)ois.readObject());
            BaseDatos.chatIdAsociado = (HashMap<String, Asociado>) ois.readObject();
        }catch(Exception e){
            e.printStackTrace();
            Avisador.mandarError(e.getLocalizedMessage());
        }
    }

    /**
     * Busca y regresa un asociado mediante su chatId
     * @param chatId
     * @return asociado buscado o null si no esta registrado.
     */
    public static Asociado buscarPorChatId(String chatId){
        carga();
        return chatIdAsociado.get(chatId);
    }

    /**
     * Le asocia su chatId a un asociado registrado por el admin
     * @param idAsociado id del asociado (int)
     * @param chatId chatId que se le asignara al Asociado
     * @return true si existe el asociado con id = idAsociado, 
     *         false en otro caso
     */
    public static boolean setChatId(int idAsociado, String chatId){
        carga();
        for(Asociado a : asociados){
            if(a.getId() == idAsociado){
                a.setChatId(chatId);
                chatIdAsociado.put(chatId, a);
                guarda();
                return true;
            }
        }
        guarda();
        return false;
    }

    public static ObservableList<Asociado> getAsociados() {
        carga();
        return asociados;
    }

    /**
     * Regresa un iterador para iterar a los asociados
     * @return iterator.
     */
    public static Iterator<Asociado> getIterador(){
        carga();
        return asociados.iterator();
    }

    /**
     * Metodo que agrega un asociado a la bd.
     * @param a asociado que se agregara. (Asociado)
     */
    public static void agregarAsociado(String nombre, String cuenta, String banco, String nombreComercio, String usuarioTelegram){
        carga();
        Asociado asociado = new Asociado(nombre, cuenta, banco, nombreComercio, usuarioTelegram);
        asociado.setId(asociados.indexOf(asociados.getLast()) + 1);
        asociados.add(asociado.getId(), asociado);
        setAsociadoPendiente(asociado.getId());
        guarda();
    }

    /**
     * Borra el asociado de la bd.
     * @param a asociado que se quiere borrar. (Asociado)
     */
    public static void borrarAsociado(Asociado a){
        carga();
        chatIdAsociado.remove(a.getChatId());
        asociados.remove(a.getId());
        guarda();
    }

    public static int getAsociadoPendiente() throws SolicitudNoEncontradaException {
        if (asociadoPendiente == 0){
            throw new SolicitudNoEncontradaException("No hay solicitud de registro pendiente");
        }
        return asociadoPendiente;
    }

    public static void setAsociadoPendiente(int asociadoPendiente) {
        carga();
        BaseDatos.asociadoPendiente = asociadoPendiente;
        guarda();
    }

}
