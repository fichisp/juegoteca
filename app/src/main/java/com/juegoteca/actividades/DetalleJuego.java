package com.juegoteca.actividades;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juegoteca.basedatos.Juego;
import com.juegoteca.basedatos.JuegosSQLHelper;
import com.juegoteca.util.JSONParser;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetalleJuego extends Activity {

    private static final String TAG_SUCCESS = "success";
    private JuegosSQLHelper juegosSQLH;
    private String idJuego;
    private boolean esJuegoOnline = false;
    private Juego juego = null;
    private Utilidades utilidades;
    private JSONObject juegoJSON;
    private ImageView imageViewCaratula, imageViewPlataforma,
            imageViewClasficacion, imageViewIdioma;
    private TextView textViewTitulo, textViewCompania, textViewGenero,
            textViewResumen, textViewFechaLanzamiento, textViewFechaCompra,
            textViewFechaCompletado, textViewPrecio, textViewEan,
            textViewComentario, textViewPuntuacion, textViewFormato;
    private String url_insertar, url_buscar, url_subir_imagen;
    private JSONParser jParser;
    private ProgressDialog dialogoExportar;
    private DescargarImagen cargarImagenAsicncrona;
    private String[] valoresBusqueda = null;

    /**
     * Llamada cuando se inicializa la actividad. Se inicializan los componentes
     * y se carga la información del juego que visualizará
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_juego);
        loadData();
    }

    protected void loadData() {
        Intent intent = getIntent();
        utilidades = new Utilidades(this);
        url_insertar = this.getResources().getString(R.string.url_insertar);
        url_buscar = this.getResources().getString(R.string.url_buscar);
        url_subir_imagen = this.getResources().getString(
                R.string.url_subir_imagen);
        jParser = new JSONParser();
        // Recuperamos los valores que nos oueden pasar en el intent
        idJuego = intent.getStringExtra("ID_JUEGO");
        valoresBusqueda = intent.getStringArrayExtra("VALORES");
        esJuegoOnline = intent.getExtras().getBoolean("ONLINE");
        juegosSQLH = new JuegosSQLHelper(this);
        // Inicializar los componentes
        imageViewCaratula = ((ImageView) this
                .findViewById(R.id.image_view_caratula_detalle));
        imageViewPlataforma = (ImageView) this
                .findViewById(R.id.image_plataforma_detalle);
        imageViewClasficacion = (ImageView) this
                .findViewById(R.id.image_clasificacion_detalle);
        imageViewIdioma = (ImageView) this
                .findViewById(R.id.image_idioma_detalle);
        textViewTitulo = ((TextView) this
                .findViewById(R.id.text_titulo_detalle));
        textViewCompania = ((TextView) this
                .findViewById(R.id.text_compania_detalle));
        textViewGenero = ((TextView) this
                .findViewById(R.id.text_genero_detalle));
        textViewResumen = ((TextView) this
                .findViewById(R.id.text_resumen_detalle));
        textViewFechaLanzamiento = ((TextView) this
                .findViewById(R.id.text_fecha_lanzamiento_detalle));
        textViewFechaCompra = ((TextView) this
                .findViewById(R.id.text_fecha_compra_detalle_editar));
        textViewPrecio = ((TextView) this
                .findViewById(R.id.text_precio_detalle));
        textViewFechaCompletado = ((TextView) this
                .findViewById(R.id.text_fecha_completado_detalle));
        textViewEan = ((TextView) this.findViewById(R.id.text_ean_detalle));
        // checkCompletado = ((CheckBox) this
        // .findViewById(R.id.check_completado_detalle));
        textViewComentario = ((TextView) this
                .findViewById(R.id.text_comentario_detalle));
        textViewPuntuacion = ((TextView) this
                .findViewById(R.id.text_puntuacion_detalle));
        textViewFormato = ((TextView) this.findViewById(R.id.formato_detalle));

        // Si es una ficha online se lanza en un hilo nuevo para no bloquear el
        // principal
        if (esJuegoOnline) {
            try {
                Handler h = new Handler();
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add((new BasicNameValuePair("id", idJuego)));
                            juegoJSON = jParser.makeHttpRequest(url_buscar,
                                    "GET", params);
                            JSONArray jA = juegoJSON.getJSONArray("juego");
                            juego = new Juego();
                            juego.convertirJSON(((JSONObject) jA.get(0)));
                            cargarFichaJuego(juego);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast toast = Toast
                                    .makeText(
                                            getApplicationContext(),
                                            getString(R.string.error_cargar_ficha_online),
                                            Toast.LENGTH_SHORT);
                            toast.show();
                            onBackPressed();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Si es una ficha local
        else {
            Cursor c = juegosSQLH.buscarJuegoID(idJuego);
            if (c != null && c.moveToFirst()) {

                juego = new Juego(c.getInt(0), c.getString(1), c.getString(2),
                        c.getString(3), c.getInt(4), c.getInt(5), c.getInt(6),
                        c.getInt(7), c.getString(8), c.getString(9),
                        c.getFloat(10), c.getInt(11), c.getString(12),
                        c.getString(13), c.getString(14), c.getString(15),
                        c.getString(16), c.getInt(17), c.getString(18));

                c.close();
                cargarFichaJuego(juego);
            }
        }
    }

    /**
     * Carga en los componentes de la actividad la información del juego
     * recuperaro.
     *
     * @param juego Juego que almacena los datos
     */
    private void cargarFichaJuego(Juego juego) {
        // No hay carátula
        if ((juego.getCaratula()).length() == 0) {
            imageViewCaratula.setImageDrawable((this.getResources()
                    .getDrawable(R.drawable.sinimagen)));
        } else {
            if (esJuegoOnline) {
                cargarImagenAsicncrona = new DescargarImagen(imageViewCaratula);
                cargarImagenAsicncrona.execute(juego.getCaratula());
            } else {
                imageViewCaratula.setImageBitmap(utilidades
                        .decodeFile(new File(this.getFilesDir().getPath() + "/"
                                + juego.getCaratula())));
            }
        }
        // Título de la actividad igual al nombre del juego
        textViewTitulo.setText(juego.getTitulo());
        textViewCompania.setText(juego.getCompania());
        cargarGenero(String.valueOf(juego.getGenero()));
        // Cargar la imagen de la plataforma
        cargarImagenPlataforma(String.valueOf(juego.getPlataforma()));
        cargarImagenClasificacion(String.valueOf(juego.getClasificacion()));
        cargarImagenIdioma(String.valueOf(juego.getIdioma()));
        if (juego.getResumen() != null && juego.getResumen().length() > 0) {

            textViewResumen.setText(juego.getResumen());
        } else {
            textViewResumen.setText("-");
        }

        if (Double.parseDouble(juego.getFechaLanzamiento()) != 0) {
            textViewFechaLanzamiento.setText(utilidades
                    .convertirMilisegundosAFecha(juego.getFechaLanzamiento()));
        } else {
            textViewFechaLanzamiento.setText("-");
        }

        if (Double.parseDouble(juego.getFechaCompra()) != 0) {
            textViewFechaCompra.setText(utilidades
                    .convertirMilisegundosAFecha(juego.getFechaCompra()));
        } else {
            textViewFechaCompra.setText("-");
        }
        if (juego.getEan().length() != 0 && !juego.getEan().contains("mjtc")) {
            textViewEan.setText(juego.getEan());
        } else {
            textViewEan.setText("-");
        }
        if (!esJuegoOnline) {

            if (juego.getPrecio() != 0) {

                final SharedPreferences settings = getSharedPreferences("UserInfo", 0);

                textViewPrecio
                        .setText(String.format("%.2f", juego.getPrecio())+" " + settings.getString("currency",null));
            } else {
                textViewPrecio.setText("-");
            }
            if (juego.getCompletado() == 1) {
                if (Double.parseDouble(juego.getFechaCompletado()) != 0) {
                    textViewFechaCompletado.setText(getString(R.string.si)
                            + " ("
                            + utilidades.convertirMilisegundosAFecha(juego
                            .getFechaCompletado()) + ")");
                } else {
                    textViewFechaCompletado.setText(getString(R.string.si));
                }
            } else {
                textViewFechaCompletado.setText("-");
            }
            if (juego.getComentario().length() != 0
                    && juego.getComentario().compareToIgnoreCase("null") != 0) {
                textViewComentario.setText(juego.getComentario());
            } else {
                textViewComentario.setText("-");
            }
            if (juego.getPuntuacion() != 0) {
                textViewPuntuacion
                        .setText(String.valueOf(juego.getPuntuacion()) + "/100");
            } else {
                textViewPuntuacion.setText("-");
            }

            if ("".equals(juego.getFormato())
                    || juego.getFormato().compareTo("F") == 0) {
                textViewFormato.setText(getString(R.string.fisico));
            } else {
                textViewFormato.setText(getString(R.string.digital));
            }

        } else {
            // Estos campos no se cargan cuando es una ficha de un juego
            // online
            ((LinearLayout) findViewById(R.id.linear_detalle_fecha_compra))
                    .setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.linear_detalle_precio))
                    .setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.linear_detalle_completado))
                    .setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.linear_detalle_comentario))
                    .setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.linear_detalle_puntuacion))
                    .setVisibility(View.GONE);
        }
    }

    /**
     * Carga nombre genero correspondiente al valor del id
     *
     * @param idGenero Identificador del género en la base de datos
     */
    private void cargarGenero(String idGenero) {
        Cursor cursorGenero = juegosSQLH.buscarGeneroID(idGenero);
        if (cursorGenero != null && cursorGenero.moveToFirst()) {
            textViewGenero.setText(cursorGenero.getString(1));
            cursorGenero.close();
        }
    }

    /**
     * Carga la imagen correspondiente al al valor del id
     *
     * @param idIdioma Identificador del idioma en ela base de datos
     */
    private void cargarImagenIdioma(String idIdioma) {
        Cursor cursorIdioma = juegosSQLH.buscarIdiomaID(idIdioma);
        if (cursorIdioma != null && cursorIdioma.moveToFirst()) {
            int drawableID = this.getResources().getIdentifier(
                    cursorIdioma.getString(2), "drawable", getPackageName());
            imageViewIdioma.setImageDrawable(this.getResources().getDrawable(
                    drawableID));
            cursorIdioma.close();
        }
    }

    /**
     * Carga la imagen de la clasificación correspondiente al valor del id
     *
     * @param idClasificacion Identificador de la clasificación en la base de datos
     */
    private void cargarImagenClasificacion(String idClasificacion) {
        Cursor cursorClasificacion = juegosSQLH
                .buscarClasificacionID(idClasificacion);
        if (cursorClasificacion != null && cursorClasificacion.moveToFirst()) {
            int drawableID = this.getResources().getIdentifier(
                    cursorClasificacion.getString(2), "drawable",
                    getPackageName());
            imageViewClasficacion.setImageDrawable(this.getResources()
                    .getDrawable(drawableID));
            cursorClasificacion.close();
        }
    }

    /**
     * Carga la imagen de la plataforma correspondiente
     *
     * @param idPlataforma Identificador de la plataforma en la base de datos
     */
    private void cargarImagenPlataforma(String idPlataforma) {
        Cursor cursorPlataforma = juegosSQLH.buscarPlataformaID(idPlataforma);
        if (cursorPlataforma != null && cursorPlataforma.moveToFirst()) {
            int drawableID = this.getResources().getIdentifier(
                    cursorPlataforma.getString(5), "drawable", getPackageName());
            imageViewPlataforma.setImageDrawable(this.getResources()
                    .getDrawable(drawableID));
            cursorPlataforma.close();
        }
    }

    /**
     * Importar la ficha online de un juego a la base de datos local. Llama a la
     * tarea asíncrona ImportarJuego para realizar la acción
     */
    private void importarJuego() {
        new ImportarJuego().execute();
    }

    /**
     * Sube la ficha local a la BBDD de la aplicación para su contribución.
     * Llama a la tarea asíncrona ExportarJuego para realizar la acción.
     */
    private void exportarJuego() {
        final SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        if (!settings.contains("usuario")) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Tienes que iniciar sesion para poder exportar un juego",
                    Toast.LENGTH_SHORT);
            t.show();
        } else {
            new ExportarJuego().execute();
        }

    }

    /**
     * Añade el juego actual a la lista de favoritos
     */
    private void anadirFavorito() {
        long id;
        if ((id = juegosSQLH.insertarFavorito(juego.getId())) > 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.juego_anadido_favoritos),
                    Toast.LENGTH_SHORT);
            toast.show();
            // Recarga de la actividad
            startActivity(getIntent().setFlags(
                    Intent.FLAG_ACTIVITY_NO_ANIMATION));
            finish();
        } else {
            if (id == -5) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.juego_ya_anadido_favoritos),
                        Toast.LENGTH_SHORT);
                // toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,0,0);
                toast.show();
                return;
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.juego_no_anadido_favoritos),
                        Toast.LENGTH_SHORT);
                // toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,0,0);
                toast.show();
            }
        }
    }

    /**
     * Elimina el juego actual de la lista de favoritos
     */
    private void eliminarFavorito() {
        int id;
        if ((id = juegosSQLH.eliminarFavorito(idJuego)) > 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.juego_eliminado_favoritos),
                    Toast.LENGTH_SHORT);
            toast.show();
            // Recarga de la actividad
            startActivity(getIntent().setFlags(
                    Intent.FLAG_ACTIVITY_NO_ANIMATION));
            finish();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.juego_no_eliminiado_favoritos),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Muestra la información de la plataforma asociada a este juego (al pulsar
     * el icono de la misma)
     *
     * @param view
     */
    public void detallePlataforma(View view) {
        String codigoIdioma = Locale.getDefault().getISO3Language();
        // Sólo se cargan en español
        if ("spa".equalsIgnoreCase(codigoIdioma)) {
            Intent intent = new Intent(this, Plataforma.class);
            intent.putExtra("ID_PLATAFORMA",
                    String.valueOf(juego.getPlataforma()));
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        String caller = getIntent().getStringExtra("CALLER");
        if (caller != null && caller.compareTo("ListadoJuego") == 0) {
            final Intent intent = new Intent(this, ListadoJuego.class);
            intent.putExtra("VALORES", valoresBusqueda);
            intent.putExtra("ONLINE", esJuegoOnline);
            startActivity(intent);
        } else {
            if (caller != null && caller.compareTo("ListadoPendientes") == 0) {
                final Intent intent = new Intent(this, Pendientes.class);
                if (getIntent().getBooleanExtra("GRID", false)) {
                    intent.putExtra("GRID", true);
                }
                startActivity(intent);
            } else {
                if (caller != null && caller.compareTo("ListadoFavoritos") == 0) {
                    final Intent intent = new Intent(this, Favoritos.class);
                    if (getIntent().getBooleanExtra("GRID", false)) {
                        intent.putExtra("GRID", true);
                    }
                    startActivity(intent);
                } else if (getIntent().getBooleanExtra("GRID", false)) {
                    Intent intent = new Intent(this, InicioMasonry.class);
                    if(getIntent().getIntExtra("SCROLL_Y",0)>0){
                        intent.putExtra("SCROLL_Y", getIntent().getIntExtra("SCROLL_Y",0));
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(this, Inicio.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detalle_juego, menu);

        MenuItem itemEditar = menu.findItem(R.id.action_editar);
        MenuItem itemEliminar = menu.findItem(R.id.action_eliminar);
        MenuItem itemImportar = menu.findItem(R.id.action_importar);
        // MenuItem itemCompartir = menu.findItem(R.id.action_exportar);
        MenuItem itemFavorito = menu.findItem(R.id.action_favorito);
        MenuItem itemFavoritoEliminar = menu
                .findItem(R.id.action_favorito_eliminar);
        // Ocultar las opciones que no deberán mostrar en las fichas de un juego
        // online
        if (getIntent().getExtras().getBoolean("ONLINE")) {
            itemEditar.setVisible(false);
            itemEliminar.setVisible(false);
            // itemCompartir.setVisible(false);
            itemFavorito.setVisible(false);
            itemFavoritoEliminar.setVisible(false);
        } else {
            itemImportar.setVisible(false);
        }
        // Si ya está en favoritos
        if (juegosSQLH.esFavorito(this.idJuego)) {
            itemFavorito.setVisible(false);
        } else {
            itemFavoritoEliminar.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_home:
                intent = new Intent(this, Inicio.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_settings:
                intent = new Intent(this, Opciones.class);
                startActivity(intent);
                return true;
            case R.id.action_favorito:
                anadirFavorito();
                return true;
            case R.id.action_favorito_eliminar:
                eliminarFavorito();
                return true;
            case R.id.action_importar:
                importarJuego();
                return true;
            // Version 1.2
            // case R.id.action_exportar:
            // exportarJuego();
            // return true;
            case R.id.action_editar:
                intent = new Intent(this, EditarJuego.class);
                intent.putExtra("ID_JUEGO", idJuego);
                String caller = getIntent().getStringExtra("CALLER");
                if (getIntent().getBooleanExtra("GRID", false)) {
                    intent.putExtra("GRID", true);
                }
                if (caller != null) {
                    intent.putExtra("CALLER", caller);
                    if (caller != null && caller.compareTo("ListadoJuego") == 0) {
                        intent.putExtra("VALORES", valoresBusqueda);
                        intent.putExtra("ONLINE", esJuegoOnline);
                    }
                }
                startActivity(intent);
                finish();
                return true;
            case R.id.action_eliminar:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.alerta_borrado_texto).setTitle(
                        R.string.alerta_borrado_titulo);
                builder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                Toast toast;
                                if (juegosSQLH.borrarJuego(idJuego) > 0) {
                                    toast = Toast.makeText(getApplicationContext(),
                                            getString(R.string.juego_eliminado),
                                            Toast.LENGTH_SHORT);
                                    try {
                                        File imagen = new File(
                                                getApplicationContext()
                                                        .getFilesDir()
                                                        .getAbsolutePath()
                                                        + "/" + juego.getCaratula());
                                        imagen.delete();
                                    } catch (Exception e) {
                                    }
                                } else {
                                    toast = Toast.makeText(getApplicationContext(),
                                            getString(R.string.juego_no_eliminado),
                                            Toast.LENGTH_SHORT);
                                }
                                // toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,0,0);
                                toast.show();
                                if (getIntent().getBooleanExtra("GRID", false)) {
                                    Intent intent = new Intent(getApplicationContext(),
                                            InicioMasonry.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getApplicationContext(),
                                            Inicio.class);
                                    startActivity(intent);
                                }
                                return;
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unbind all drawables starting from the first viewgroup
        utilidades.unbindDrawables(findViewById(R.id.linear_detalle_all));
        if (cargarImagenAsicncrona != null) {
            cargarImagenAsicncrona.cancel(true);
        }

        // indicate to the vm that it would be a good time to run the gc
        System.gc();
    }

    /**
     * Clase asíncrona para la exportación de una ficha a la base de datos del
     * servidor.
     *
     * @author alvaro
     */
    private class ExportarJuego extends AsyncTask<Void, Void, Boolean> {

        private String mensajeError = "";

        /**
         * Muestra un diálogo de progreso que se mostrará mientras la tarea se
         * ejecuta.
         */
        @Override
        protected void onPreExecute() {
            dialogoExportar = ProgressDialog.show(DetalleJuego.this, null,
                    "Compartiendo juego...", true);

        }

        /**
         * Realiza la conexión con el servidor. Envía los datos en JSON y
         * tranfiere la imagen.
         */
        @Override
        protected Boolean doInBackground(Void... parameters) {
            boolean juegoExportado = false;
            final SharedPreferences settings = getSharedPreferences("UserInfo",
                    0);
            try {
                // Establecemos los parámetros que vamos a necesitar
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("ean", String.valueOf(juego
                        .getEan())));
                params.add(new BasicNameValuePair("titulo", juego.getTitulo()));
                params.add(new BasicNameValuePair("compania", juego
                        .getCompania()));
                params.add(new BasicNameValuePair("genero", String
                        .valueOf(juego.getGenero())));
                params.add(new BasicNameValuePair("plataforma", String
                        .valueOf(juego.getPlataforma())));
                params.add(new BasicNameValuePair("clasificacion", String
                        .valueOf(juego.getClasificacion())));
                params.add(new BasicNameValuePair("idioma", String
                        .valueOf(juego.getIdioma())));
                params.add(new BasicNameValuePair("fecha_lanzamiento", juego
                        .getFechaLanzamiento()));
                params.add(new BasicNameValuePair("fecha_compra", ""));
                params.add(new BasicNameValuePair("precio", ""));
                params.add(new BasicNameValuePair("completado", ""));
                params.add(new BasicNameValuePair("resumen", juego.getResumen()));
                params.add(new BasicNameValuePair("fecha_creacion", ""));
                params.add(new BasicNameValuePair("fecha_completado", ""));
                try {

                    if (utilidades.subirFichero(
                            getApplicationContext().getFilesDir().getPath()
                                    + "/" + juego.getCaratula(),
                            url_subir_imagen) == 200) {
                        params.add(new BasicNameValuePair("caratula", juego
                                .getCaratula()));
                    } else {
                        params.add(new BasicNameValuePair("caratula", ""));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    params.add(new BasicNameValuePair("caratula", ""));
                }
                params.add(new BasicNameValuePair("usuario", settings
                        .getString("usuario", "")));

                // Hacemos la llamada al PHP enviando los parámetros en el POST
                JSONObject json = jParser.makeHttpRequest(url_insertar, "POST",
                        params);

                // Compromabamos el resultado de la operación con el valor
                // TAG_SUCESS que devolverá la petición
                try {
                    int success = Integer.valueOf(json.get("success")
                            .toString());

                    // La inserción se completó con éxito
                    switch (success) {
                        case 1:
                            juegoExportado = true;
                            break;
                        case 3:
                            mensajeError = "No puede exportar el juego porque su cuenta ha sido baneada";
                            break;
                        default:
                            mensajeError = "No se ha podido exportar el juego";
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
            }
            return juegoExportado;
        }

        /**
         * Muestra el resultado de la operación a través de un Toast.
         */
        @Override
        protected void onPostExecute(Boolean result) {
            dialogoExportar.dismiss();
            Toast resultadoExportacion;
            if (result) {
                resultadoExportacion = Toast
                        .makeText(
                                getApplicationContext(),
                                "Gracias por colaborar. Tu juego será incluido en la base de datos una vez validado.",
                                Toast.LENGTH_SHORT);
                resultadoExportacion.show();
            } else {
                resultadoExportacion = Toast.makeText(getApplicationContext(),
                        mensajeError, Toast.LENGTH_SHORT);
                resultadoExportacion.show();
            }
            return;
        }
    }

    /**
     * Clase asíncrona para la importación de una ficha desde la base de datos
     * del servidor.
     *
     * @author alvaro
     */
    private class ImportarJuego extends AsyncTask<Void, Void, Boolean> {
        long id;

        /**
         * Muestra un diálogo de progreso que se mostrará mientras la tarea se
         * ejecuta.
         */
        @Override
        protected void onPreExecute() {
            // TODO: String
            dialogoExportar = ProgressDialog.show(DetalleJuego.this, null,
                    "Importando juego...", true);

        }

        /**
         * Inserta el juego que se está visualizando en la base de datos del
         * dispositovo y descarga la imagen asociada.
         */
        @Override
        protected Boolean doInBackground(Void... parameters) {
            // Insertamos en la base de datos
            if ((id = juegosSQLH.insertarJuego(juego)) > 0) {
                juego.setId(id);
                // Descargar la imagen de la caratula
                if (juego.getCaratula().length() > 0) {
                    try {
                        String nombreFichero = utilidades.encriptar(juego
                                .getTitulo() + juego.getEan());
                        String extensionFichero = juego.getCaratula()
                                .substring(
                                        juego.getCaratula().lastIndexOf('.'),
                                        juego.getCaratula().length());
                        File destino = new File(getApplicationContext()
                                .getFilesDir().getPath()
                                + "/"
                                + nombreFichero
                                + extensionFichero);
                        Bitmap bmOriginal = utilidades.descargarImagen(juego
                                .getCaratula());
                        Bitmap bmEscalado = utilidades.redimensionarImagen(
                                bmOriginal, 600);
                        bmEscalado.compress(CompressFormat.JPEG, 90,
                                new FileOutputStream(destino));
                        juego.setCaratula(nombreFichero + extensionFichero);
                    } catch (IOException ioe) {
                        Toast toast = Toast
                                .makeText(
                                        getApplicationContext(),
                                        "No se puede copiar la carátula al directorio de la apliación",
                                        Toast.LENGTH_SHORT);
                        toast.show();
                        juego.setCaratula("");
                    }
                }
                juegosSQLH.actualizarJuego(juego);
                return true;
            } else {
                if (id == -5) {
                    return false;
                } else {
                    return null;
                }
            }
        }

        /**
         * Muestra el resultado de la operación a través de un Toast.
         */
        @Override
        protected void onPostExecute(Boolean result) {
            dialogoExportar.dismiss();
            Toast resultadoExportacion;
            if (result == null) {
                resultadoExportacion = Toast.makeText(getApplicationContext(),
                        "Error al importar el juego", Toast.LENGTH_SHORT);
                resultadoExportacion.show();
            } else {
                if (result) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Juego guardado", Toast.LENGTH_SHORT);
                    toast.show();
                    Intent intent = new Intent(DetalleJuego.this,
                            EditarJuego.class);
                    intent.putExtra("ID_JUEGO", String.valueOf(id));
                    startActivity(intent);
                } else {
                    resultadoExportacion = Toast.makeText(
                            getApplicationContext(),
                            "Ya tienes este juego en tu colección.",
                            Toast.LENGTH_SHORT);
                    resultadoExportacion.show();
                }
            }
            return;
        }
    }

    /**
     * Clase asíncrona para la descarga de imágenes en segudo plano. Usada al
     * visualizar una ficha de la base de datos online sin tener que esperar a
     * que se descargue la imagen.
     *
     * @author alvaro
     */
    class DescargarImagen extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference imageViewReference;

        public DescargarImagen(ImageView imageView) {
            imageViewReference = new WeakReference(imageView);
        }

        /**
         * Descarga la imagen asociada al juego.
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            return utilidades.descargarImagen(params[0]);
        }

        /**
         * Asocia la imagen descargada con el imageView correspondiente
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (imageViewReference != null) {
                ImageView imageView = (ImageView) imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setImageDrawable(imageView.getContext()
                                .getResources()
                                .getDrawable(R.drawable.sinimagen));
                    }
                }

            }
        }
    }
}
