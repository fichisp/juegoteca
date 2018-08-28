package com.juegoteca.actividades;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.juegoteca.basedatos.Juego;
import com.juegoteca.basedatos.JuegosSQLHelper;
import com.juegoteca.util.ListadoJuegosArrayAdapter;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;


public class Pendientes extends Activity {

    private static final String EMPTY_STRING = "";
    private ListView listadoJuegos;
    private Juego[] datosJuegos;
    private TextView textoPendientes;
    private int opcionSeleccionadaPlataformas;
    private Utilidades utilidades;
    private ListadoJuegosArrayAdapter adaptador;
    private final DefaultRenderer rendererJuegosPendientesPlataforma = new DefaultRenderer();
    private final CategorySeries serieJuegosPendientesPlataforma = new CategorySeries(EMPTY_STRING);
    private static final int[] COLORS = new int[]{
            Color.rgb(255, 64, 0),
            Color.rgb(255, 128, 0),
            Color.rgb(255, 191, 0),
            Color.rgb(255, 255, 0),
            Color.rgb(191, 255, 0),
            Color.rgb(128, 255, 0),
            Color.rgb(64, 255, 0),
            Color.rgb(0, 255, 0),
            Color.rgb(0, 255, 64),
            Color.rgb(0, 255, 128),
            Color.rgb(0, 255, 191),
            Color.rgb(0, 255, 255),
            Color.rgb(0, 191, 255),
            Color.rgb(0, 128, 255),
            Color.rgb(0, 64, 255),
            Color.rgb(0, 0, 255),
            Color.rgb(64, 0, 255),
            Color.rgb(128, 0, 255),
            Color.rgb(191, 0, 255),
            Color.rgb(255, 0, 255),
            Color.rgb(255, 0, 191),
            Color.rgb(255, 0, 128),
            Color.rgb(255, 0, 64),
            Color.rgb(255, 0, 0)

    };
    private int[] numeroJuegos = null;
    private String[] etiquetas = null;
    private float pxLabelSize;
    private float pxLegendSize;
    private JuegosSQLHelper juegosSQLH;

