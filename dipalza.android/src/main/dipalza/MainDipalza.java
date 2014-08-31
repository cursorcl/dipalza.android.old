package main.dipalza;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainDipalza extends DashboardActivity {

	/**
	 * Referencia a controlador de sistema.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menumaindipalza, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemVentas:
			realizarVentas();
			return true;
		case R.id.itemInicializacion:
			realizarInicializacion();
			return true;
		case R.id.itemResumen:
			visaulizarResumenVentas();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	/**
	 * Metodo que visualiza resumen de ventas.
	 */
	private void visaulizarResumenVentas() {
		Intent intent = new Intent(this, VentasResumen.class);
		this.startActivity(intent);		
	}

	/**
	 * Metodo que solicita al controlador inicializar interfaz de
	 * inicialziacion.
	 */
	private void realizarInicializacion() {
		Intent intent = new Intent(this, ActivityConfiguracion.class);
		this.startActivity(intent);
	}

	/**
	 * Metodo que solicita al controlador inicializar interfaz de
	 * inicializacion.
	 */
	private void realizarVentas() {
		Intent intent = new Intent(this, VentaRegistros.class);		
		this.startActivity(intent);
	}
}