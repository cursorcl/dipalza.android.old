package main.dipalza.modelo;

import java.util.List;

import main.dipalza.database.DBDipalza;
import main.dipalza.modelo.vistas.IModeloDipalza;
import main.dipalza.ot.OTCliente;
import main.dipalza.ot.OTEspeciales;
import main.dipalza.ot.OTProducto;
import main.dipalza.ot.OTProductoVenta;
import main.dipalza.ot.OTResumenVentas;
import main.dipalza.ot.OTVenta;
import android.content.Context;

public class ModeloDipalza implements IModeloDipalza
{
	/**
	 * Acceso a los datos.
	 * @uml.property  name="dbDipalza"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private DBDipalza dbDipalza;

	/**
	 * Constructor del modelo dipalza.
	 */
	public ModeloDipalza(final Context context)
	{
		dbDipalza = new DBDipalza(context);
		dbDipalza.open();
	}

	public List<OTCliente> obtenerClientes()
	{
		return dbDipalza.getClientes();
	}
	
	public OTCliente obtenerCliente(long idCliente)
	{
		return dbDipalza.getCliente(idCliente);
	}

	public List<OTProducto> obtenerProductos()
	{
		return dbDipalza.getProductos();
	}

	public void grabarCliente(final OTCliente cliente)
	{
		dbDipalza.grabarCliente(cliente);
	}

	public void grabarProducto(final OTProducto producto)
	{
		dbDipalza.grabarProducto(producto);
	}
	
	public void grabarVenta(final OTVenta venta)
	{
		dbDipalza.grabarVenta(venta);
	}
	
	public void borrarVenta(final long idVenta)
	{
		dbDipalza.borrarVenta(idVenta);
	}
	
	public void limpiarBaseDatos()
	{
		dbDipalza.eliminarRegistros();
	}
	
	public OTVenta  obtenerVenta(final long idVenta)
	{
		return dbDipalza.obtenerVenta(idVenta);
	}

	public List<OTProductoVenta> obtenerItemesVenta(long idVenta)
	{
		return dbDipalza.obtenerItemesVenta(idVenta);
	}
	
	public List<OTVenta> obtenerListadoVentas()
	{
		return dbDipalza.obtenerListadoVentas();
	}
	
	public OTResumenVentas obtenerResumenVenta()
	{
		return dbDipalza.obtenerResumenVenta();
	}

	public List<String> obtenerEpesciales() { return dbDipalza.obtenerEspeciales();}
	public boolean esEspecial(String code) { return dbDipalza.esEspecial(code);}
	public void grabarEspecial(String especial) { dbDipalza.grabarEspecial(especial);}
	public void grabarEspecial(List<String> especiales) { dbDipalza.grabarEspecial(especiales);}
}
