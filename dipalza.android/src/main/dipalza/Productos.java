package main.dipalza;

import java.util.LinkedList;
import java.util.List;

import main.dipalza.factory.Fabrica;
import main.dipalza.ot.OTProducto;
import main.dipalza.ot.OTProductoVenta;
import main.dipalza.ot.decorador.DecProductoArticulo;
import main.dipalza.ot.decorador.DecProductoNombre;
import main.dipalza.utilitarios.EmisorMensajes;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

public class Productos extends DashboardActivity /* implements TextWatcher */ {
    /**
     * Constante de producto agregado a la venta.
     */
    private static final String PRODUCTO_VENTA = "productoVenta";
    /**
     * Constante de lista de productos.
     */
    public static final String LISTA_PRODUCTOS = "listaProductos";
    /**
     * Campo de autocompletado de datos de productos.
     */
    private AutoCompleteTextView autoCompleteProducto;
    /**
     * Campo de autocompletado de datos de productos por articulo.
     */
    private AutoCompleteTextView autoCompleteArticulo;
    /**
     * Listra de productos decorados por articulo.
     */
    private List<DecProductoArticulo> listaArticulos;
    /**
     * Lista de productos por nombre.
     */
    private List<DecProductoNombre> listaNombres;
    
    private List<String> lstCodigosEspeciales;
    /**
     * Producto seleccionado.
     */
    private OTProducto productoSeleccionado;
    /**
     * Componente grafico.
     */
    private TextView textPrecio;
    /**
     * Componente grafico.
     */
    private TextView textCantidad;
    /**
     * Componente grafico.
     */
    private TextView textDescuento;
    /**
     * Componente grafico.
     */
    private TextView textPrecioNeto;
    /**
     * Componente grafico.
     */
    private TextView txtStock;
    /**
     * Objeto producto en proceso de venta.
     */
    private OTProductoVenta otProductoVenta;
    /**
     * Componente grafico.
     */
    private ImageButton buttonAceptar;
    /**
     * Componente grafico.
     */
    private ImageButton buttonCancelar;
    private TextView txtUnidad;
    private boolean internal;
    private double doublePrecio;
    private double doubleCantidad;
    private double doubleDescuento;
    private double doublePrecioNeto;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productos);
        setTitleFromActivityLabel(R.id.title_text);
        inicializarDecoradoresListaProductos();
        inicializarComponentesGraficos();
        inicializarAutocompletePorArticulo();
        inicializarAutocompletePorNombre();
        Bundle bundle = getIntent().getExtras();
        otProductoVenta = (OTProductoVenta) bundle.get("productoSeleccionado");
        if (otProductoVenta != null) {
            inicializacionModificacionProducto();
        }
    }

    /**
     * Metodo que inicialida componentes de TextView.
     */
    private void inicializarComponentesGraficos() {
        textPrecio = (TextView) findViewById(R.id.textPrecioProducto);
        textCantidad = (TextView) findViewById(R.id.textCantidad);
        textDescuento = (TextView) findViewById(R.id.textDescuento);
        textPrecioNeto = (TextView) findViewById(R.id.textNetoProducto);
        txtUnidad = (TextView) findViewById(R.id.textUnidadProducto);
        txtStock = (TextView) findViewById(R.id.txtStock);
        textPrecio.addTextChangedListener(new AbstractTextWatcher() {
            public void afterTextChanged(Editable s) {
                calcularPrecioVenta();
            }
        });
        textCantidad.addTextChangedListener(new AbstractTextWatcher() {
            public void afterTextChanged(Editable s) {
                calcularPrecioVenta();
            }
        });
        textDescuento.addTextChangedListener(new AbstractTextWatcher() {
            public void afterTextChanged(Editable s) {
                calcularPrecioVenta();
            }
        });
        buttonAceptar = (ImageButton) findViewById(R.id.imgBtnAceptarProducto);
        buttonAceptar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                agregarProducto();
            }
        });
        buttonCancelar = (ImageButton) findViewById(R.id.imgBtnCancelarProducto);
        buttonCancelar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                cancelarProducto();
            }
        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Inicializacion de producto en proceso de modificacion.l
     */
    private void inicializacionModificacionProducto() {
        this.autoCompleteArticulo.setText(otProductoVenta.getProducto().getArticulo());
        this.autoCompleteArticulo.setEnabled(false);
        this.autoCompleteArticulo.setDropDownHeight(0);

        this.autoCompleteProducto.setText(otProductoVenta.getProducto().getNombre());
        this.autoCompleteProducto.setEnabled(false);
        this.autoCompleteProducto.setDropDownHeight(0);
        this.textPrecio.setText(Fabrica.getDecimalFormat().format(otProductoVenta.getProducto().getPrecio()));
        this.textCantidad.setText(String.valueOf(otProductoVenta.getCantidad()));
        this.textDescuento.setText(String.valueOf((int) otProductoVenta.getDescuento()));
        this.textPrecioNeto.setText(Fabrica.getDecimalFormat().format(otProductoVenta.getPrecioNeto()));
        this.txtUnidad.setText(otProductoVenta.getProducto().getUnidad());
        this.productoSeleccionado = otProductoVenta.getProducto();
        double stock = otProductoVenta.getProducto().getStock() + otProductoVenta.getCantidad();
        this.txtStock.setText(String.format("%7.2f [%s]", stock, otProductoVenta.getProducto().getUnidad()));
        doublePrecio = otProductoVenta.getProducto().getPrecio();
        doubleCantidad = otProductoVenta.getCantidad();
        doubleDescuento = otProductoVenta.getDescuento();
        doublePrecioNeto = otProductoVenta.getPrecioNeto();
    }

    /**
     * Metodo que inicializa campo de autocomplete de producto por articulo.
     */
    private void inicializarAutocompletePorArticulo() {
        autoCompleteArticulo = (AutoCompleteTextView) findViewById(R.id.autoCompleteArticulo);

        autoCompleteArticulo.setAdapter(new ArrayAdapter<DecProductoArticulo>(this,
                android.R.layout.simple_dropdown_item_1line, listaArticulos));
        autoCompleteArticulo.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> elemento, View arg1, int position, long arg3) {
                productoSeleccionado = ((DecProductoArticulo) elemento.getAdapter().getItem(position)).getObject();
                autoCompleteArticulo.setText(productoSeleccionado.getArticulo());
                autoCompleteProducto = (AutoCompleteTextView) findViewById(R.id.autoCompleteProducto);
                autoCompleteProducto.setText(productoSeleccionado.getNombre());
                
                boolean esEspecial = Fabrica.obtenerInstancia().obtenerModeloDipalza().esEspecial(productoSeleccionado.getArticulo());

                textPrecio.setFocusable(esEspecial);
                textPrecio.setEnabled(esEspecial);
                textPrecio.setClickable(esEspecial);
                textPrecio.setFocusableInTouchMode(esEspecial);
                
                completarDatosProductos();
            }
        });

        autoCompleteArticulo.addTextChangedListener(new AbstractTextWatcher() {
            public void afterTextChanged(Editable s) {
                if (Productos.this.autoCompleteArticulo.length() == 0 && !internal) {
                    limpiarControlArticulo();
                } else if (Productos.this.autoCompleteArticulo.length() == 3 && !internal) {
                    String codigo = Productos.this.autoCompleteArticulo.getText().toString();
                    for (DecProductoArticulo dec : listaArticulos) {
                        if (dec.getObject().getArticulo().equalsIgnoreCase(codigo)) {
                            productoSeleccionado = dec.getObject();
                            autoCompleteProducto = (AutoCompleteTextView) findViewById(R.id.autoCompleteProducto);
                            autoCompleteProducto.setText(productoSeleccionado.getNombre());
                            autoCompleteProducto.requestFocus();
                            autoCompleteProducto.dismissDropDown();
                            
                            boolean esEspecial = Fabrica.obtenerInstancia().obtenerModeloDipalza().esEspecial(productoSeleccionado.getArticulo());

                            textPrecio.setFocusable(esEspecial);
                            textPrecio.setEnabled(esEspecial);
                            textPrecio.setClickable(esEspecial);
                            textPrecio.setFocusableInTouchMode(esEspecial);
                            completarDatosProductos();
                            textCantidad.requestFocus();
                            break;
                        }
                    }
                }
            }
        });

        autoCompleteArticulo.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            }
        });


    }

    /**
     * Metodo que inicializa campo de autocomplete de producto por nombre.
     */
    private void inicializarAutocompletePorNombre() {
        autoCompleteProducto = (AutoCompleteTextView) findViewById(R.id.autoCompleteProducto);
        autoCompleteProducto.setAdapter(new ArrayAdapter<DecProductoNombre>(this,
                android.R.layout.simple_dropdown_item_1line, listaNombres));
        autoCompleteProducto.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> elemento, View arg1, int position, long arg3) {
                productoSeleccionado = ((DecProductoNombre) elemento.getAdapter().getItem(position)).getObject();
                autoCompleteProducto.setText(productoSeleccionado.getNombre());
                autoCompleteArticulo = (AutoCompleteTextView) findViewById(R.id.autoCompleteArticulo);
                autoCompleteArticulo.setText(productoSeleccionado.getArticulo());
                completarDatosProductos();

                boolean esEspecial = Fabrica.obtenerInstancia().obtenerModeloDipalza().esEspecial(productoSeleccionado.getArticulo());

                textPrecio.setFocusable(esEspecial);
                textPrecio.setEnabled(esEspecial);
                textPrecio.setClickable(esEspecial);
                textPrecio.setFocusableInTouchMode(esEspecial);

            }
        });
        autoCompleteProducto.addTextChangedListener(new AbstractTextWatcher() {
            public void afterTextChanged(Editable s) {
                limpiarControlProducto();
            }
        });
    }

    /**
     * Limpia controles de la interfaz.
     */
    protected void limpiarControlArticulo() {

        productoSeleccionado = null;
        internal = true;
        autoCompleteProducto.setText("");
        internal = false;
        textPrecio.setText("0");
        txtUnidad.setText("0");
        textPrecioNeto.setText("0");
        textCantidad.setText("0");
        textDescuento.setText("0");
        txtStock.setText("");
        doublePrecio = 0;
        doubleCantidad = 0;
        doubleDescuento = 0;
        doublePrecioNeto = 0;
    }

    /**
     * Limpia controles de la interfaz.
     */
    protected void limpiarControlProducto() {
        if (Productos.this.autoCompleteProducto.length() == 0 && !internal) {
            productoSeleccionado = null;
            internal = true;
            autoCompleteArticulo.setText("");
            internal = false;
            textPrecio.setText("0");
            txtUnidad.setText("0");
            textPrecioNeto.setText("0");
            textCantidad.setText("0");
            textDescuento.setText("0");
            txtStock.setText("");
            doublePrecio = 0;
            doubleCantidad = 0;
            doubleDescuento = 0;
            doublePrecioNeto = 0;
        }
    }

    /**
     * Metodo mque completa datos del producto seleccionado en los controles de
     * autocompletacion..
     */
    private void completarDatosProductos() {
        textPrecio.setText("0");
        if (productoSeleccionado != null) {
            if (productoSeleccionado.getPrecio() != null) {
                textPrecio.setText(Fabrica.getDecimalFormat().format(productoSeleccionado.getPrecio()));
                doublePrecio = productoSeleccionado.getPrecio();
            }
            txtUnidad.setText(productoSeleccionado.getUnidad());
            this.txtStock.setText(String.format("%s[%s]",
                    Fabrica.getDecimalFormat().format(productoSeleccionado.getStock()),
                    productoSeleccionado.getUnidad()));
        }
    }

    /**
     * Metodo que genera decorador de la liosta de productos por articulo y por
     * nombre.
     */
    private void inicializarDecoradoresListaProductos() {
        List<OTProducto> listaProductos = Fabrica.obtenerInstancia().obtenerModeloDipalza().obtenerProductos();
        listaArticulos = new LinkedList<DecProductoArticulo>();
        listaNombres = new LinkedList<DecProductoNombre>();
        for (OTProducto elemento : listaProductos) {
            DecProductoArticulo productoArticulo = new DecProductoArticulo(elemento);
            DecProductoNombre productoNombre = new DecProductoNombre(elemento);
            listaArticulos.add(productoArticulo);
            listaNombres.add(productoNombre);
        }
    }

    /**
     * Metodo que se ejecuta desplues de cambiar el texto de los campos cantidad
     * y descuento.
     */
    public void calcularPrecioVenta() {
        try {
            doubleCantidad = Double.parseDouble(Productos.this.textCantidad.getText().toString());
        } catch (Exception exception) {
        }
        try {
            doubleDescuento = Double.parseDouble(Productos.this.textDescuento.getText().toString());
        } catch (Exception exception) {
        }
        double valorPrecioNeto = doublePrecio * doubleCantidad;
        doublePrecioNeto = valorPrecioNeto * (1.0 - (doubleDescuento / 100d));
        textPrecioNeto.setText(Fabrica.getDecimalFormat().format(doublePrecioNeto));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuproducto, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemAceptarProducto:
                agregarProducto();
                return true;
            case R.id.itemCancelarProducto:
                cancelarProducto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Metodo que cancela el proceso de agregar un producto a la tabla de ventas
     * de productos.
     */
    private void cancelarProducto() {
        finish();
    }

    /**
     * Metodo que agrega producto a la tabla de ventas de productos.
     */
    private void agregarProducto() {
        if (validadorDatosProducto()) {
            crearProductoVenta();
            Intent resultIntent;
            resultIntent = new Intent();
            resultIntent.putExtra(PRODUCTO_VENTA, otProductoVenta);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    /**
     * Metodo que valida los datos del producto.
     *
     * @return Verdadero si se ingresaron datos basicos.
     */
    private boolean validadorDatosProducto() {
        boolean retorno = true;
        if (productoSeleccionado == null) {
            EmisorMensajes.mostrarMensaje(this, "Producto", "Debe ingresar producto a ingresar.");
            retorno = false;
        } else if (Productos.this.textCantidad.getText().toString() == null
                || Productos.this.textCantidad.getText().toString().equals("")) {
            EmisorMensajes.mostrarMensaje(this, "Producto", "Debe ingresar cantidad de producto.");
            retorno = false;
        }
        return retorno;
    }

    /**
     * Metodo que crea producto a agregar en la venta.
     */
    private void crearProductoVenta() {
        if (otProductoVenta == null) {
            otProductoVenta = new OTProductoVenta();
            otProductoVenta.setProducto(productoSeleccionado);
            otProductoVenta.setIdProducto(productoSeleccionado.getIdProducto());
            otProductoVenta.setNombre(productoSeleccionado.getNombre());
        }
        otProductoVenta.setCantidad(doubleCantidad);
        otProductoVenta.setDescuento(doubleDescuento);
        otProductoVenta.setPrecioNeto(doublePrecioNeto);
    }
}