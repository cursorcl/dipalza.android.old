package main.dipalza.transmision;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import android.os.Handler;

import com.grupo.biblioteca.MessageToTransmit;

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

	public ConexionTCP(String ipDirection, int port)
	{
		super();
		this.ipDirection = ipDirection;
		this.port = port;
	}

	public ConexionTCP(Socket socket)
	{
		super();
		this.socket = socket;
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
		// Notificar que se esta conectando al servidor.
		socket = new Socket(ipDirection, port);
		output = new ObjectOutputStream(socket.getOutputStream());
		input = new ObjectInputStream(socket.getInputStream());
		alive = true;
		// Notificar que se encuetra conectado.
		
		start();
	}

	public final void disconnect() throws IOException
	{
		if (isConnected())
		{
			procesador.setAlive(false);
			socket.close();
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
				catch (ClassNotFoundException e)
				{
					// Notificar falla en la recepcion de algo.
				  e.printStackTrace();
				}
			}
			disconnect();
		}
		catch (UnknownHostException e)
		{
			// Notificar un error desconocido desde el host.
		  e.printStackTrace();
		}
		catch (IOException e)
		{
			// notificar un error de entrada / salida.
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
			// Enviar notificacion de falla en el envio de algo.
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

	
}