    /**
     * Llamada cuando se inicializa la actividad.
     * Realiza la carga inicial del listado de pendientes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendientes);
        utilidades = new Utilidades(this);
        opcionSeleccionadaPlataformas = 0;
        listadoJuegos = (ListView) findViewById(R.id.listado_pendientes);
        textoPendientes = (TextView) findViewById(R.id.texto_pendientes);
        Spinner filtroPlataformas = (Spinner) findViewById(R.id.spinner_filtro_plataforma);
        utilidades.cargarPlataformasBuscador(filtroPlataformas);
        setupActionBar();
        cargarPendientes(0);//Todas las plataformas

        juegosSQLH = new JuegosSQLHelper(this);
        //Calculo del tamaño en pixeles de las etiquetas para todos los gráficos
        Resources r = getResources();
        pxLabelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, r.getDisplayMetrics());
        pxLegendSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());

        gamesPendingByPlatformGraphic("SPA");
        findViewById(R.id.graphicLayout).setVisibility(View.VISIBLE);

        ListView list = (ListView) findViewById(R.id.listado_pendientes);
        setListViewHeightBasedOnChildren(list);

        // Filtrado de juegos
        filtroPlataformas.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 != opcionSeleccionadaPlataformas) {//Si cambia la opción seleccionada
                    findViewById(R.id.graphicLayout).setVisibility(View.GONE);
                    cargarPendientes(arg2);
                    if (datosJuegos != null) {
                        adaptador.actualizar(datosJuegos);
                    }
                    opcionSeleccionadaPlataformas = arg2;

                    if(opcionSeleccionadaPlataformas==0){
                        findViewById(R.id.graphicLayout).setVisibility(View.VISIBLE);
                    }
                    ListView list = (ListView) findViewById(R.id.listado_pendientes);
                    setListViewHeightBasedOnChildren(list);
                    ((ScrollView)findViewById(R.id.scrollPendientes)).fullScroll(ScrollView.FOCUS_UP);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }



    /**
     * Realiza la consulta a la base de datos para obtener los juegos pendientes.
     *
     * @param plataforma Plataforma para el filtro (si es 0 se devuelven todos los elementos)
     */
    private void cargarPendientes(int plataforma) {
        listadoJuegos = (ListView) findViewById(R.id.listado_pendientes);
        textoPendientes = (TextView) findViewById(R.id.texto_pendientes);
        JuegosSQLHelper juegoSQLH = new JuegosSQLHelper(this);
        Cursor c = (plataforma == 0) ? juegoSQLH.getPendientes() : juegoSQLH.getPendientesPlataforma(plataforma);
        // Si hay elementos en el cursor
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
            adaptador = new ListadoJuegosArrayAdapter(this, datosJuegos);
            listadoJuegos.setAdapter(adaptador);
            listadoJuegos.setVisibility(View.VISIBLE);
            textoPendientes.setText(datosJuegos.length + " " + getString(R.string.juegos));
            //textoPendientes.setVisibility(View.GONE);
            c.close();
        } else {
            textoPendientes.setText("0 " + getString(R.string.juegos));
            textoPendientes.setVisibility(View.VISIBLE);
            listadoJuegos.setVisibility(View.GONE);
        }
    }

    /**
     * Lanza la actividad para ver la ficha de un juego
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
        intent.putExtra("CALLER", "ListadoPendientes");
        intent.putExtra("NUEVO_JUEGO", false);
        if (getIntent().getBooleanExtra("GRID", false)) {
            intent.putExtra("GRID", true);
        }
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pendientes, menu);
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

        // unbind all drawables starting from the first viewgroup
        utilidades.unbindDrawables(findViewById(R.id.linear_pendientes_all));

        // indicate to the vm that it would be a good time to run the gc
        System.gc();
    }

    /**
     * PieChart by platform
     *
     * @param codigoIdioma
     */
    private void gamesPendingByPlatformGraphic(String codigoIdioma) {

        rendererJuegosPendientesPlataforma.setApplyBackgroundColor(true);
        rendererJuegosPendientesPlataforma.setLabelsTextSize(pxLabelSize);
        rendererJuegosPendientesPlataforma.setLabelsColor(Color.BLACK);
        rendererJuegosPendientesPlataforma.setShowLegend(false);
        rendererJuegosPendientesPlataforma.setPanEnabled(false);
        rendererJuegosPendientesPlataforma.setZoomEnabled(false);
        rendererJuegosPendientesPlataforma.setShowLabels(false);
        rendererJuegosPendientesPlataforma.setScale(1.35f);
        rendererJuegosPendientesPlataforma.setStartAngle(0);
        rendererJuegosPendientesPlataforma.setShowLabels(false);
        rendererJuegosPendientesPlataforma.setAntialiasing(true);


        Cursor cursorJuegosPlataforma = juegosSQLH.getJuegosPendientesPorPlataforma();



        if (cursorJuegosPlataforma != null
                && cursorJuegosPlataforma.moveToFirst()) {

            numeroJuegos = new int[cursorJuegosPlataforma.getCount()];
            etiquetas = new String[cursorJuegosPlataforma.getCount()];

            int i = 0;
            do {
                numeroJuegos[i] = cursorJuegosPlataforma.getInt(0);
                etiquetas[i] = cursorJuegosPlataforma.getString(1);
                i++;
            } while (cursorJuegosPlataforma.moveToNext());

            LinearLayout footL = (LinearLayout) findViewById(R.id.linear_estadisticas_3_1_foot_left);
            LinearLayout footR = (LinearLayout) findViewById(R.id.linear_estadisticas_3_1_foot_right);

            for (int j = 0; j < numeroJuegos.length; j++) {
                int x;

                try {
                    x = numeroJuegos[j];
                } catch (NumberFormatException e) {
                    x = 0;
                }

                serieJuegosPendientesPlataforma.add(etiquetas[j], x);

                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();

                int color = COLORS[(serieJuegosPendientesPlataforma.getItemCount() - 1)
                        % COLORS.length];

                renderer.setColor(color);

                //Añadimos a la falsa leyenda una entrada
                LinearLayout tmpFoot = new LinearLayout(this);
                tmpFoot.setOrientation(LinearLayout.HORIZONTAL);


                TextView tmpColor = new TextView(this);
                tmpColor.setHeight(50);
                tmpColor.setWidth(50);
                tmpColor.setTextSize(12);
                tmpColor.setPadding(0, 0, 0, 10);
                tmpColor.setGravity(Gravity.TOP);
                tmpColor.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tmpColor.setBackgroundColor(color);

                TextView tmp = new TextView(this);
                tmp.setText(etiquetas[j] + " (" + numeroJuegos[j] + ")");
                tmp.setTextSize(12);

                tmp.setGravity(Gravity.TOP);
                tmp.setPadding(15, 0, 0, 10);
                tmp.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                tmpFoot.addView(tmpColor);
                tmpFoot.addView(tmp);

                tmpFoot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                //foot.addView(tmpFoot);
                if (j % 2 == 0) {
                    footL.addView(tmpFoot);
                } else {
                    footR.addView(tmpFoot);
                }

                rendererJuegosPendientesPlataforma.addSeriesRenderer(renderer);

            }

            LinearLayout layout = (LinearLayout) findViewById(R.id.linea_estadisticas_3_1_layout_pie);


            GraphicalView graficoJuegosPlataforma = ChartFactory.getPieChartView(this,
                    serieJuegosPendientesPlataforma, rendererJuegosPendientesPlataforma);


            layout.addView(graficoJuegosPlataforma);


            cursorJuegosPlataforma.close();
        }
    }
}
