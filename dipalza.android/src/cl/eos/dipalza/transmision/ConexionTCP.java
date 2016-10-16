package cl.eos.dipalza.transmision;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.grupo.biblioteca.MessageToTransmit;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cl.eos.dipalza.ActivityHandler;

public class ConexionTCP implements Runnable
{
	/**
	 * Socket de conexion.
	 */
	private Socket socket;
	/**
	 * Direccion ip a conectarse.
	 */
	private String ipDirection = "localhost";
	/**
	 * Puerta de conexion.
	 */
	private int port = 5500;
	/**
	 * Salida del socket.
	 */
	private ObjectOutputStream output = null;
	/**
	 * Entrada del socket.
	 */
	private ObjectInputStream input = null;
	/**
	 * Estado de conexion.
	 */
	private boolean alive = false;
	
	/**
	 * Encargado de comunicar resultados a la HMI.
	 */
	private Handler handler;
	/**
	 * Quien realmente procesa la informacion.
	 */
	private ProcesadorCliente procesador;
	
	Context falseContext;

	public ConexionTCP(String ipDirection, int port)
	{
		super();
		this.ipDirection = ipDirection;
		this.port = port;
	}


	/**
	 * Conecta el socket e inicia la recepcion de datos.
	 */
	private final void start()
	{
		(new Thread(this)).start();
	}

	/**
	 * Conecta la hebra.
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public final void connect() throws UnknownHostException, IOException
	{
		procesador = new ProcesadorCliente();
		procesador.setHandler(this.handler);

		socket = new Socket(ipDirection, port);
		output = new ObjectOutputStream(socket.getOutputStream());
		input = new ObjectInputStream(socket.getInputStream());
		alive = true;
		
		notifyNotification("Conectado al servidor");
		start();
	}

	public final void disconnect() throws IOException
	{
		if (isConnected())
		{
			procesador.setAlive(false);
			socket.close();
			notifyNotification("Desconetado del servidor");
		}
	}

	public boolean isConnected()
	{
		return (this.socket != null) && (this.socket.isConnected());
	}

	public void run()
	{
		try
		{
			while (alive)
			{
				Object obj;
				try
				{
					obj = input.readObject();
					if (obj instanceof MessageToTransmit)
					{
						procesador.addToProcess((MessageToTransmit) obj);
					}
				}
				catch (Exception e)
				{
				  e.printStackTrace();
				  //notifyError("Se ha producido un error en la transferencia");
				  alive =false;
				}
			}
			disconnect();
		}
		catch (UnknownHostException e)
		{
		  notifyError("Direccion de servidor desconocida");
		  e.printStackTrace();
		}
		catch (IOException e)
		{
		  notifyError("Error de escritura/lectura");
		  e.printStackTrace();
		}
	}

	/**
	 * Envia solamente un mensaje.
	 * 
	 * @param mensaje
	 * @return Verdadero si se ha enviado el mensaje sin excepciones.
	 */
	public boolean send(MessageToTransmit mensaje)
	{
		boolean result = true;
		try
		{
			this.output.writeObject(mensaje);
		}
		catch (Exception e)
		{
			result = false;
		}
		return result;
	}

	public boolean send(List<MessageToTransmit> datos)
	{
		boolean result = true;
		for (MessageToTransmit mensaje : datos)
		{
			result = result && send(mensaje);
		}
		return result;
	}

	/**
	 * Obtiene el valor del atributo handler.
	 * @return El valor de handler.
	 */
	public final Handler getHandler()
	{
		return handler;
	}

	/**
	 * Establece el valor al atributo handler.
	 * @param handler el valor a establecer a handler.
	 */
	public final void setHandler(Handler handler)
	{
		this.handler = handler;
	}

	/**
	 * Obtiene el valor del atributo ipDirection.
	 * @return El valor de ipDirection.
	 */
	public final String getIpDirection()
	{
		return ipDirection;
	}

	/**
	 * Establece el valor al atributo ipDirection.
	 * @param ipDirection el valor a establecer a ipDirection.
	 */
	public final void setIpDirection(String ipDirection)
	{
		this.ipDirection = ipDirection;
	}

	
	  private void notifyError(String error) {
	    if (getHandler() != null) {
	      Message message = getHandler().obtainMessage();
	      message.what = ActivityHandler.MSG_ERROR;
	      Bundle bundle = new Bundle();
	      bundle.putString(ActivityHandler.CAUSA, error);
	      message.setData(bundle);
	      getHandler().sendMessage(message);
	    }

	  }

	  private void notifyNotification(String atributo) {
	    if (getHandler() != null) {
	      Message message = getHandler().obtainMessage();
	      message.what = ActivityHandler.MSG_NOTIFICACION;
	      Bundle bundle = new Bundle();
	      bundle.putString(ActivityHandler.ATRIBUTO, atributo);
	      message.setData(bundle);
	      getHandler().sendMessage(message);
	    }

	  }
	  
	
}
