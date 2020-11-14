package com.juegoteca.actividades;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.juegoteca.basedatos.Juego;
import com.juegoteca.basedatos.JuegosSQLHelper;
import com.juegoteca.util.CheckLaunchDatesService;
import com.juegoteca.util.GridJuegosMasonryAdapterHorizontal;
import com.juegoteca.util.GridJuegosItemDecorationHorizontal;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Inicio extends Activity {

    private Juego[] datosJuegos;
    private Utilidades utilidades;


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
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(),
                    0).versionName;

            if (!settings.contains("releaseNotes"+versionName)) {

                utilidades.showBuildNotes(versionName);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        // Desactiva el modo estricto
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //scheduling lanch dates job
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        String currentDay = dateFormat.format(new Date());

        if (!settings.contains("27021985_"+currentDay)) {
            scheduleJobCheckLaunchDate(currentDay);
        }
        //utilidades.checkLaunchDates();


        checkPermission();





    }

    private void checkPermission(){
        String[] permissions = new String []{"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.CAMERA"};
        requestPermissions(permissions,200);
    }



    private void scheduleJobCheckLaunchDate(final String currentDay){



        final long ONE_DAY_INTERVAL = 24 * 60 * 60 * 1000L; // 1 Day

        @SuppressLint("WrongConstant") JobScheduler jobScheduler = (JobScheduler)getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);

        boolean hasBeenScheduled = false ;

        for ( JobInfo jobInfo : jobScheduler.getAllPendingJobs() ) {
            if ( jobInfo.getId() == 27021985 ) {
                hasBeenScheduled = true ;
                break ;
            }
        }

        if(!hasBeenScheduled) {
            ComponentName componentName = new ComponentName(this,
                    CheckLaunchDatesService.class);


            JobInfo.Builder builder = new JobInfo.Builder(27021985, componentName);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
            builder.setPeriodic(ONE_DAY_INTERVAL);
            PersistableBundle extras = new PersistableBundle();
            extras.putString("currentDay",currentDay);
            builder.setExtras(extras);
            jobScheduler.schedule(builder.build());
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
    private void cargarUltimosAnadidos() {

        JuegosSQLHelper juegosSQLH = new JuegosSQLHelper(this);
        final SharedPreferences settings = getSharedPreferences("UserInfo",
                0);

        Cursor c;

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
    private void cargarUltimosCompletados() {
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
            case R.id.action_options:
                intent = new Intent(this, Opciones.class);
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
    public void undiacomohoy(View view) {
        Intent intent = new Intent(this, UnDiaComoHoy.class);
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
