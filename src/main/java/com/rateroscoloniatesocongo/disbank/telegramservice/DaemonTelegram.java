package com.rateroscoloniatesocongo.disbank.telegramservice;

import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Hilo daemon del controlador de Telegram
 *
 * Está destinado a correr durante toda la ejecución, recibiendo las Updates del bot mediante long polling y destinandolas a
 * los metodos asignados para este motivo del {@link ControladorTelegram}
 *
 * Tiene acceso privilegiado a varios métodos del ControladorTelegram que están destinados a funcionar exclusivamente para esta
 * clase.
 *
 * Se podria discutir que este es el controlador de telegram real, pero la clase {@link ControladorTelegram} es la que llama a la
 * existencia a este mismo hilo, el cual solamente asume la tarea de escuchar activamente las updates del bot y canalizarlas a los
 * metodos y procedimientos correspondientes
 *
 * Atributos:
 * -
 */
public class DaemonTelegram extends Thread{

    ControladorTelegram controlador;

    DaemonTelegram(ControladorTelegram controlador){
        this.controlador = controlador;
    }


    @Override
    public void run(){
        while(true){
            JSONArray updates = null;
            try{
                updates = controlador.getUpdates();
            }catch (ErrorEnConexionException e){
                //TODO : Pensar en como implementar un posible error en conexion con la API
            }

            for(int i = 0; i<updates.length() ; i++){
                JSONObject update = updates.getJSONObject(i);
                JSONObject mensaje = update.optJSONObject("message");
                //Guard clause para cualquier otra update que no sea un mensaje
                if(mensaje == null)
                    continue;

                //Obteniendo chatID y texto del mensaje
                String chatID = mensaje.getJSONObject("chat").getString("id");
                String text = mensaje.getString("text");


            }

        }
    }

    private void actuar(String chatID, String mensaje){

    }
}
