package com.rateroscoloniatesocongo.disbank.telegramservice;

import com.rateroscoloniatesocongo.disbank.modelo.Asociado;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.ErrorEnConexionException;
import com.rateroscoloniatesocongo.disbank.telegramservice.excepciones.SolicitudNoEncontradaException;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * -controlador : La instancia del controlador que estará recibiendo todas las ordenes a lo largo de la ejecución del programa
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
                try{
                    actuar(chatID, text);
                }catch (ErrorEnConexionException e){
                    //TODO : IDEM;
                }

            }
        }
    }

    /**
     *  Decide que hacer con el mensaje que llegó por medio de una Update en el run() previo
     *  */
    private void actuar(String chatID, String mensaje) throws ErrorEnConexionException{
        Asociado asociado = controlador.buscarAsociado(chatID);

        if(asociado == null){
            if(!mensaje.equals("/start"))
                return;
            else{
                if(mensaje.equals("marco")){
                    new VistaTelegram(chatID).enviarMensaje("polo");
                }
                try{
                    controlador.registrarNuevoAsociado(chatID);
                }catch(SolicitudNoEncontradaException e){
                    new VistaTelegram(chatID).enviarMensaje(ControladorTelegram.noRegistroPendiente);
                }
            }
        }else{
            switch(mensaje){
                case "Iniciar ventas":
                    controlador.registrarNuevaInteraccion(chatID);
                    break;

                case "Cerrar ventas":
                    controlador.cortePersonal(asociado);
                    break;

                case  "Comandos":
                    controlador.darComandos(asociado);
                    break;

                case "Necesito ayuda":
                    controlador.pedirAyuda(asociado);
                    break;

                default:
                    if(asociado.recibiendoAyuda){
                        controlador.mensajeAyuda(asociado, mensaje);
                    }else{
                        String[] detallesCobro = buscarCobro(mensaje);
                        if(detallesCobro != null){
                            controlador.generarNuevaTransaccion(Double.parseDouble(detallesCobro[0]), detallesCobro[1], asociado);
                            return;
                        }

                        new VistaTelegram(chatID).enviarMensaje(ControladorTelegram.instruccionNoReconocida);
                    }
            }

        }

    }

    private String[] buscarCobro(String mensaje){
        Pattern pattern = Pattern.compile("Cobrar \\$([0-9]+\\.[0-9]{2}) con (.*)");
        Matcher matcher = pattern.matcher(mensaje);

        // Si se encuentra la coincidencia, extraemos la cantidad y la terminal
        if (matcher.find()) {
            String cantidad = matcher.group(1); // Obtenemos el grupo que coincide con la cantidad numérica
            String terminal = matcher.group(2); // Obtenemos el grupo que coincide con la parte "metodo de cobro"
            return new String[]{cantidad, terminal};
        } else {
            // Si no se encuentra la coincidencia, retornamos null
            return null;
        }
    }
}
