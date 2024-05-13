package com.rateroscoloniatesocongo.disbank.bd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;

/**
 * Proyecto 2 del curso Modelado y Programacion
 * Clase BaseDatos que modela la base de datos de disbank
 * @author Rateros Colonia Tesocongo
 * @version Version 1.
 */
public class BaseDatos {
    // Cada cosa de aquí es estática

    /**Lista de asociados registrados*/
    public static ArrayList<Asociado> asociados;
    /**Map que relaciona el ChatId con su Asociado */
    public static HashMap<String, Asociado> chatIdAsociado = new HashMap<>();

    public BaseDatos(){

    }

    /**
     * Busca y regresa un asociado mediante su chatId
     * @param chatId
     * @return asociado buscado o null si no esta registrado.
     */
    public Asociado buscarPorChatId(String chatId){
        if(chatIdAsociado.containsKey(chatId)){
            return chatIdAsociado.get(chatId);
        }
        return null;
    }

    /**
     * Le asocia su chatId a un asociado registrado por el admin
     * @param idAsociado. id del asociado (int)
     * @param chatId. chatId que se le asignara al Asociado
     * @return true si existe el asociado con id = idAsociado, 
     *         false en otro caso
     */
    public boolean setChatId(int idAsociado, String chatId){
        for(Asociado a : asociados){
            if(a.getId() == idAsociado){
                a.setChatId(chatId);
                chatIdAsociado.put(chatId, a);
                return true;
            }
        }
        return false;
    }

    /**
     * Regresa un iterador para iterar a los asociados
     * @return iterator.
     */
    public Iterator<Asociado> getIterador(){
        return asociados.iterator();
    }

}
