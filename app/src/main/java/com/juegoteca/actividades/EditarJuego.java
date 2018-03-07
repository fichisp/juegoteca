package com.juegoteca.actividades;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.juegoteca.basedatos.Juego;
import com.juegoteca.basedatos.JuegosSQLHelper;
import com.juegoteca.util.TextWatcherFechas;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

public class EditarJuego extends Activity {

	private JuegosSQLHelper juegosSQLH;
	private String idJuego;
	private static final int IMAGEN_SELECCIONADA = 1;
	// private String selectedImagePath = null;
	private Uri caratulaOriginal, caratulaTemporal;
	private Juego juegoDetalle, juegoEditado;
	private Utilidades utilidades;
	private ImageView imageCaratula;
	private EditText textTitulo, textComentario, textPuntuacion, textResumen,
			textFechaLanzamiento, textFechaCompra, textPrecio,
			textFechaCompletado, textEan;
	private AutoCompleteTextView autoCompania;
	private CheckBox checkCompletado;
	private Spinner spinnerGenero, spinnerPlataforma, spinnerClasificacion,
			spinnerIdioma, spinnerFormato;
	private String[] valoresBusqueda;
	private boolean esJuegoOnline;

	/**
	 * Llamada cuando se inicializa la actividad. Inicializa los spinner con los
	 * datos necesarios y carga el resto de componentes con los datos del juego
	 * que se edita.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editar_juego);
		Intent intent = getIntent();
		juegosSQLH = new JuegosSQLHelper(this);
		idJuego = intent.getStringExtra("ID_JUEGO");
		valoresBusqueda = intent.getStringArrayExtra("VALORES");
		esJuegoOnline = intent.getExtras().getBoolean("ONLINE");
		// Inicializar los spinner
		utilidades = new Utilidades(this);
		utilidades
				.cargarPlataformas((Spinner) findViewById(R.id.spinner_plataforma_editar));
		utilidades
				.cargarClasificacion((Spinner) findViewById(R.id.spinner_clasificacion_editar));
		utilidades
				.cargarIdioma((Spinner) findViewById(R.id.spinner_idioma_editar));
		utilidades
				.cargarGeneros((Spinner) findViewById(R.id.spinner_genero_editar));
		utilidades
				.cargarCompanias((AutoCompleteTextView) findViewById(R.id.autoComplete_compania_editar));
		utilidades
				.cargarFormatos(((Spinner) findViewById(R.id.spinner_formato_editar)));
		textTitulo = ((EditText) this.findViewById(R.id.edit_titulo_editar));
		imageCaratula = ((ImageView) this
				.findViewById(R.id.image_view_caratula_editar));
		textComentario = (EditText) this
				.findViewById(R.id.edit_comentario_editar);
		textPuntuacion = (EditText) this
				.findViewById(R.id.edit_puntuacion_editar);
		spinnerGenero = ((Spinner) this
				.findViewById(R.id.spinner_genero_editar));
		spinnerPlataforma = ((Spinner) this
				.findViewById(R.id.spinner_plataforma_editar));
		spinnerClasificacion = ((Spinner) this
				.findViewById(R.id.spinner_clasificacion_editar));
		textResumen = ((EditText) this.findViewById(R.id.edit_resumen_editar));
		spinnerIdioma = ((Spinner) this
				.findViewById(R.id.spinner_idioma_editar));
		spinnerFormato = ((Spinner) this
				.findViewById(R.id.spinner_formato_editar));
		textFechaLanzamiento = ((EditText) this
				.findViewById(R.id.edit_fecha_lanzamiento_editar));
		textFechaCompra = ((EditText) this
				.findViewById(R.id.edit_fecha_compra_editar));
		textPrecio = ((EditText) this.findViewById(R.id.edit_precio_editar));
		autoCompania = ((AutoCompleteTextView) this
				.findViewById(R.id.autoComplete_compania_editar));
		checkCompletado = ((CheckBox) this
				.findViewById(R.id.check_completado_editar));
		textFechaCompletado = ((EditText) this
				.findViewById(R.id.edit_fecha_completado_editar));
		textEan = ((EditText) this.findViewById(R.id.edit_ean_editar));
		textTitulo.requestFocus();
		// Intentar recuperar el juego a visualizar y cargar los datos en los
		// controles
		Log.v("ID del juego a mostrar: ", String.valueOf(idJuego));
		Cursor c = juegosSQLH.buscarJuegoID(idJuego);
		Log.v("CURSOR ID - Elementos: ", String.valueOf(c.getCount()));
		if (c != null && c.moveToFirst()) {
			Log.v("CURSOR ID - Columnas: ", String.valueOf(c.getColumnCount()));
			juegoDetalle = new Juego(c.getInt(0), c.getString(1),
					c.getString(2), c.getString(3), c.getInt(4), c.getInt(5),
					c.getInt(6), c.getInt(7), c.getString(8), c.getString(9),
					c.getFloat(10), c.getInt(11), c.getString(12),
					c.getString(13), c.getString(14), c.getString(15),
					c.getString(16), c.getInt(17), c.getString(18));
			Log.v("JUEGO DETALLE", juegoDetalle.toString());
			if ((juegoDetalle.getCaratula()).length() == 0) {
				imageCaratula.setImageDrawable((this.getResources()
						.getDrawable(R.drawable.sinimagen)));
			} else {
				imageCaratula.setImageURI(Uri.parse(this.getFilesDir()
						.getPath() + "/" + juegoDetalle.getCaratula()));
			}
			textTitulo.setText(juegoDetalle.getTitulo());
			autoCompania.setText(juegoDetalle.getCompania());
			// Hay que restar 1 a la posición de los elementos porque empiezan
			// en 0 y los IDs en 1
			spinnerGenero.setSelection(juegoDetalle.getGenero() - 1);
			spinnerPlataforma.setSelection(juegoDetalle.getPlataforma() - 1);
			spinnerClasificacion
					.setSelection(juegoDetalle.getClasificacion() - 1);
			spinnerIdioma.setSelection(juegoDetalle.getIdioma() - 1);
			textResumen.setText(juegoDetalle.getResumen());
			if (Integer.parseInt(juegoDetalle.getFechaLanzamiento()) != 0) {
				textFechaLanzamiento.setText(utilidades
						.convertirMilisegundosAFecha(juegoDetalle
								.getFechaLanzamiento()));
			}
			if (Integer.parseInt(juegoDetalle.getFechaCompra()) != 0) {
				textFechaCompra.setText(utilidades
						.convertirMilisegundosAFecha(juegoDetalle
								.getFechaCompra()));
			}

			if (juegoDetalle.getPrecio() != 0) {

				textPrecio.setText(String.valueOf(juegoDetalle.getPrecio()));
			} else {
				textPrecio.setText("");
			}

			if (juegoDetalle.getCompletado() == 1) {
				checkCompletado.setChecked(true);
				if (Double.parseDouble(juegoDetalle.getFechaCompletado()) != 0) {
					textFechaCompletado.setText(utilidades
							.convertirMilisegundosAFecha(juegoDetalle
									.getFechaCompletado()));
				}
				textFechaCompletado.setEnabled(true);
			}
			if (!juegoDetalle.getEan().contains("mjtc")) {
				textEan.setText(String.valueOf(juegoDetalle.getEan()));
			}
			if (juegoDetalle.getComentario().length() != 0
					&& juegoDetalle.getComentario().compareToIgnoreCase("null") != 0) {
				textComentario.setText(juegoDetalle.getComentario());
			} else {
				textComentario.setText("");
			}

			if (juegoDetalle.getPuntuacion() != 0) {
				textPuntuacion.setText(String.valueOf(juegoDetalle
						.getPuntuacion()));
			}

			if ("".equals(juegoDetalle.getFormato())
					|| juegoDetalle.getFormato().compareTo("F") == 0) {
				spinnerFormato.setSelection(0);
			} else {
				spinnerFormato.setSelection(1);
			}
			
			c.close();
		}
		// TextWatcher para las fechas
		textFechaLanzamiento.addTextChangedListener(new TextWatcherFechas(
				textFechaLanzamiento));
		textFechaCompletado.addTextChangedListener(new TextWatcherFechas(
				textFechaCompletado));
		textFechaCompra.addTextChangedListener(new TextWatcherFechas(
				textFechaCompra));

		imageCaratula.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				cargarCaratula(v);
				return false;
			}
		});
	}

	/**
	 * Accede a las imágenes del dispositivo para elegir una carátula para la
	 * ficha
	 * 
	 * @param view
	 */
	public void cargarCaratula(View view) {
		((ImageView) findViewById(R.id.image_view_caratula_editar))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(
								Intent.createChooser(intent, "Select Picture"),
								IMAGEN_SELECCIONADA);
					}
				});
	}

	/**
	 * Actualiza el juego en la base de datos
	 * 
	 * @param view
	 */
	public void actualizarJuego() {
		juegoEditado = new Juego();
		// Establecemos el valor de los atributos recuperados del formulario
		juegoEditado.setId(Integer.parseInt(idJuego));
		juegoEditado.setTitulo(textTitulo.getText().toString());
		juegoEditado.setCompania(autoCompania.getText().toString());
		juegoEditado.setGenero(spinnerGenero.getSelectedItemPosition() + 1);
		juegoEditado.setResumen(textResumen.getText().toString());
		juegoEditado
				.setPlataforma(spinnerPlataforma.getSelectedItemPosition() + 1);
		juegoEditado.setClasificacion(spinnerClasificacion
				.getSelectedItemPosition() + 1);
		juegoEditado.setIdioma(spinnerIdioma.getSelectedItemPosition() + 1);

		String fechaLanzamiento = textFechaLanzamiento.getText().toString();

		if (fechaLanzamiento.length() == 0) {
			// Toast toast = Toast.makeText(getApplicationContext(),
			// "El campo FECHA DE LANZAMIENTO no puede estar en blanco",
			// Toast.LENGTH_SHORT);
			// // toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,0,0);
			// toast.show();
			// return;
			juegoEditado.setFechaLanzamiento("0");
		} else {
			if (utilidades.validaFecha(fechaLanzamiento) != 0) {
				Toast toast = Toast
						.makeText(
								getApplicationContext(),
								getString(R.string.campo_fecha_lanzamiento_invalido),
								Toast.LENGTH_SHORT);
				// toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,0,0);
				toast.show();
				return;
			} else {
				juegoEditado.setFechaLanzamiento(utilidades
						.convertirFechaAMilisegundos(fechaLanzamiento));

			}
		}
		String fechaCompra = textFechaCompra.getText().toString();

		if (fechaCompra.length() == 0) {
			juegoEditado.setFechaCompra("0");
		} else {
			if (utilidades.validaFecha(fechaCompra) != 0) {
				Toast toast = Toast.makeText(getApplicationContext(),
						getString(R.string.campo_fecha_compra_invalido),
						Toast.LENGTH_SHORT);
				toast.show();
				return;
			} else {
				juegoEditado.setFechaCompra(utilidades
						.convertirFechaAMilisegundos(fechaCompra));
			}
		}

		try {
			juegoEditado.setPrecio(Float.parseFloat(textPrecio.getText()
					.toString()));
		} catch (NumberFormatException nfe) {
			juegoEditado.setPrecio(0);
		}

		if (checkCompletado.isChecked()) {
			juegoEditado.setCompletado(1);

			String fechaCompletado = textFechaCompletado.getText().toString();
			if (fechaCompletado != null && fechaCompletado.length() > 0) {
				if (utilidades.validaFecha(fechaCompletado) != 0) {
					Toast toast = Toast
							.makeText(
									getApplicationContext(),
									getString(R.string.campo_fecha_completado_invalido),
									Toast.LENGTH_SHORT);
					//toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
					toast.show();
					return;
				} else {
					juegoEditado.setFechaCompletado(utilidades
							.convertirFechaAMilisegundos(fechaCompletado));
				}
			} else {
				juegoEditado.setFechaCompletado("0");
			}
			// juegoEditado.setFechaCompletado(utilidades.convertirFechaAMilisegundos(textFechaCompletado.getText().toString()));
		} else {
			juegoEditado.setCompletado(0);
		}
		// juegoEditado.setEan(Long.parseLong(textEan.getText().toString()));
		if (textEan.getText().toString().length() != 0) {
			juegoEditado.setEan(textEan.getText().toString());
		} else {
			// TODO:string
			// Toast toast = Toast.makeText(getApplicationContext(),
			// "El campo CÓDIGO DE PRODUCTO no puede estar en blanco",
			// Toast.LENGTH_SHORT);
			// toast.show();
			// return;
			String codigo = "mjtc"
					+ utilidades.encriptar(juegoEditado.getTitulo()
							+ juegoEditado.getPlataforma()
							+ juegoEditado.getCompania());
			juegoEditado.setEan(codigo);
		}

		// Formato
		if (spinnerFormato.getSelectedItemPosition() == 0) {
			juegoEditado.setFormato("F");
		} else {
			juegoEditado.setFormato("D");
		}

		// Movemos la carátula de la carpeta donde esté a la carpeta de la
		// apliacion y la reducimos de tamaño
		try {
			if (caratulaTemporal != null) {
				String nombre = utilidades.encriptar(juegoEditado.getTitulo()
						+ juegoEditado.getEan());
				if (utilidades.caratulaDefinitiva(caratulaTemporal, nombre)) {
					juegoEditado.setCaratula(nombre + ".png");
				}
			} else {
				juegoEditado.setCaratula(juegoDetalle.getCaratula());
			}
		} catch (Exception e) {
			Toast toast = Toast
					.makeText(
							getApplicationContext(),
							getString(R.string.error_cargar_caratula),
							Toast.LENGTH_SHORT);
			toast.show();
			e.printStackTrace();
			juegoEditado.setCaratula("");
		}

		Log.v("JUEGO EDITADO", juegoEditado.toString());

		juegoEditado.setCompletado(checkCompletado.isChecked() ? 1 : 0);
		if (textComentario.getText().toString().length() > 0) {

			juegoEditado.setComentario(textComentario.getText().toString());
		} else {
			juegoEditado.setComentario("-");
		}
		try {
			int puntuacion = Integer.parseInt(textPuntuacion.getText()
					.toString());
			if (puntuacion > 100) {
				Toast toast = Toast.makeText(getApplicationContext(),
						getString(R.string.campo_puntuacion_invalido),
						Toast.LENGTH_SHORT);
				toast.show();
				return;
			}
			juegoEditado.setPuntuacion(puntuacion);
			// juegoEditado.setPuntuacion(Integer.parseInt(textPuntuacion.getText().toString()));

		} catch (NumberFormatException nfe) {
			juegoEditado.setPuntuacion(0);
		}

		// Actualizamos el juego
		long id;
		if ((id = juegosSQLH.actualizarJuego(juegoEditado)) > 0) {
			Toast toast = Toast.makeText(getApplicationContext(),
					getString(R.string.juego_actualizado_ok), Toast.LENGTH_SHORT);
			toast.show();
			Log.v("ID ACTUALIZADO: ", idJuego);

			final SharedPreferences settings = getSharedPreferences("UserInfo",
					0);
			Intent intent;
			if(settings.contains("detalle_imagen") && settings.getBoolean("detalle_imagen",true)){

				intent = new Intent(this, DetalleJuegoImagenGrande.class);

			} else {
				intent = new Intent(this, DetalleJuego.class);

			}

			//Intent intent = new Intent(this, DetalleJuego.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("ID_JUEGO", idJuego);
			intent.putExtra("EDITADO", true);
			intent.putExtra("NUEVO_JUEGO", false);
			String caller = getIntent().getStringExtra("CALLER");
			if (caller != null) {
				intent.putExtra("CALLER", caller);
				if (caller != null && caller.compareTo("ListadoJuego") == 0) {
					intent.putExtra("VALORES", valoresBusqueda);
					intent.putExtra("ONLINE", esJuegoOnline);
				}
			}

			startActivity(intent);
		} else {
			if (id == -5) {
				Toast toast = Toast.makeText(getApplicationContext(),
						getString(R.string.juego_codigo_duplicado), Toast.LENGTH_SHORT);
				toast.show();
				return;
			} else {
				Toast toast = Toast.makeText(getApplicationContext(),
						getString(R.string.juego_actualizado_ko), Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	/**
	 * Procesar el escaneo de la cámara para obtener el código leído o la imagen
	 * seleccionada
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Procesar la imagen
		if (resultCode == RESULT_OK) {
			// Imagen de la carátula desde el sistema de ficheros
			if (requestCode == IMAGEN_SELECCIONADA) {
				Uri selectedImageUri = data.getData();
				imageCaratula.setImageURI(caratulaTemporal = utilidades
						.caratulaTemporal(selectedImageUri));
				Log.v("CARGAR CARATULA",
						"URI Temporal: " + caratulaTemporal.toString());
			}
		}
		// Procesar el código escaneado
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = data.getStringExtra("SCAN_RESULT");
				((EditText) this.findViewById(R.id.edit_ean_editar))
						.setText(contents);
			}
			// else if (resultCode == RESULT_CANCELED) {
			// }
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nuevo_juego, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_guardar:
			Log.i("ActionBar", "Guardar el juego");
			actualizarJuego();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Activa el campo de fecha si se marca el checkbox de completado
	 * 
	 * @param view
	 */
	public void activarFechaCompletado(View view) {
		EditText fechaCompletado = (EditText) this
				.findViewById(R.id.edit_fecha_completado_editar);
		if (((CheckBox) this.findViewById(R.id.check_completado_editar))
				.isChecked()) {
			fechaCompletado.setEnabled(true);
		} else {
			fechaCompletado.setEnabled(false);
			fechaCompletado.setText("");
		}
	}

	/**
	 * Lanza la actividad que se encarga de escanear el código
	 * 
	 * @param view
	 */
	public void capturarCodigo(View view) {
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
		startActivityForResult(intent, 0);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, DetalleJuego.class);
		intent.putExtra("ID_JUEGO", idJuego);
		intent.putExtra("NUEVO_JUEGO", false);
		startActivity(intent);
		finish();
	}
}