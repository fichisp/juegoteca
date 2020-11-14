package com.juegoteca.actividades;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.juegoteca.basedatos.Juego;
import com.juegoteca.basedatos.JuegosSQLHelper;
import com.juegoteca.util.ListadoJuegosArrayAdapter;
import com.juegoteca.util.ListadoJuegosFechaArrayAdapter;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

import java.util.Date;

public class UnDiaComoHoy extends Activity {

    private ListView listadoJuegos;
    private Juego[] datosJuegos;
    private TextView textoFavoritos;
    private Utilidades utilidades;
    private int opcionSeleccionadaPlataformas;
    private ListadoJuegosFechaArrayAdapter adaptador;


    /**
     * Llamada cuando se inicializa la actividad.
     * Realiza la consulta de favoritos y los carga en el listView
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undiacomohoy);
        utilidades = new Utilidades(this);
        listadoJuegos = (ListView) findViewById(R.id.listado_favoritos);
        textoFavoritos = (TextView) findViewById(R.id.texto_favoritos);
        setupActionBar();
        cargarJuegos(0);
        listadoJuegos.setAdapter(adaptador);
    }


    /**
     * Realiza la consulta de los favoritos
     *
     * @param plataforma Plataforma para el filtro de la consulta (si es 0 se supone que es todos)
     */
    private void cargarJuegos(int plataforma) {
        JuegosSQLHelper juegoSQLH = new JuegosSQLHelper(this);
        String day          = (String) android.text.format.DateFormat.format("dd",   new Date());
        String monthNumber  = (String) android.text.format.DateFormat.format("MM",   new Date());
        Cursor c =  juegoSQLH.gamesLaunchedSameDay(day, monthNumber);
        if (c != null && c.moveToFirst()) {
            datosJuegos = new Juego[c.getCount()];
            int i = 0;
            do {
                datosJuegos[i] = new Juego();
                datosJuegos[i].setId(c.getInt(0));
                datosJuegos[i].setTitulo(c.getString(2));
                datosJuegos[i].setCompania(c.getString(3));
                datosJuegos[i].setCaratula(c.getString(13));
                datosJuegos[i].setFechaLanzamiento(utilidades.convertirMilisegundosAFecha(c.getString(8)));
                // Recuperar el nombre de la plataforma
                Cursor cursorPlataforma = juegoSQLH.buscarPlataformaID(String.valueOf(c.getInt(5)));
                cursorPlataforma.moveToFirst();
                datosJuegos[i].setNombrePlataforma(cursorPlataforma.getString(1));
                i++;
            }
            while (c.moveToNext());
            adaptador = new ListadoJuegosFechaArrayAdapter(this, datosJuegos);
            listadoJuegos.setAdapter(adaptador);
            listadoJuegos.setVisibility(View.VISIBLE);
            textoFavoritos.setText(datosJuegos.length + " " + getString(R.string.juegos));
            //textoFavoritos.setVisibility(View.GONE);
            c.close();
        } else {
            textoFavoritos.setText("0 " + getString(R.string.juegos));
            textoFavoritos.setVisibility(View.VISIBLE);
            listadoJuegos.setVisibility(View.GONE);
        }
        juegoSQLH.close();
    }

    /**
     * Lanza la actividad para ver la ficha de un jeugo
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

        //Intent intent = new Intent(this, DetalleJuego.class);
        intent.putExtra("ID_JUEGO", String.valueOf(id.getText()));
        if (getIntent().getBooleanExtra("GRID", false)) {
            intent.putExtra("GRID", true);
        }
        intent.putExtra("CALLER", "ListadoFavoritos");
        intent.putExtra("NUEVO_JUEGO", false);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.favoritos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_home:
                intent = new Intent(this, Inicio.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("GRID", false)) {
            Intent intent = new Intent(this, InicioMasonry.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, Inicio.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utilidades.unbindDrawables(findViewById(R.id.linear_favoritos_all));
        System.gc();
    }
}
