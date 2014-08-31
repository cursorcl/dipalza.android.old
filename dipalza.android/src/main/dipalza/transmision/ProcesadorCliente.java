package main.dipalza.transmision;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import main.dipalza.ActivityHandler;
import main.dipalza.factory.Fabrica;
import main.dipalza.ot.OTCliente;
import main.dipalza.ot.OTProducto;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.grupo.MsgInformaDatos;
import com.grupo.basedatos.Cliente;
import com.grupo.basedatos.Producto;
import com.grupo.biblioteca.EMessagesTypes;
import com.grupo.biblioteca.MessageToTransmit;
import com.grupo.biblioteca.VectorClientes;
import com.grupo.biblioteca.VectorProductos;

public class ProcesadorCliente implements Runnable
{
	public static final String CLIENTES = "CLIENTES";
	public static final String PRODUCTOS = "PRODUCTOS";
	
	private final String TAG = "ProcesadorCliente";
	private Queue<Object> queue;
	private boolean alive;
	private ReentrantLock lock = new ReentrantLock();
	
	/**
	 * Encargado de comunicar resultados a la HMI.
	 */
	private Handler handler;
	private int totalClientes;
	private int totalProductos;
	private int stepProducto;
	private int stepCliente;

	public ProcesadorCliente()
	{
		this.queue = new LinkedList<Object>();
		alive = true;
		(new Thread(this)).start();
	}

	/**
	 * Obtiene el valor del atributo alive.
	 * 
	 * @return El valor de alive.
	 */
	public boolean isAlive()
	{
		return alive;
	}

