package main.dipalza.modelo.vistas;

import java.util.List;

import main.dipalza.ot.OTCliente;
import main.dipalza.ot.OTProducto;
import main.dipalza.ot.OTProductoVenta;
import main.dipalza.ot.OTVenta;

public interface IModeloDipalza {

	/**
	 * Obtiene lista de clientes disponibles en el sistema.
	 * 
	 * @return Lista de clientes.
	 */
	List<OTCliente> obtenerClientes();

	/**
	 * Obtiene lista de productos disponibles en el sistema.
	 * 
	 * @return Lista de productos.
	 */
	List<OTProducto> obtenerProductos();
	
	
	OTVenta  obtenerVenta(final long idVenta);
	
	List<OTProductoVenta> obtenerItemesVenta(long idVenta);
	
	 List<OTVenta> obtenerListadoVentas();
}
