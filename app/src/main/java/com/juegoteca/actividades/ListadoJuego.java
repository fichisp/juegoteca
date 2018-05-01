package com.juegoteca.actividades;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.juegoteca.basedatos.Juego;
import com.juegoteca.basedatos.JuegosSQLHelper;
import com.juegoteca.util.AdaptadorJuegosLista;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("NewApi")
public class ListadoJuego extends Activity {

    private ListView listadoJuegos;
    private Juego[] datosJuegos;
    private String[] valoresBusqueda = null;
    private JSONObject listadoJuegosJSON;
    private String url_buscar;
    private ProgressDialog dialogoBusqueda;
    private TextView tituloBusqueda;
    private Utilidades utilidades;

    /**
     * Llamada cuando se inicializa la actividad.
     * Realiza la consulta con los parámetros proporcionados por la actividad Buscador
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_juego);
        setupActionBar();

        url_buscar = this.getResources().getString(R.string.url_buscar);
        Intent intent = getIntent();
        valoresBusqueda = intent.getStringArrayExtra("VALORES");
        new RealizaBusqueda(this).execute();
    }

    /**
     * Lanza la actividad para ver la ficha de un juego.
     * En el caso de ser una ficha de un juego online, llama a la clase asíncrona CargarJuegoOnline
     * para que realice la carga asíncrona de los datos.
     *
     * @param view
     */
    public void detalleJuego(View view) {
        TextView id = (TextView) view.findViewById(R.id.id_juego);
        final SharedPreferences settings = getSharedPreferences("UserInfo",
                0);



        Intent intent;
        if (settings.contains("detalle_imagen") && settings.getBoolean("detalle_imagen", true)) {

            intent = new Intent(this, DetalleJuegoImagenGrande.class);

        } else {
            intent = new Intent(this, DetalleJuego.class);

        }

        intent.putExtra("ID_JUEGO", String.valueOf(id.getText()));
        intent.putExtra("NUEVO_JUEGO", false);
        intent.putExtra("CALLER", "ListadoJuego");
        intent.putExtra("VALORES", valoresBusqueda);
        if (getIntent().getBooleanExtra("GRID", false)) {
            intent.putExtra("GRID", true);
        }

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.listado_juego, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_home:
                intent = new Intent(this, Inicio.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Buscador.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    /**
     * Clase auxiliar para realizar la búsqueda de forma asíncrona y no bloquear el hilo principal de la
     * aplicación
     *
     * @author alvaro
     */
    private class RealizaBusqueda extends AsyncTask<Void, Void, AdaptadorJuegosLista> {
        private Activity context;
        private int elementosEncontrados = 0;

        public RealizaBusqueda(Activity context) {
            this.context = context;
        }

        @Override
        protected AdaptadorJuegosLista doInBackground(Void... values) {
            // Referenciamos los controles
            listadoJuegos = (ListView) findViewById(R.id.listado_juegos);
            tituloBusqueda = (TextView) findViewById(R.id.nombre_busqueda);
            JuegosSQLHelper juegoSQLH = new JuegosSQLHelper(context);
            elementosEncontrados = 0;
            listadoJuegosJSON = null;


            // Búsqueda en la base de datos local

            Cursor c = juegoSQLH.getJuegos(valoresBusqueda);
            if (c != null && c.moveToFirst()) {
                datosJuegos = new Juego[c.getCount()];
                int i = 0;
                do {
                    datosJuegos[i] = new Juego();
                    datosJuegos[i].setId(c.getInt(0));
                    datosJuegos[i].setTitulo(c.getString(2));
                    datosJuegos[i].setCompania(c.getString(3));
                    datosJuegos[i].setCaratula(c.getString(13));
                    // Recuperar el nombre de la plataforma
                    Cursor cursorPlataforma = juegoSQLH.buscarPlataformaID(String.valueOf(c.getInt(5)));
                    cursorPlataforma.moveToFirst();
                    datosJuegos[i].setNombrePlataforma(cursorPlataforma.getString(1));
                    i++;
                }
                while (c.moveToNext());
                elementosEncontrados = c.getCount();
                c.close();
                AdaptadorJuegosLista adaptador = new AdaptadorJuegosLista(context, datosJuegos, false);
                return adaptador;
            } else {
                return null;
            }

        }


        @Override
        protected void onPreExecute() {
            dialogoBusqueda = ProgressDialog.show(context, "", getString(R.string.buscando), true);
        }

        @Override
        protected void onPostExecute(AdaptadorJuegosLista result) {
            dialogoBusqueda.dismiss();
            if (result != null && elementosEncontrados != 0) {
                listadoJuegos.setAdapter(result);
                tituloBusqueda.setText(elementosEncontrados + " " + getString(R.string.juegos_encontrados));
            } else {
                // No se han encontrado juegos
                tituloBusqueda.setText(R.string.sin_resultados_busqueda);
            }
        }

    }
}