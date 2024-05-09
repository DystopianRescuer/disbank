package com.rateroscoloniatesocongo.disbank.transacciones;

import com.rateroscoloniatesocongo.disbank.clipservice.ControladorClip;
import com.rateroscoloniatesocongo.disbank.clipservice.excepciones.TransaccionNoRegistradaException;
import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;

import java.util.LinkedList;
import java.util.List;
/**
 * Proyecto 2 del curso de Modelado y Programacion.
 * Clase GestorTransacciones.
 * @author Rateros colonia tesocongo.
 * @version Version 1.
 */
public class GestorTransacciones {

    private static GestorTransacciones instance;

    private final List<Transaccion> pendientes;
    private ControladorTelegram controladorTelegram;
    private final ControladorClip controladorClip;
    private boolean iniciado;

    private GestorTransacciones() {
        pendientes = new LinkedList<>();
        iniciado = false;
        try{
            controladorTelegram = ControladorTelegram.getInstance();
        }catch(ErrorEnConexionException e){
            //Placeholder
            //TODO: Desarrollar logica de este error
            //mandar el mensaje pop de error a la interfaz
        }
        controladorClip = ControladorClip.getInstance();
    }

    // Inicia el gestor de transacciones
    public void iniciar() {
        if(!iniciado) {
            // TBD
            iniciado = true;
        }
    }

    // TODO: hacer esto seguro, que no cualquiera pueda llamar
    public void detener() {
        //crear un Cortador 
    }

    public String nuevaTransaccion(Asociado asociado, Cobro cobro) {
        // Crea el objeto transacción
        Transaccion transaccion = new Transaccion(asociado, cobro);
        // Se la da a Clip y espera la respuesta positiva de este
        try {
            controladorClip.solicitarTransaccion();
        } catch (TransaccionNoRegistradaException e) {
            //mandar mensaje a telegram de que no se pudo registrar la transaccion
            return "No se pudo registrar la transaccion."; // este es mientras hacemos el pquete 
        }
        // Si esto ocurrió correctamente entonces registra la transacción en pendientes
        pendientes.add(transaccion);
        return "";
    }


    // TODO los argumentos que recibe
    public void actualizarEstado(Transaccion transaccion) {
        // Checa el estado de la transaccion
        // Si todo sale bien va y le avisa a telegram que todo salio bien para que le avise al usuario
        // Actualiza lo que deba actualizar en base de datos
        // Quita de su lista dependientes
        // Esto de momento, checar/pensar si falta algo más
    }

    /**
     * Metodo que busca y regresa una transaccion mediante su id.
     * @param id. id de la transaccion buscada. (String)
     * @return t. transaccion requerida. (Transaccion)
     */
    public Transaccion getTransaccionPorId(String id){
        for(Transaccion t : pendientes){
            if(t.getId().equals(id)){
                return t;
            }
        }
        return null;
    }


    public static GestorTransacciones getInstance() {
        if(instance == null) {
            instance = new GestorTransacciones();
        }
        return instance;
    }
}
