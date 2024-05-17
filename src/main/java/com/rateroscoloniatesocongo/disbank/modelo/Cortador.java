package com.rateroscoloniatesocongo.disbank.modelo;

import com.rateroscoloniatesocongo.disbank.bd.BaseDatos;
import com.rateroscoloniatesocongo.disbank.telegramservice.ControladorTelegram;
import com.rateroscoloniatesocongo.disbank.telegramservice.VistaTelegram;
import com.rateroscoloniatesocongo.disbank.clipservice.ControladorClip;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;
import com.rateroscoloniatesocongo.disbank.telegramservice.mensajes.Mensaje;
import com.rateroscoloniatesocongo.disbank.telegramservice.mensajes.MensajeFactory;
import com.rateroscoloniatesocongo.disbank.transacciones.Transaccion;
import com.rateroscoloniatesocongo.disbank.util.Avisador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Iterator;

public class Cortador {
    /** Unica instancia de ControladorTelegram */
    private ControladorTelegram controladorTelegram;
    /** Unica instancia de ControladorClip */
    private final ControladorClip controladorClip;

    public Cortador() {
        try{
            controladorTelegram = ControladorTelegram.getInstance();
        }catch(ErrorEnConexionException e){
            Avisador.mandarErrorFatal("No se pudo establecer conexion con Telegram.");
        }
        controladorClip = ControladorClip.getInstance();
    }

    /**
     *
     *  */
    public void corteDiario(){
        StringBuilder dumpTxt = new StringBuilder();
        Iterator<Asociado> iterador = BaseDatos.getIterador();
        while(iterador.hasNext()){
            Asociado asociado = iterador.next();
            StringBuilder transacciones = extraerTransaccionesAsociado(asociado);
            enviarCorte(asociado, "CorteFinal", transacciones);
            dumpTxt.append("Asociado: " + asociado.getNombre() + "\n");
            dumpTxt.append(transacciones);
        }

        String diaHoy = LocalDate.now() + ".txt";
        try (PrintWriter out = new PrintWriter("cortesDiarios/" + diaHoy)){
            out.println(dumpTxt.toString());
        }catch(Exception e){
            Avisador.mandarError(e.getMessage());
        }

    }

    /**
     *  Para enviar el corte personal al asociado que lo solicita.
     *  Recorre la lista de transacciones interna del asociado y junta toda la información de las mismas en una lista
     *  que luego envía por Telegram.
     *
     *  @param asociado el asociado del que se desea realizar el corte personal
     *  */
    public void cortePersonal(Asociado asociado){
        StringBuilder mensajeFinal = extraerTransaccionesAsociado(asociado);
        enviarCorte(asociado, "CortePersonal", mensajeFinal);
    }

    /**
     *  Para extraer las transacciones de un asociado en la lista que luego se le envía para los cortes.
     *
     *  Metodo auxiliar para refactorizacion de los dos metodos principales de esta clase
     *
     *  @param asociado el asociado del que se extraerán las transacciones
     *  */
    private StringBuilder extraerTransaccionesAsociado(Asociado asociado) {
        StringBuilder mensajeFinal = new StringBuilder();
        for(Transaccion e : asociado.transaccionesDia){
            mensajeFinal.append(e.toString() + "\n");
        }
        return mensajeFinal;
    }

    /**
     *  Envia un mensaje de corte con las especificaciones dadas
     *
     *  Metodo auxiliar para refactorizacion de los dos metodos principales de esta clase
     *
     *  @param asociado el asociado del que se está obteniendo el corte
     *  @param tipoCorte para especificar que tipo de corte se está enviando en el mensaje
     *  @param mensajeFinal un StringBuilder con la información de transacciones del corte
     *  */
    private void enviarCorte(Asociado asociado, String tipoCorte, StringBuilder mensajeFinal) {
        Mensaje mensaje = MensajeFactory.nuevoMensaje(tipoCorte, asociado, mensajeFinal.toString());
        //Guard clause
        if(tipoCorte.equals("CorteFinal")){
            try{
                new VistaTelegram(mensaje.getAsociado().getChatId()).enviarMensaje(mensaje.darMensaje());
            }catch(ErrorEnConexionException e){
                Avisador.mandarError(e.getMessage());
            }
            return;
        }

        try{
            controladorTelegram.enviarMensaje(mensaje);
        }catch(ErrorEnConexionException e){
            Avisador.mandarError(e.getMessage());
        }
    }
}
