package main.dipalza.factory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import main.dipalza.ActivityConfiguracion;
import main.dipalza.DipalzaApplication;
import main.dipalza.modelo.ModeloDipalza;
import main.dipalza.transmision.ConexionTCP;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Fabrica
{
	private static Fabrica instancia;
	/**
	 * Referencia a modelo dipalza.
	 */
	private ModeloDipalza modelo;
	/**
	 * Referencia Context.
	 */
	private Context context;
	/**
	 * Formato de los decimales en la aplicación.
	 */
	private static DecimalFormat decimalFormat;
	private static SimpleDateFormat dateFormat;

	/**
	 * Constructor de la clase.
	 * 
	 * @param pContext
	 *            Referencia a context.
	 */
	public Fabrica()
	{
		super();
		context = DipalzaApplication.getAppContext();
	}

	public static final Fabrica obtenerInstancia()
	{
		if (instancia == null)
		{
			instancia = new Fabrica();
		}
		return instancia;
	}

	public ModeloDipalza obtenerModeloDipalza()
	{
		if (modelo == null)
		{
			modelo = new ModeloDipalza(context);
		}
		return modelo;
	}

	public ConexionTCP obtenerConexion()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String ipDirection = preferences.getString(ActivityConfiguracion.PREF_IP, "localhost");
		int port = 5500;
		return new ConexionTCP(ipDirection, port);
	}

	public static DecimalFormat getDecimalFormat()
	{
		if (decimalFormat == null)
		{
			decimalFormat = new DecimalFormat("##0.00");
		}
		return decimalFormat;
	}

	public static SimpleDateFormat getSimpleDateFormat()
	{
		if (dateFormat == null)
		{
			dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		}
		return dateFormat;
	}
}
