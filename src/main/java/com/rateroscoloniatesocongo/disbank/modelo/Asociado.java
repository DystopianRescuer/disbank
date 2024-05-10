package com.rateroscoloniatesocongo.disbank.modelo;
import java.util.ArrayList;

import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;
/**
 * Proyecto 2 del curso de Modelado y Programacion.
 * Clase Asociado que modela un asociado de la merkadita.
 * @author Rateros colonia tesocongo.
 * @version Version 1.
 */

public class Asociado {
    /** id del chat de telegram del asociado*/
    String chatId;
    /** nombre del asociado*/
    String nombre;
    /** cuenta del asociado */
    String cuenta;
    /** nombre de banco del asociado */
    String banco;
    /** id del asociado */
    int id;
    /** Lista de transacciones hechas en el dia del asociado */
    transient ArrayList <Transaccion> transaccionesDia;

    /**
     * Metodo constructor Asociado que inicializa los atributos
     * @param chat_id. chat id del asociado (String)
     * @param nombre. nombre del asociado (String)
     * @param cuenta. cuenta de banco del asociado (String)
     * @param banco. nombre de banco del asociado (String)
     * @param id. id del asociado (int)
     */
    public Asociado(String nombre, String cuenta, String banco, int id){
        this.nombre = nombre;
        this.cuenta = cuenta;
        this.banco = banco;
        this.id = id;
        transaccionesDia = new ArrayList <Transaccion> ();
    }

    /**
     * Permite acceder al chat Id del asociado
     * @return chatId. chat id del asociado. (String)
     */
    public String getChatId(){
        return chatId;
    }
    
    /**
     * Modifica el chatId del asociado.
     * @param chatId. nuevo chatId del asociado. (String)
     */
    public void setChatId(String chatId){
        this.chatId = chatId;
    }

    /**
     * Permite acceder al nombre del Asociado.
     * @return nombre. nombre del Asociado. (String)
     */
    public String getNombre(){
        return nombre;
    }

    /**
     * Modifica el nombre del Asociado.
     * @param nombre. nuevo nombre del Asociado. (String)
     */
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    /**
     * Permite acceder a la cuenta del Asociado.
     * @return cuenta. cuenta del asociado. (String)
     */
    public String getCuenta(){
        return cuenta;
    }

    /**
     * Modifica la cuenta del Asociado.
     * @param cuenta. nueva cuenta del Asociado. (String)
     */
    public void setCuenta(String cuenta){
        this.cuenta = cuenta;
    }

    /**
     * Permite acceder al nombre del banco del Asociado.
     * @return banco. nombre del banco del Asociado. (String)
     */
    public String getBanco(){
        return banco;
    }

    /**
     * Modifica el banco del asociado.
     * @param banco. nuevo banco del Asociado. (String)
     */
    public void setBanco(String banco){
        this.banco = banco;
    }

    /**
     * Permite acceder al id del asociado.
     * @return id. id del asociado. (int).
     */
    public int getId(){
        return id;
    }

    /**
     * Modifica el id del asociado.
     * @param id. nuevo id del asociado (int)
     */
    public void setId(int id){
        this.id = id;
    }

    /**
     * Permite acceder a la lista de transacciones del dia del Asociado.
     * @return transaccionesDia. lista de transacciones del dia. (ArrayList)
     */
    public ArrayList<Transaccion> getTransaccionesDia(){
        return transaccionesDia;
    }

    /**
     * Agrega una transaccion a la lista de transacciones del dia del asociado.
     * @param t. transaccion a agregar. (Transaccion)
     */
    public void agregarTransaccionDia(Transaccion t){
        transaccionesDia.add(t);
    }

}
