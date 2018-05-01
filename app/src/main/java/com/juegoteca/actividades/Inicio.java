package com.juegoteca.actividades;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.juegoteca.basedatos.Juego;
import com.juegoteca.basedatos.JuegosSQLHelper;
import com.juegoteca.util.GridJuegosMasonryAdapterHorizontal;
import com.juegoteca.util.GridJuegosItemDecorationHorizontal;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

public class Inicio extends Activity {

    private Juego[] datosJuegos;
    private Utilidades utilidades;

    private int NUM_MAX_ELEMENTOS_INICIO = 25;


    /**
     * Llamada cuando se inicializa la actividad. Carga los listados que se
     * muestran en pantalla.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        utilidades = new Utilidades(this);
        cargarUltimosAnadidos();
        cargarUltimosCompletados();

        final SharedPreferences settings = getSharedPreferences("UserInfo",
                0);

        //Ya se ha visualizado
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0);

            int versionCode = packageInfo.versionCode;
            String versionName = getPackageManager().getPackageInfo(getPackageName(),
                    0).versionName;


            if (!settings.contains("releaseNotes"+versionName)) {

                utilidades.showBuildNotes(versionName);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        // Desactiva el modo estricto
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_inicio);
        cargarUltimosAnadidos();
        cargarUltimosCompletados();
    }

    /**
     * Carga la lista con los últimos añadidos
     */
    public void cargarUltimosAnadidos() {

        JuegosSQLHelper juegosSQLH = new JuegosSQLHelper(this);
        final SharedPreferences settings = getSharedPreferences("UserInfo",
                0);

        Cursor c = null;

        if(settings.contains("orden_ultimos_fecha_compra") && settings.getBoolean("orden_ultimos_fecha_compra", true)) {
            c = juegosSQLH.getUltimosJuegosAnadidosFechaCompra();
        } else {
            c = juegosSQLH.getUltimosJuegosAnadidos();
        }

        if (c != null & c.moveToFirst()) {

            datosJuegos = new Juego[c.getCount()];
            int i = 0;
            do {
                datosJuegos[i] = new Juego();
                datosJuegos[i].setId(c.getInt(0));
                datosJuegos[i].setCaratula(c.getString(13));
                datosJuegos[i].setTitulo(c.getString(2));
                i++;
            } while (c.moveToNext());
            c.close();
        } else {
            datosJuegos = new Juego[1];
            datosJuegos[0] = new Juego();
            datosJuegos[0].setId(-1);
            datosJuegos[0].setCaratula("");
        }
        RecyclerView mRecyclerView;

        mRecyclerView = (RecyclerView) findViewById(R.id.masonry_grid_nuevos);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        mRecyclerView.setLayoutFrozen(true);

        GridJuegosMasonryAdapterHorizontal adapter = new GridJuegosMasonryAdapterHorizontal(this);
        adapter.setDatosJuegos(datosJuegos);
        mRecyclerView.setAdapter(adapter);
        GridJuegosItemDecorationHorizontal decoration = new GridJuegosItemDecorationHorizontal(8);
        mRecyclerView.addItemDecoration(decoration);


        juegosSQLH.close();


    }

    /**
     * Carga la lista con los últimos añadidos
     */
    public void cargarUltimosCompletados() {
        //listaCompletados = (HorizontalListView) findViewById(R.id.lista_completados);
        JuegosSQLHelper juegosSQLH = new JuegosSQLHelper(this);
        Cursor c = juegosSQLH.getUltimosJuegosCompletados();
        if (c != null & c.moveToFirst()) {
            datosJuegos = new Juego[c.getCount()];
            int i = 0;
            do {
                datosJuegos[i] = new Juego();
                datosJuegos[i].setId(c.getInt(0));
                datosJuegos[i].setCaratula(c.getString(13));
                datosJuegos[i].setTitulo(c.getString(2));
                i++;
            } while (c.moveToNext()); // Cargar

            c.close();

            RecyclerView mRecyclerView;

            mRecyclerView = (RecyclerView) findViewById(R.id.masonry_grid_completados);
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

            GridJuegosMasonryAdapterHorizontal adapter = new GridJuegosMasonryAdapterHorizontal(this);
            adapter.setDatosJuegos(datosJuegos);
            mRecyclerView.setAdapter(adapter);
            GridJuegosItemDecorationHorizontal decoration = new GridJuegosItemDecorationHorizontal(8);
            mRecyclerView.addItemDecoration(decoration);

        }
        juegosSQLH.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inicio, menu);
        MenuItem itemGrid = menu.findItem(R.id.action_grid);
        itemGrid.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;


        switch (item.getItemId()) {
            case R.id.action_grid:
                intent = new Intent(this, InicioMasonry.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Lanza la actividad para crear un juego
     *
     * @param view
     */
    public void nuevoJuego(View view) {
        Intent intent = new Intent(this, NuevoJuego.class);
        startActivity(intent);
    }

    /**
     * Lanza la actividad para listar los juegos
     *
     * @param view
     */
    public void listarJuegos(View view) {
        Intent intent = new Intent(this, Buscador.class);
        startActivity(intent);
    }

    /**
     * Lanza la actividad para ver las estadísitcas
     *
     * @param view
     */
    public void estadisticas(View view) {
        Intent intent = new Intent(this, Estadisitcas.class);
        startActivity(intent);
    }

    /**
     * Lanza la actividad para ver el listado de favoritos
     *
     * @param view
     */
    public void favoritos(View view) {
        Intent intent = new Intent(this, Favoritos.class);
        startActivity(intent);
    }

    /**
     * Lanza la actividad para ver el listado de pendientes
     *
     * @param view
     */
    public void pendientes(View view) {
        Intent intent = new Intent(this, Pendientes.class);
        startActivity(intent);
    }

    /**
     * Lanza la actividad para ver las opciones
     *
     * @param view
     */
    public void opciones(View view) {
        Intent intent = new Intent(this, Opciones.class);
        startActivity(intent);
    }

    public void gridJuegos(View view) {
        Intent intent = new Intent(this, InicioMasonry.class);
        startActivity(intent);
    }

    /**
     * Lanza la actividad para ver la ficha de un juego
     *
     * @param view
     */
    public void detalleJuego(View view) {
        TextView id = (TextView) view.findViewById(R.id.id_juego);
        Intent intent;
        final SharedPreferences settings = getSharedPreferences("UserInfo",
                0);
        if (Integer.parseInt(String.valueOf(id.getText())) == -1) {
            intent = new Intent(this, NuevoJuego.class);
        } else if (settings.contains("detalle_imagen") && settings.getBoolean("detalle_imagen", true)) {

            intent = new Intent(this, DetalleJuegoImagenGrande.class);
            intent.putExtra("ID_JUEGO", String.valueOf(id.getText()));
            intent.putExtra("NUEVO_JUEGO", false);
        } else {
            intent = new Intent(this, DetalleJuego.class);
            intent.putExtra("ID_JUEGO", String.valueOf(id.getText()));
            intent.putExtra("NUEVO_JUEGO", false);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        utilidades.unbindDrawables(findViewById(R.id.layout_listado));
        System.gc();
    }


}
