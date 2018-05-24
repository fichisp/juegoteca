package com.juegoteca.actividades;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
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


public class NuevoJuego extends Activity {

    private static final int IMAGEN_SELECCIONADA = 1;
    private JuegosSQLHelper juegosSQLH;
    private Uri caratulaTemporal;
    private Utilidades utilidades;
    private EditText editTituloNuevo, editCompaniaNuevo, editResumenNuevo,
            editFechaLanzamiento, editFechaCompra, editFechaCompletado,
            editPrecioNuevo, editEanNuevo, comentarioNuevo, puntuacionNuevo;
    private Spinner spinnerGeneroNuevo, spinnerPlataformaNuevo,
            spinnerClasificacionNuevo, spinnerIdiomaNuevo, spinnerFormatoNuevo;
    private CheckBox checkCompletadoNuevo;
    private ImageView imageCaratula;


    /**
     * Llamada cuando se inicializa la actividad. Inicializa los componentes y
     * carga los datos en los spinner.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_juego);
        setupActionBar();
        juegosSQLH = new JuegosSQLHelper(this);
        utilidades = new Utilidades(this);
        utilidades
                .cargarPlataformas((Spinner) findViewById(R.id.spinner_plataforma_nuevo));
        utilidades
                .cargarClasificacion((Spinner) findViewById(R.id.spinner_clasificacion_nuevo));
        utilidades
                .cargarIdioma((Spinner) findViewById(R.id.spinner_idioma_nuevo));
        utilidades
                .cargarGeneros((Spinner) findViewById(R.id.spinner_genero_nuevo));
        utilidades
                .cargarFormatos(((Spinner) findViewById(R.id.spinner_formato_nuevo)));
        utilidades
                .cargarCompanias((AutoCompleteTextView) findViewById(R.id.autoComplete_compania_nuevo));
        // Inicializar los controles
        editTituloNuevo = ((EditText) this.findViewById(R.id.edit_titulo_nuevo));
        editCompaniaNuevo = ((EditText) this
                .findViewById(R.id.autoComplete_compania_nuevo));
        spinnerGeneroNuevo = ((Spinner) findViewById(R.id.spinner_genero_nuevo));
        editResumenNuevo = ((EditText) this
                .findViewById(R.id.edit_resumen_nuevo));
        spinnerPlataformaNuevo = ((Spinner) findViewById(R.id.spinner_plataforma_nuevo));
        spinnerClasificacionNuevo = ((Spinner) findViewById(R.id.spinner_clasificacion_nuevo));
        spinnerFormatoNuevo = ((Spinner) findViewById(R.id.spinner_formato_nuevo));
        spinnerIdiomaNuevo = ((Spinner) findViewById(R.id.spinner_idioma_nuevo));
        editFechaLanzamiento = ((EditText) this
                .findViewById(R.id.edit_fecha_lanzamiento_nuevo));
        editFechaCompra = ((EditText) this
                .findViewById(R.id.edit_fecha_compra_nuevo));
        checkCompletadoNuevo = ((CheckBox) this
                .findViewById(R.id.check_completado_nuevo));
        editFechaCompletado = ((EditText) this
                .findViewById(R.id.edit_fecha_completado_nuevo));
        editPrecioNuevo = ((EditText) this.findViewById(R.id.edit_precio_nuevo));
        editEanNuevo = ((EditText) this.findViewById(R.id.edit_ean_nuevo));
        imageCaratula = ((ImageView) findViewById(R.id.image_view_caratula_nuevo));
        comentarioNuevo = ((EditText) this
                .findViewById(R.id.edit_comentario_nuevo));
        puntuacionNuevo = ((EditText) this
                .findViewById(R.id.edit_puntuacion_nuevo));
        editTituloNuevo.requestFocus();

        // TextWatcher para las fechas
        editFechaLanzamiento.addTextChangedListener(new TextWatcherFechas(
                editFechaLanzamiento));
        editFechaCompletado.addTextChangedListener(new TextWatcherFechas(
                editFechaCompletado));
        editFechaCompra.addTextChangedListener(new TextWatcherFechas(
                editFechaCompra));

        // TouchListner para evitar el doble "clik" y lanzar la acción de buscar
        // imagen solo tocando
        imageCaratula.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                cargarCaratula(v);
                return false;
            }
        });
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nuevo_juego, menu);
        return true;
    }

    /**
     * Permite buscar una imagen en el dispositivo para asociarla como la
     * carátula del juego que estamos creando
     *
     * @param view
     */
    public void cargarCaratula(View view) {
        imageCaratula.setOnClickListener(new OnClickListener() {
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
     * Crea un nuevo juego en la base de datos
     */
    private void crearJuego() {
        // Comprobamos que los campos titulo y compañia están rellenados
        if (editTituloNuevo.getText().toString().length() == 0
                || editCompaniaNuevo.getText().toString().length() == 0) {
            if (editTituloNuevo.getText().toString().length() == 0) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.campo_titulo_vacio),
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                if (editCompaniaNuevo.getText().toString().length() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.campo_compania_vacio),
                            Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            }
        } else {
            Juego juego = new Juego();
            // Establecemos el valor de los atributos recuperados del formulario
            juego.setTitulo(editTituloNuevo.getText().toString());
            juego.setCompania(editCompaniaNuevo.getText().toString());
            juego.setGenero(spinnerGeneroNuevo.getSelectedItemPosition() + 1);
            juego.setResumen(editResumenNuevo.getText().toString());

            // Sumar 1 a la posicion en el spinner para obtener el ID asociado
            // de cada elemento
            juego.setPlataforma(spinnerPlataformaNuevo
                    .getSelectedItemPosition() + 1);
            juego.setClasificacion(spinnerClasificacionNuevo
                    .getSelectedItemPosition() + 1);
            juego.setIdioma(spinnerIdiomaNuevo.getSelectedItemPosition() + 1);

            // Formato
            if (spinnerFormatoNuevo.getSelectedItemPosition() == 0) {
                juego.setFormato("F");
            } else {
                juego.setFormato("D");
            }

            // Validar las fechas antes de guardar
            String fechaLanzamiento = editFechaLanzamiento.getText().toString();
            if (fechaLanzamiento.length() == 0) {
                juego.setFechaLanzamiento("0");
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
                    juego.setFechaLanzamiento(utilidades
                            .convertirFechaAMilisegundos(fechaLanzamiento));
                }
            }
            String fechaCompra = editFechaCompra.getText().toString();
            if (fechaCompra.length() == 0) {
                juego.setFechaCompra("0");
            } else {
                if (utilidades.validaFecha(fechaCompra) != 0) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.campo_fecha_compra_invalido),
                            Toast.LENGTH_SHORT);
                    //toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
                    toast.show();
                    return;
                } else {
                    juego.setFechaCompra(utilidades
                            .convertirFechaAMilisegundos(fechaCompra));
                }
            }
            // Comprobamos el checkbox para establecer el valor de atributo
            // "completado" y la fecha
            if (checkCompletadoNuevo.isChecked()) {
                juego.setCompletado(1);
                String fechaCompletado = editFechaCompletado.getText()
                        .toString();
                if (fechaCompletado != null && fechaCompletado.length() > 0) {
                    if (utilidades.validaFecha(fechaCompletado) != 0) {
                        Toast toast = Toast
                                .makeText(
                                        getApplicationContext(),
                                        getString(R.string.campo_fecha_completado_invalido),
                                        Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    } else {
                        juego.setFechaCompletado(utilidades
                                .convertirFechaAMilisegundos(fechaCompletado));
                    }
                } else {
                    juego.setFechaCompletado("0");
                }

            } else {
                juego.setCompletado(0);
            }
            // Si no se indica un precio, se pone a 0
            try {
                juego.setPrecio(Float.parseFloat(editPrecioNuevo.getText()
                        .toString()));
            } catch (NumberFormatException nfe) {
                juego.setPrecio(0);
            }
            // El campo EAN
            if (editEanNuevo.getText().toString().length() != 0) {
                juego.setEan(editEanNuevo.getText().toString());
            } else {
                // Generar un código
                String codigo = "mjtc"
                        + utilidades.encriptar(juego.getTitulo()
                        + juego.getPlataforma() + juego.getCompania());
                juego.setEan(codigo);
            }
            // Comentario
            try {
                juego.setComentario(comentarioNuevo.getText().toString());
            } catch (NullPointerException npe) {
                juego.setComentario("-");
            }
            // Puntuación
            try {
                int puntuacion = Integer.parseInt(puntuacionNuevo.getText()
                        .toString());
                if (puntuacion > 100) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.campo_puntuacion_invalido),
                            Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                juego.setPuntuacion(puntuacion);
            } catch (NumberFormatException nfe) {
                juego.setPuntuacion(0);
            }
            try {
                if (caratulaTemporal != null) {
                    // Nombre generado
                    String nombre = utilidades.encriptar(juego.getTitulo()
                            + juego.getEan() + juego.getPlataforma()
                            + juego.getCompania());
                    if (utilidades.caratulaDefinitiva(caratulaTemporal, nombre)) {
                        juego.setCaratula(nombre + ".png");
                    }
                } else {
                    juego.setCaratula("");
                }
            } catch (Exception e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.error_cargar_caratula),
                        Toast.LENGTH_SHORT);


                toast.show();

                e.printStackTrace();
                juego.setCaratula("");
            }

            Log.v("JUEGO", juego.toString());
            // Insertamos en la base de datos
            long id;
            if ((id = juegosSQLH.insertarJuego(juego)) > 0) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.juego_guardado), Toast.LENGTH_SHORT);
                toast.show();

                final SharedPreferences settings = getSharedPreferences("UserInfo",
                        0);
                Intent intent;
                if (settings.contains("detalle_imagen") && settings.getBoolean("detalle_imagen", true)) {

                    intent = new Intent(this, DetalleJuegoImagenGrande.class);

                } else {
                    intent = new Intent(this, DetalleJuego.class);

                }
                //Intent intent = new Intent(this, DetalleJuego.class);
                intent.putExtra("ID_JUEGO", String.valueOf(id));
                intent.putExtra("NUEVO_JUEGO", true);
                if (getIntent().getBooleanExtra("GRID", false)) {
                    intent.putExtra("GRID", true);
                }


                startActivity(intent);
            } else {
                if (id == -5) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.juego_codigo_duplicado),
                            Toast.LENGTH_SHORT);
                    //toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
                    toast.show();
                    return;
                } else {
                    Toast toast = Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.juego_no_guardado) + " "
                                    + juego.getTitulo(), Toast.LENGTH_SHORT);
                    //toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
            }
        }
    }

    /**
     * Activa el campo de fecha si se marca el checkbox de completado
     *
     * @param view
     */
    public void activarFechaCompletado(View view) {
        EditText fechaCompletado = (EditText) this
                .findViewById(R.id.edit_fecha_completado_nuevo);
        if (((CheckBox) this.findViewById(R.id.check_completado_nuevo))
                .isChecked()) {
            fechaCompletado.setEnabled(true);
        } else {
            fechaCompletado.setEnabled(false);
            fechaCompletado.setText("");
        }
    }

    /**
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Imagen de la carátula desde el sistema de ficheros
            if (requestCode == IMAGEN_SELECCIONADA) {
                Uri selectedImageUri = data.getData();
                imageCaratula.setImageURI(caratulaTemporal = utilidades
                        .caratulaTemporal(selectedImageUri));
            }
        }
        /**
         * Procesar el escaneo de un código
         */
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                ((EditText) this.findViewById(R.id.edit_ean_nuevo))
                        .setText(contents);
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_guardar:
                crearJuego();
                return true;
            case android.R.id.home:
                this.onBackPressed();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Lanza la actividad que se encarga de escanear un código de barras.
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

        AlertDialog.Builder builder = new AlertDialog.Builder(NuevoJuego.this);
        builder.setMessage(R.string.alerta_atras_nuevo_texto).setTitle(
                R.string.alerta_atras_nuevo_titulo);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Escoger fichero
                        if (getIntent().getBooleanExtra("GRID", false)) {
                            Intent intent = new Intent(NuevoJuego.this, InicioMasonry.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(NuevoJuego.this,
                                    Inicio.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                });

        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        return;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
