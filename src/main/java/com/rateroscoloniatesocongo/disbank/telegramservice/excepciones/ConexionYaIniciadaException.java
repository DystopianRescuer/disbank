package com.rateroscoloniatesocongo.disbank.telegramservice.excepciones;

/**
 * ConexionYaIniciadaException
 */
public class ConexionYaIniciadaException extends Exception{

	public ConexionYaIniciadaException(){
        super("La conexion ya fue iniciada con un token distinto, verifica la documentacion");
    }
}