	/**
	 * Establece el valor al atributo alive.
	 * 
	 * @param alive
	 *            el valor a establecer a alive.
	 */
	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}

	public void addToProcess(final Object paramObject)
	{
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				lock.lock();
				try
				{
					queue.add(paramObject);
				}
				finally
				{
					lock.unlock();
				}
			}
		};
		(new Thread(runnable)).start();
	}

	/**
	 * Procesa mientras existan datos.
	 */
	public void run()
	{
		while (isAlive())
		{
			lock.lock();
			try
			{
				Object object = queue.poll();
				if (object != null)
				{
					process(object);
				}
			}
			finally
			{
				lock.unlock();
			}
		}
	}

	
	/**
	 * Procesador de los mensaje encolados.
	 * @param mensajeWrapper
	 */
	public void process(Object mensajeWrapper)
	{
		if (mensajeWrapper instanceof MessageToTransmit)
		{
			MessageToTransmit message = (MessageToTransmit) mensajeWrapper;
			
			if (message.getType().equals(EMessagesTypes.MSG_INITIALIZE_CLIENTE_FINALIZED))
			{
				// Informa finalizacion de recepcion de clientes.
			}
			else if (message.getType().equals(EMessagesTypes.MSG_INITIALIZE_PRODUCTO_FINALIZED))
			{
				// Informa finalizacion de recepcion de clientes.
			}
			if (message.getType().equals(EMessagesTypes.MSG_INITIALIZE_CLIENTE))
			{
				OTCliente otCliente = UtilitarioConversion.toOTCliente((Cliente) message.getData());
				Log.d(TAG, "Se ha recibido " + otCliente.getNombre());
				Fabrica.obtenerInstancia().obtenerModeloDipalza().grabarCliente(otCliente);
				notifyProgress(CLIENTES, ++stepCliente, totalClientes);
			}
			else if (message.getType().equals(EMessagesTypes.MSG_VECTORCLIENTES))
			{
				VectorClientes vCltes = (VectorClientes) message.getData();
				for (int n = 0; n < vCltes.size(); n++)
				{
					OTCliente otCliente = UtilitarioConversion.toOTCliente(vCltes.elementAt(n));
					Log.d(TAG, "Se ha recibido " + otCliente.getNombre());
					Fabrica.obtenerInstancia().obtenerModeloDipalza().grabarCliente(otCliente);
					notifyProgress(CLIENTES, ++stepCliente, totalClientes);
				}
			}
			else if (message.getType().equals(EMessagesTypes.MSG_INITIALIZE_PRODUCTO))
			{
				OTProducto otProducto = UtilitarioConversion.toOTProcucto((Producto) message.getData());
				Log.d(TAG, "Se ha almacenado " + otProducto.getNombre());
				Fabrica.obtenerInstancia().obtenerModeloDipalza().grabarProducto(otProducto);
				notifyProgress(PRODUCTOS, ++stepProducto, totalProductos);
			}
			else if (message.getType().equals(EMessagesTypes.MSG_VECTORPRODUCTOS))
			{
				VectorProductos vProds = (VectorProductos) message.getData();
				for (Producto producto : vProds.asVector())
				{
					OTProducto otProducto = UtilitarioConversion.toOTProcucto(producto);
					Log.d(TAG, "Se ha almacenado " + otProducto.getNombre());
					Fabrica.obtenerInstancia().obtenerModeloDipalza().grabarProducto(otProducto);
					notifyProgress(PRODUCTOS, ++stepProducto, totalProductos);
				}
			}
			else if (message.getType().equals(EMessagesTypes.MSG_INITIALIZE_CLIENTE_FINALIZED))
			{
				Log.d(TAG, "Finalizada la transmisión de clientes");
				notifyNotification("Se ha recibido " + totalClientes + " clientes.");
			}
			else if (message.getType().equals(EMessagesTypes.MSG_INITIALIZE_PRODUCTO_FINALIZED))
			{
				Log.d(TAG, "Finalizada la transmisión de productos");
				notifyNotification("Se ha recibido " + totalProductos + " productos.");
			}
			else if (message.getType().equals(EMessagesTypes.MSG_INFORMATIVE))
			{
				Log.d(TAG, "Informativo");
				// Me informa cuantos datos vienen.
				MsgInformaDatos info = (MsgInformaDatos) message.getData();
				if (info.getName().equals(Cliente.class.getName()))
				{
					stepCliente = 0;
					totalClientes =  info.getCantidad();
					notifyProgress(CLIENTES, stepCliente, totalClientes);
				}
				else if (info.getName().equals(Producto.class.getName()))
				{
					stepProducto = 0;
					totalProductos = info.getCantidad();
					notifyProgress(PRODUCTOS, stepProducto, totalProductos);
				}
			}
			else
			{
				String error = String.format("Ha llegado un mensaje errado [%s].", new Object[] { message.getType()
								.getName() });
				Log.e(TAG, error);
				notifyError(error);
			}
		}
		else
		{
			String error = String.format("Ha llegado un objeto errado [%s].", new Object[] { mensajeWrapper.getClass()
							.getName() });
			Log.e(TAG, error);
			notifyError(error);
		}
	}

	
	
	private void notifyError(String error)
	{
		if(getHandler() != null)
		{
			Message message = getHandler().obtainMessage();
			message.what = ActivityHandler.MSG_ERROR;
			Bundle bundle = new Bundle();
			bundle.putString(ActivityHandler.CAUSA, error);
			message.setData(bundle);
			getHandler().sendMessage(message);
		}
		
	}

	private void notifyProgress(String atributo, int step, int total)
	{
		if(getHandler() != null)
		{
			Message message = getHandler().obtainMessage();
			message.what = ActivityHandler.MSG_AVANCE;
			Bundle bundle = new Bundle();
			bundle.putString(ActivityHandler.ATRIBUTO, atributo);
			bundle.putInt(ActivityHandler.STEP, step);
			bundle.putInt(ActivityHandler.TOTAL, total);
			message.setData(bundle);
			getHandler().sendMessage(message);
		}		
	}

	private void notifyNotification(String atributo)
	{
		if(getHandler() != null)
		{
			Message message = getHandler().obtainMessage();
			message.what = ActivityHandler.MSG_NOTIFICACION;
			Bundle bundle = new Bundle();
			bundle.putString(ActivityHandler.ATRIBUTO, atributo);
			message.setData(bundle);
			getHandler().sendMessage(message);
		}		
		
	}
	/**
	 * Obtiene el valor del atributo handler.
	 * @return El valor de handler.
	 */
	public Handler getHandler()
	{
		return handler;
	}

	/**
	 * Establece el valor al atributo handler.
	 * @param handler el valor a establecer a handler.
	 */
	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}
}
