package com.juegoteca.actividades;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juegoteca.basedatos.Juego;
import com.juegoteca.basedatos.JuegosSQLHelper;
import com.juegoteca.util.ExpandableTextView;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Locale;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class DetalleJuego extends Activity {

    private JuegosSQLHelper juegosSQLH;
    private String idJuego;
    private boolean esJuegoOnline = false;
    private Juego juego = null;
    private Utilidades utilidades;
    private ImageView imageViewCaratula, imageViewPlataforma,
            imageViewClasficacion, imageViewIdioma;
    private TextView textViewTitulo, textViewCompania, textViewGenero,
            textViewResumen, textViewFechaLanzamiento, textViewFechaCompra,
            textViewFechaCompletado, textViewPrecio, textViewEan,
            textViewComentario, textViewPuntuacion, textViewFormato, textViewPlataforma,textViewClasificacion, textViewIdioma;
    private String[] valoresBusqueda = null;

    int widthL  = 0;
    int heightL = 0;

    /**
     * Llamada cuando se inicializa la actividad. Se inicializan los componentes
     * y se carga la información del juego que visualizará
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_juego);
        getActionBar().setDisplayHomeAsUpEnabled(true); // In `OnCreate();`
        loadData();


        LinearLayout layout = (LinearLayout)findViewById(R.id.datos1_detalle);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                widthL= layout.getMeasuredWidth();
                heightL = layout.getMeasuredHeight();

                if(heightL !=0){
                    int newTrimSize = heightL/3;
                    ((ExpandableTextView)textViewResumen).setTrimLength(newTrimSize < 300 ? newTrimSize:100);
                    ((ExpandableTextView)textViewResumen).setTrim(true);
                    ((ExpandableTextView)textViewResumen).setText();

                }

            }
        });

    }

    void loadData() {
        Intent intent = getIntent();
        utilidades = new Utilidades(this);
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
/*        imageViewClasficacion = (ImageView) this
                .findViewById(R.id.image_clasificacion_detalle);
        imageViewIdioma = (ImageView) this
                .findViewById(R.id.image_idioma_detalle);*/
        textViewTitulo = ((TextView) this
                .findViewById(R.id.text_titulo_detalle));
        textViewCompania = ((TextView) this
                .findViewById(R.id.text_compania_detalle));
        textViewGenero = ((TextView) this
                .findViewById(R.id.text_genero_detalle));

        textViewPlataforma = ((TextView) this
                .findViewById(R.id.text_plataforma_detalle));


        textViewClasificacion = ((TextView) this
                .findViewById(R.id.text_clasificacion_detalle));

        textViewIdioma = ((TextView) this
                .findViewById(R.id.text_idioma_detalle));

        textViewFormato = ((TextView) this
                .findViewById(R.id.text_formato_detalle));

        textViewResumen = ((TextView) this
                .findViewById(R.id.text_resumen_detalle));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textViewResumen.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }





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
        //textViewFormato = ((TextView) this.findViewById(R.id.formato_detalle));


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
            try {
                File f = new File(this.getFilesDir().getPath() + "/"
                        + juego.getCaratula());

                if(f.exists()){
                    imageViewCaratula.setImageBitmap(utilidades
                            .decodeFile(f));
                } else {
                    imageViewCaratula.setImageDrawable((this.getResources()
                            .getDrawable(R.drawable.sinimagen)));
                }

            } catch (Exception e)
            {
                imageViewCaratula.setImageDrawable((this.getResources()
                        .getDrawable(R.drawable.sinimagen)));
            }
        }
        // Título de la actividad igual al nombre del juego
        textViewTitulo.setText(juego.getTitulo());
        textViewCompania.setText(juego.getCompania());
        cargarGenero(String.valueOf(juego.getGenero()));

        Cursor c = juegosSQLH.buscarPlataformaID(String.valueOf(juego.getPlataforma()));
        c.moveToFirst();
        textViewPlataforma.setText(c.getString(1));
        textViewPlataforma.setSingleLine(false);
        c.close();

        c = juegosSQLH.buscarClasificacionID(String.valueOf(juego.getClasificacion()));
        c.moveToFirst();
        textViewClasificacion.setText(c.getString(1));
        c.close();

        c = juegosSQLH.buscarIdiomaID(String.valueOf(juego.getIdioma()));
        c.moveToFirst();
        textViewIdioma.setText(c.getString(1));
        c.close();

        // Cargar la imagen de la plataforma
        cargarImagenPlataforma(String.valueOf(juego.getPlataforma()));
       /* cargarImagenClasificacion(String.valueOf(juego.getClasificacion()));
        cargarImagenIdioma(String.valueOf(juego.getIdioma()));*/

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

                final SharedPreferences settings = getSharedPreferences("JuegotecaPrefs", 0);

                String currency = settings.getString("currency",null);

                String currencyText = (currency!=null && !"".equals(currency))?currency:"";

                textViewPrecio
                        .setText(String.format("%.2f", juego.getPrecio())+" " +currencyText);
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
            findViewById(R.id.linear_detalle_fecha_compra)
                    .setVisibility(View.GONE);
            findViewById(R.id.linear_detalle_precio)
                    .setVisibility(View.GONE);
            findViewById(R.id.linear_detalle_completado)
                    .setVisibility(View.GONE);
            findViewById(R.id.linear_detalle_comentario)
                    .setVisibility(View.GONE);
            findViewById(R.id.linear_detalle_puntuacion)
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
        if (juegosSQLH.eliminarFavorito(idJuego) > 0) {
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
//        String caller = getIntent().getStringExtra("CALLER");
//        if (caller != null && caller.compareTo("ListadoJuego") == 0) {
//            final Intent intent = new Intent(this, ListadoJuego.class);
//            intent.putExtra("VALORES", valoresBusqueda);
//            intent.putExtra("ONLINE", esJuegoOnline);
//            startActivity(intent);
//        } else {
//            if (caller != null && caller.compareTo("ListadoPendientes") == 0) {
//                final Intent intent = new Intent(this, Pendientes.class);
//                if (getIntent().getBooleanExtra("GRID", false)) {
//                    intent.putExtra("GRID", true);
//                }
//                startActivity(intent);
//            } else {
//                if (caller != null && caller.compareTo("ListadoFavoritos") == 0) {
//                    final Intent intent = new Intent(this, Favoritos.class);
//                    if (getIntent().getBooleanExtra("GRID", false)) {
//                        intent.putExtra("GRID", true);
//                    }
//                    startActivity(intent);
//                } else if (getIntent().getBooleanExtra("GRID", false)) {
//                    Intent intent = new Intent(this, InicioMasonry.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Intent intent = new Intent(this, Inicio.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        }

        //Si es un juego nuevo, volvemos al inicio siempre
/*        if(getIntent().getBooleanExtra("NUEVO_JUEGO", false)) {
            if (getIntent().getBooleanExtra("GRID", false)) {
                Intent intent = new Intent(this, InicioMasonry.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, Inicio.class);
                startActivity(intent);
                finish();
            }
        } else {
            finish();
        }*/

        finish();

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
            case R.id.action_editar:
                intent = new Intent(this, EditarJuego.class);
                intent.putExtra("ID_JUEGO", idJuego);
                String caller = getIntent().getStringExtra("CALLER");
                if (getIntent().getBooleanExtra("GRID", false)) {
                    intent.putExtra("GRID", true);
                }
                if (caller != null) {
                    intent.putExtra("CALLER", caller);
                    if (caller.compareTo("ListadoJuego") == 0) {
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
                            @SuppressWarnings("ResultOfMethodCallIgnored")
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
                                        Log.e("Error", "Error al borrar la cartula" + e.getMessage());
                                    }
                                } else {
                                    toast = Toast.makeText(getApplicationContext(),
                                            getString(R.string.juego_no_eliminado),
                                            Toast.LENGTH_SHORT);
                                }
                                // toast.setGravity(Gravity.CENTER|Gravity.BOTTOM,0,0);
                                toast.show();
                                finish();
                                /*if (getIntent().getBooleanExtra("GRID", false)) {
                                    Intent intent = new Intent(getApplicationContext(),
                                            InicioMasonry.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getApplicationContext(),
                                            Inicio.class);
                                    startActivity(intent);
                                }*/
                            }
                        });
                builder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            utilidades.unbindDrawables(findViewById(R.id.linear_detalle_all));
        } catch (Exception e){
            Log.e("DETALLE_JUEGO",e.getMessage());
        }
        System.gc();
    }

}
