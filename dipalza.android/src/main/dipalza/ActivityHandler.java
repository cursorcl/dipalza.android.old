package main.dipalza;

import android.os.Handler;
import android.os.Message;

/**
 * Actividad que ya tiene implementado un Handler por defecto.
 * @author cursor
 */
public abstract class ActivityHandler extends DashboardActivity
{
	
	public static final String CAUSA = "CAUSA";
	public static final String STEP = "STEP";
	public static final String TOTAL = "TOTAL";
	public static final String ATRIBUTO = "ATRIBUTO";
	
	public static final int MSG_ERROR = 1;
	public static final int MSG_AVANCE = 2;
	public static final int MSG_NOTIFICACION = 3;
	
	private HanlderRecepcion handler;
	
	public ActivityHandler()
	{
		super();
		handler = new HanlderRecepcion();
	}
	
	
	
	/**
	 * Obtiene el valor del atributo handler.
	 * @return El valor de handler.
	 */
	public final HanlderRecepcion getHandler()
	{
		return handler;
	}

	/**
	 * Establece el valor al atributo handler.
	 * @param handler el valor a establecer a handler.
	 */
	public final void setHandler(HanlderRecepcion handler)
	{
		this.handler = handler;
	}



	/**
	 * Clase que recibe mensajes asincronicos.
	 * @author cursor
	 */
	private class HanlderRecepcion extends Handler
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void handleMessage(final Message message)
		{
			super.handleMessage(message);
			procesarMensajeAvance(message);
			procesarMensajeError(message);
			procesarMensajeNotificacion(message);
		}
	}
	
	
	/**
	 * Procesa mensaje de notificacion enviado desde una hebra. 
	 * @param message Mensaje a procesar.
	 */
	protected abstract void procesarMensajeNotificacion(Message message);

	/**
	 * Procesa mensaje de error enviado desde una hebra.
	 * @param message Mensaje a procesar.
	 */
	protected abstract void procesarMensajeError(Message message);

	/**
	 * Procesa mensaje de avance enviado desde una hebra.
	 * @param message Mensaje a procesar.
	 */
	protected abstract void procesarMensajeAvance(Message message);

}
