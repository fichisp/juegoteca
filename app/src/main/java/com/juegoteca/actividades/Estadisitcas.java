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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juegoteca.basedatos.JuegosSQLHelper;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Estadísitcas
 */
public class Estadisitcas extends Activity {

    private static final String TYPE = "type";
    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_COMPLETED = "Completed";
    private static final String STATUS_NO_COMPLETADO = "Pendiente";
    private static final String STATUS_COMPLETADO = "Completado";
    private static final String FORMAT_N_D = "N/D";
    private static final String FORMAT_RETAIL_EN = "Retail";
    private static final String FORMAT_DIGITAL = "Digital";
    private static final String FORMAT_RETAIL_ES = "Físico";
    private static final String F = "F";
    private static final String D = "D";
    private static final String EMPTY_STRING = "";
    private static final String ZERO_STRING = "0";
    private static final String SPA = "spa";

    private static int[] COLORS = new int[]{
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


    private CategorySeries serieJuegosPlataforma = new CategorySeries(EMPTY_STRING);
    private CategorySeries serieJuegosCompletados = new CategorySeries(EMPTY_STRING);
    private CategorySeries serieJuegosGenero = new CategorySeries(EMPTY_STRING);
    private CategorySeries serieJuegosFormato = new CategorySeries(EMPTY_STRING);
    private DefaultRenderer rendererJuegosPlataforma = new DefaultRenderer();
    private DefaultRenderer rendererJuegosCompletados = new DefaultRenderer();
    private DefaultRenderer rendererJuegosGenero = new DefaultRenderer();
    private DefaultRenderer rendererJuegosFormato = new DefaultRenderer();
    private GraphicalView graficoJuegosPlataforma;
    private GraphicalView graficoJuegosCompletados;
    private GraphicalView graficoJuegosGenero;
    private GraphicalView graficoJuegosFormato;
    private int[] numeroJuegos = null;
    private String[] etiquetas = null;
    private JuegosSQLHelper juegosSQLH;
    private Utilidades utilidades;
    private float pxLabelSize;
    private float pxLegendSize;

    /**
     * Llamada cuando se inicializa la actividad. Crea los gráficos que se
     * mostrarán
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisitcas);

        String codigoIdioma = Locale.getDefault().getISO3Language();

        utilidades = new Utilidades(this);
        juegosSQLH = new JuegosSQLHelper(this);

        setupActionBar();

        //Calculo del tamaño en pixeles de las etiquetas para todos los gráficos
        Resources r = getResources();
        pxLabelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, r.getDisplayMetrics());
        pxLegendSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());

        // Juegos agrupados por plataforma
        gamesByPlatformGraphic(codigoIdioma);

        // Juegos agrupados por genero
        gamesByGenreGrpahic(codigoIdioma);

        // Juegos agrupados por formato
        gamesByFormatGraphic(codigoIdioma);

        // Juegos agrupados por completado
        gamesByStatusGraphic(codigoIdioma);
    }


    /**
     * PieChart by status
     *
     * @param codigoIdioma
     */
    private void gamesByStatusGraphic(String codigoIdioma) {

        rendererJuegosCompletados.setApplyBackgroundColor(true);
        rendererJuegosCompletados.setLabelsTextSize(pxLabelSize);
        rendererJuegosCompletados.setLegendTextSize(pxLegendSize);
        rendererJuegosCompletados.setLabelsColor(Color.BLACK);
        rendererJuegosCompletados.setPanEnabled(false);
        rendererJuegosCompletados.setShowLegend(false);
        rendererJuegosCompletados.setShowLabels(false);
        rendererJuegosCompletados.setZoomEnabled(false);
        rendererJuegosCompletados.setFitLegend(false);
        rendererJuegosCompletados.setScale(1.35f);
        rendererJuegosCompletados.setAntialiasing(true);

        Cursor cursorJuegosCompletados = juegosSQLH.getJuegosCompletados();
        if (cursorJuegosCompletados != null
                && cursorJuegosCompletados.moveToFirst()) {

            numeroJuegos = new int[cursorJuegosCompletados.getCount()];
            etiquetas = new String[cursorJuegosCompletados.getCount()];

            int i = 0;

            //Procesarmos el cursor para generar las etiquetas y valores
            do {

                numeroJuegos[i] = cursorJuegosCompletados.getInt(0);

                if (!SPA.equalsIgnoreCase(codigoIdioma)) {
                    if (cursorJuegosCompletados.getString(1).compareTo(ZERO_STRING) == 0) {
                        etiquetas[i] = STATUS_PENDING;
                    } else {
                        etiquetas[i] = STATUS_COMPLETED;
                    }
                } else {
                    if (cursorJuegosCompletados.getString(1).compareTo(ZERO_STRING) == 0) {
                        etiquetas[i] = STATUS_NO_COMPLETADO;
                    } else {
                        etiquetas[i] = STATUS_COMPLETADO;
                    }
                }

                i++;
            } while (cursorJuegosCompletados.moveToNext());


            //Procesamos las etiquetas y valores
            for (int j = 0; j < numeroJuegos.length; j++) {
                int x = 0;
                try {
                    x = numeroJuegos[j];
                } catch (NumberFormatException e) {
                    x = 0;
                }
                serieJuegosCompletados.add(etiquetas[j] + " (" + x + ")", x);
                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();

                if (STATUS_NO_COMPLETADO.equals(etiquetas[j]) || STATUS_PENDING.equals(etiquetas[j])) {
                    renderer.setColor(Color.rgb(255, 69, 0));
                } else {
                    renderer.setColor(Color.rgb(0, 255, 0));
                }

                rendererJuegosCompletados.addSeriesRenderer(renderer);

                LinearLayout foot = (LinearLayout) findViewById(R.id.linear_estadisticas_3_foot);
                LinearLayout footL = (LinearLayout) findViewById(R.id.linear_estadisticas_3_foot_left);
                LinearLayout footR = (LinearLayout) findViewById(R.id.linear_estadisticas_3_foot_right);

                LinearLayout tmpFoot = new LinearLayout(this);
                tmpFoot.setOrientation(LinearLayout.HORIZONTAL);


                TextView tmpColor = new TextView(this);
                tmpColor.setHeight(50);
                tmpColor.setWidth(50);
                tmpColor.setTextSize(12);
                tmpColor.setPadding(0, 0, 0, 10);
                tmpColor.setGravity(Gravity.TOP);
                tmpColor.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tmpColor.setBackgroundColor(renderer.getColor());

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


            }

            LinearLayout layout = (LinearLayout) findViewById(R.id.linear_estadisticas_3);
            graficoJuegosCompletados = ChartFactory.getPieChartView(this,
                    serieJuegosCompletados, rendererJuegosCompletados);

            graficoJuegosCompletados.setPadding(0,20,0,0);
            layout.addView(graficoJuegosCompletados);

            cursorJuegosCompletados.close();
        }
    }

    /**
     * * PieChart by format
     * @param codigoIdioma
     */
    private void gamesByFormatGraphic(String codigoIdioma) {
        rendererJuegosFormato.setApplyBackgroundColor(true);
        rendererJuegosFormato.setLabelsTextSize(pxLabelSize);
        rendererJuegosFormato.setLegendTextSize(pxLegendSize);
        rendererJuegosFormato.setLabelsColor(Color.BLACK);
        rendererJuegosFormato.setShowLegend(false);
        rendererJuegosFormato.setShowLabels(false);
        rendererJuegosFormato.setPanEnabled(false);
        rendererJuegosFormato.setZoomEnabled(false);
        rendererJuegosFormato.setFitLegend(false);
        rendererJuegosFormato.setScale(1.35f);
        rendererJuegosFormato.setAntialiasing(true);

        Cursor cursorJuegosFormato = juegosSQLH.getJuegosPorFormato();

        if (cursorJuegosFormato != null && cursorJuegosFormato.moveToFirst()) {

            numeroJuegos = new int[cursorJuegosFormato.getCount()];
            etiquetas = new String[cursorJuegosFormato.getCount()];
            int i = 0;
            do {
                numeroJuegos[i] = cursorJuegosFormato.getInt(0);

                if (!SPA.equalsIgnoreCase(codigoIdioma)) {
                    if (cursorJuegosFormato.getString(1).equals(F)) {
                        etiquetas[i] = FORMAT_RETAIL_EN;
                    }
                    if (cursorJuegosFormato.getString(1).equals(D)) {
                        etiquetas[i] = FORMAT_DIGITAL;
                    }
                    if (cursorJuegosFormato.getString(1).equals(EMPTY_STRING)) {
                        etiquetas[i] = FORMAT_N_D;
                    }
                } else {
                    if (cursorJuegosFormato.getString(1).equals(F)) {
                        etiquetas[i] = FORMAT_RETAIL_ES;
                    }
                    if (cursorJuegosFormato.getString(1).equals(D)) {
                        etiquetas[i] = FORMAT_DIGITAL;
                    }
                    if (cursorJuegosFormato.getString(1).equals(EMPTY_STRING)) {
                        etiquetas[i] = FORMAT_N_D;
                    }
                }

                i++;
            } while (cursorJuegosFormato.moveToNext());




            int[] coloresFormato = new int[]{Color.rgb(255, 64, 0), Color.rgb(255, 215, 0)};

            for (int j = 0; j < numeroJuegos.length; j++) {
                int x = 0;
                try {
                    x = numeroJuegos[j];
                } catch (NumberFormatException e) {
                    x = 0;
                }
                serieJuegosFormato.add(etiquetas[j] + "(" + x + ")", x);
                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
                renderer.setColor(coloresFormato[(serieJuegosFormato.getItemCount() - 1)
                        % coloresFormato.length]);
                rendererJuegosFormato.addSeriesRenderer(renderer);


                LinearLayout foot = (LinearLayout) findViewById(R.id.linear_estadisticas_4_foot);
                LinearLayout footL = (LinearLayout) findViewById(R.id.linear_estadisticas_4_foot_left);
                LinearLayout footR = (LinearLayout) findViewById(R.id.linear_estadisticas_4_foot_right);

                LinearLayout tmpFoot = new LinearLayout(this);
                tmpFoot.setOrientation(LinearLayout.HORIZONTAL);


                TextView tmpColor = new TextView(this);
                tmpColor.setHeight(50);
                tmpColor.setWidth(50);
                tmpColor.setTextSize(12);
                tmpColor.setPadding(0, 0, 0, 10);
                tmpColor.setGravity(Gravity.TOP);
                tmpColor.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tmpColor.setBackgroundColor(renderer.getColor());

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

            }

            LinearLayout layout = (LinearLayout) findViewById(R.id.linear_estadisticas_4);
            graficoJuegosFormato = ChartFactory.getPieChartView(this,
                    serieJuegosFormato, rendererJuegosFormato);


            layout.addView(graficoJuegosFormato);

            cursorJuegosFormato.close();
        }
    }

    /**
     * PieChart by genre
     * @param codigoIdioma
     */
    private void gamesByGenreGrpahic(String codigoIdioma) {
        rendererJuegosGenero.setApplyBackgroundColor(true);
        rendererJuegosGenero.setLabelsTextSize(pxLabelSize);
        rendererJuegosGenero.setLegendTextSize(pxLabelSize);
        rendererJuegosGenero.setLabelsColor(Color.BLACK);
        rendererJuegosGenero.setShowLegend(false);
        rendererJuegosGenero.setPanEnabled(false);
        rendererJuegosGenero.setZoomEnabled(false);
        rendererJuegosGenero.setScale(1.35f);
        rendererJuegosGenero.setShowLabels(false);
        rendererJuegosGenero.setAntialiasing(true);

        Cursor cursorJuegosGenero = null;

        if (!SPA.equalsIgnoreCase(codigoIdioma)) {
            cursorJuegosGenero = juegosSQLH.getJuegosGeneroEN();
        } else {
            cursorJuegosGenero = juegosSQLH.getJuegosGenero();
        }


        if (cursorJuegosGenero != null && cursorJuegosGenero.moveToFirst()) {

            numeroJuegos = new int[cursorJuegosGenero.getCount()];
            etiquetas = new String[cursorJuegosGenero.getCount()];
            int i = 0;
            do {
                numeroJuegos[i] = cursorJuegosGenero.getInt(0);
                etiquetas[i] = cursorJuegosGenero.getString(1);
                i++;
            } while (cursorJuegosGenero.moveToNext());

            LinearLayout foot = (LinearLayout) findViewById(R.id.linear_estadisticas_2_foot);
            LinearLayout footL = (LinearLayout) findViewById(R.id.linear_estadisticas_2_foot_left);
            LinearLayout footR = (LinearLayout) findViewById(R.id.linear_estadisticas_2_foot_right);

            for (int j = 0; j < numeroJuegos.length; j++) {
                int x = 0;
                try {
                    x = numeroJuegos[j];
                } catch (NumberFormatException e) {
                    x = 0;
                }

                serieJuegosGenero.add(etiquetas[j], x);
                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();


                int color = COLORS[(serieJuegosGenero.getItemCount() - 1)
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


                rendererJuegosGenero.addSeriesRenderer(renderer);

            }

            LinearLayout layout = (LinearLayout) findViewById(R.id.linea_estadisticas_2_layout_pie);
            graficoJuegosGenero = ChartFactory.getPieChartView(this,
                    serieJuegosGenero, rendererJuegosGenero);

            layout.addView(graficoJuegosGenero);

            cursorJuegosGenero.close();
        }
    }

    /**
     * PieChart by platform
     *
     * @param codigoIdioma
     */
    private void gamesByPlatformGraphic(String codigoIdioma) {

        rendererJuegosPlataforma.setApplyBackgroundColor(true);
        rendererJuegosPlataforma.setLabelsTextSize(pxLabelSize);
        rendererJuegosPlataforma.setLabelsColor(Color.BLACK);
        rendererJuegosPlataforma.setShowLegend(false);
        rendererJuegosPlataforma.setPanEnabled(false);
        rendererJuegosPlataforma.setZoomEnabled(false);
        rendererJuegosPlataforma.setShowLabels(false);
        rendererJuegosPlataforma.setScale(1.35f);
        rendererJuegosPlataforma.setStartAngle(0);
        rendererJuegosPlataforma.setShowLabels(false);
        rendererJuegosPlataforma.setAntialiasing(true);

        Cursor cursorJuegos = juegosSQLH.getJuegos();
        int total = cursorJuegos.getCount();
        cursorJuegos.close();

        Float valorColeccion = juegosSQLH.getValorColeccion();
        Integer juegosConPrecio = juegosSQLH.getCountJuegosConPrecio();

        TextView resumenTotal = (TextView) findViewById(R.id.text_view_resumen_total);
        TextView resumenSum = (TextView) findViewById(R.id.text_view_resumen_sum);
        TextView resumenAVG = (TextView) findViewById(R.id.text_view_resumen_avg);

        resumenTotal.setText(String.valueOf(total));

        if (valorColeccion != null && juegosConPrecio != null && juegosConPrecio > 0) {

            final SharedPreferences settings = getSharedPreferences("UserInfo",
                    0);
            String currency = settings.getString("currency","");
            String coleccionValueText = String.valueOf(valorColeccion) + " " + currency;
            BigDecimal bd = new BigDecimal(Float.toString(valorColeccion / juegosConPrecio));
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            String avgText = String.valueOf(bd.floatValue()) + " " + currency;

            if (SPA.equalsIgnoreCase(codigoIdioma)) {
                coleccionValueText=coleccionValueText.replace(".", ",");
                avgText=avgText.replace(".",",");
            }

            resumenSum.setText(coleccionValueText);
            resumenAVG.setText(avgText);

        } else {
            resumenSum.setText("");
            resumenAVG.setText("");
        }


        Cursor cursorJuegosPlataforma = juegosSQLH.getJuegosPorPlataforma();



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

            LinearLayout foot = (LinearLayout) findViewById(R.id.linear_estadisticas_1_foot);
            LinearLayout footL = (LinearLayout) findViewById(R.id.linear_estadisticas_1_foot_left);
            LinearLayout footR = (LinearLayout) findViewById(R.id.linear_estadisticas_1_foot_right);

            for (int j = 0; j < numeroJuegos.length; j++) {
                int x = 0;

                try {
                    x = numeroJuegos[j];
                } catch (NumberFormatException e) {
                    x = 0;
                }

                serieJuegosPlataforma.add(etiquetas[j], x);

                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();

                int color = COLORS[(serieJuegosPlataforma.getItemCount() - 1)
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

                rendererJuegosPlataforma.addSeriesRenderer(renderer);

            }

            LinearLayout layout = (LinearLayout) findViewById(R.id.linea_estadisticas_1_layout_pie);


            graficoJuegosPlataforma = ChartFactory.getPieChartView(this,
                    serieJuegosPlataforma, rendererJuegosPlataforma);


            layout.addView(graficoJuegosPlataforma);


            cursorJuegosPlataforma.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.listado_juego, menu);
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
            case android.R.id.home:
                onBackPressed();
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
}
