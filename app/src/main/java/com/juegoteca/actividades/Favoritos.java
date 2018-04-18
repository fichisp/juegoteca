package com.juegoteca.actividades;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.juegoteca.basedatos.Juego;
import com.juegoteca.basedatos.JuegosSQLHelper;
import com.juegoteca.util.AdaptadorJuegosLista;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

public class Favoritos extends Activity {

    private ListView listadoJuegos;
    private Juego[] datosJuegos;
    private TextView textoFavoritos;
    private Utilidades utilidades;
    private Spinner filtroPlataformas;
    private int opcionSeleccionadaPlataformas;
    private JuegosSQLHelper juegoSQLH;
    private AdaptadorJuegosLista adaptador;
    private AdView adView;

    /**
     * Llamada cuando se inicializa la actividad.
     * Realiza la consulta de favoritos y los carga en el listView
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);
        utilidades = new Utilidades(this);
        opcionSeleccionadaPlataformas = 0;
        listadoJuegos = (ListView) findViewById(R.id.listado_favoritos);
        textoFavoritos = (TextView) findViewById(R.id.texto_favoritos);
        filtroPlataformas = (Spinner) findViewById(R.id.spinner_filtro_plataforma);
        utilidades.cargarPlataformasBuscador(filtroPlataformas);
        //utilidades.cargarAnuncio();
        setupActionBar();
        cargarFavoritos(0);
        listadoJuegos.setAdapter(adaptador);
        // Filtrado de juegos
        filtroPlataformas.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 != opcionSeleccionadaPlataformas) {
                    cargarFavoritos(arg2);
                    if (datosJuegos != null) {
                        adaptador.actualizar(datosJuegos);
                    }
                    opcionSeleccionadaPlataformas = arg2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        //loadAds();

    }

    private void loadAds() {
//        //Anuncio
//        adView = new AdView(this);
//        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId("ca-app-pub-5590574021757982/9422268351");
//
//        LinearLayout layoutBanner = (LinearLayout) findViewById(R.id.layout_banner_Ads);
//        layoutBanner.addView(adView);
//
//        // Create an ad request. Check logcat output for the hashed device ID to
//        // get test ads on a physical device.
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// Emulador
//                .addTestDevice("358240057325789")
//                .build();
//
//        // Start loading the ad in the background.
//        adView.loadAd(adRequest);
    }

    /**
     * Realiza la consulta de los favoritos
     *
     * @param plataforma Plataforma para el filtro de la consulta (si es 0 se supone que es todos)
     */
    public void cargarFavoritos(int plataforma) {
        juegoSQLH = new JuegosSQLHelper(this);
        Cursor c = (plataforma == 0) ? juegoSQLH.getFavoritos() : juegoSQLH.getFavoritosPlataforma(plataforma);
        if (c != null && c.moveToFirst()) {
            datosJuegos = new Juego[c.getCount()];
            textoFavoritos.setText(c.getCount() + " juegos encontrados");
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
                Log.v("JUEGO FAVORITO", datosJuegos[i].toString());
                i++;
            }
            while (c.moveToNext());
            adaptador = new AdaptadorJuegosLista(this, datosJuegos, false);
            listadoJuegos.setAdapter(adaptador);
            listadoJuegos.setVisibility(View.VISIBLE);
            textoFavoritos.setVisibility(View.GONE);
            c.close();
        } else {
            textoFavoritos.setText("No hay resultados");
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
                Log.i("ActionBar", "Home");
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
