package com.juegoteca.actividades;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juegoteca.basedatos.JuegosSQLHelper;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.model.XYValueSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Random;

/**
 * Estadísitcas
 */
public class Estadisitcas extends Activity {

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


    private final CategorySeries serieJuegosPlataforma = new CategorySeries(EMPTY_STRING);
    private final CategorySeries serieJuegosPendientesPlataforma = new CategorySeries(EMPTY_STRING);
    private final CategorySeries serieJuegosCompletados = new CategorySeries(EMPTY_STRING);
    private final CategorySeries serieJuegosGenero = new CategorySeries(EMPTY_STRING);
    private final CategorySeries serieJuegosFormato = new CategorySeries(EMPTY_STRING);
    private final CategorySeries serieJuegosAnyo = new CategorySeries(EMPTY_STRING);
    private final DefaultRenderer rendererJuegosAnyo = new DefaultRenderer();
    private final DefaultRenderer rendererJuegosPlataforma = new DefaultRenderer();
    private final DefaultRenderer rendererJuegosPendientesPlataforma = new DefaultRenderer();
    private final DefaultRenderer rendererJuegosCompletados = new DefaultRenderer();
    private final DefaultRenderer rendererJuegosGenero = new DefaultRenderer();
    private final DefaultRenderer rendererJuegosFormato = new DefaultRenderer();
    private int[] numeroJuegos = null;
    private String[] etiquetas = null;
    private JuegosSQLHelper juegosSQLH;
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

        Utilidades utilidades = new Utilidades(this);
        juegosSQLH = new JuegosSQLHelper(this);

        setupActionBar();

        //Calculo del tamaño en pixeles de las etiquetas para todos los gráficos
        Resources r = getResources();
        pxLabelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, r.getDisplayMetrics());
        pxLegendSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());

        // Resumen
        collectionSummary(codigoIdioma);

        //Detalle
        detailValueByPlatform();
        detailValueByYear();

        // Juegos agrupados por plataforma
        gamesByPlatformGraphic(codigoIdioma);

        // Juegos agrupados por genero
        gamesByGenreGrpahic(codigoIdioma);

        // Juegos agrupados por formato
        gamesByFormatGraphic(codigoIdioma);

        // Juegos por año de compra
        gamesByYearGraphic(codigoIdioma);

        // Juegos agrupados por completado
        gamesByStatusGraphic(codigoIdioma);

        gamesPendingByPlatformGraphic(codigoIdioma);
    }


    private void detailValueByPlatform(){


        Cursor c = juegosSQLH.getSumPrecioPorPlataforma();


        if (c != null && c.moveToFirst()) {

            Float[] sum = new Float[c.getCount()];
            etiquetas = new String[c.getCount()];
            int j = 0;
            do {
                sum[j] = c.getFloat(0);
                etiquetas[j] = c.getString(1);
                j++;
            } while (c.moveToNext());


            LinearLayout linear  = (LinearLayout) findViewById(R.id.horizontalbars);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;


            for (int i = 0; i < etiquetas.length; i++) {

                if (sum[i] > 0) {


                    int maxWidth = linear.getWidth();

                    LinearLayout lay = new LinearLayout(this);
                    LinearLayout lay1 = new LinearLayout(this);

                    TextView text = new TextView(this);
                    text.setText(etiquetas[i]);
                    text.setTextColor(Color.BLACK);
                    text.setLayoutParams(new LinearLayout.LayoutParams((int) (width*0.3), 50));
                    text.setTextSize(12);

                    TextView line = new TextView(this);
                    line.setBackgroundColor(Color.BLACK);
                    line.setLayoutParams(new LinearLayout.LayoutParams(1, 90));

                    TextView btn = new TextView(this);

                    btn.setBackgroundColor(Color.rgb(231,118,26));





                    btn.setLayoutParams(new LinearLayout.LayoutParams((int) ((sum[i]/ (width*0.6)*100)), 40));

                    TextView text1 = new TextView(this);

                    BigDecimal bd = new BigDecimal(Float.toString(sum[i]));
                    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);

                    final SharedPreferences settings = getSharedPreferences("UserInfo",
                            0);
                    String currency = settings.getString("currency","");


                    text1.setText(bd.toString()+ " " + currency);
                    text1.setTextSize(12);
                    text1.setPadding(10,0,0,0);

                    text1.setTextColor(Color.parseColor("#000000"));
                    text1.setLayoutParams(new LinearLayout.LayoutParams(400, 40));
                    lay1.setOrientation(LinearLayout.HORIZONTAL);
                    lay1.setGravity(Gravity.CENTER_VERTICAL);
                    lay1.setPadding(0, 0, 0, 0);
                    lay1.addView(text);
                    lay1.addView(line);
                    lay1.addView(btn);
                    lay1.addView(text1);
                    lay.setOrientation(LinearLayout.VERTICAL);
                    lay.addView(lay1);
                    linear.addView(lay);
                }
            }
        }


    }

    private void detailValueByYear(){


        Cursor c = juegosSQLH.getSumPrecioPorAnyo();


        if (c != null && c.moveToFirst()) {

            Float[] sum = new Float[c.getCount()];
            etiquetas = new String[c.getCount()];
            int j = 0;
            do {
                sum[j] = c.getFloat(0);
                etiquetas[j] = c.getString(1);
                j++;
            } while (c.moveToNext());


            LinearLayout linear  = (LinearLayout) findViewById(R.id.horizontalbarsyear);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;

            for (int i = 0; i < etiquetas.length; i++) {

                if (sum[i] > 0 && !"1970".equals(etiquetas[i])) {


                    int maxWidth = linear.getWidth();

                    LinearLayout lay = new LinearLayout(this);
                    LinearLayout lay1 = new LinearLayout(this);

                    TextView text = new TextView(this);
                    text.setText(etiquetas[i]);
                    text.setTextColor(Color.BLACK);
                    text.setLayoutParams(new LinearLayout.LayoutParams((int) (width*0.2), 50));
                    text.setTextSize(12);

                    TextView line = new TextView(this);
                    line.setBackgroundColor(Color.BLACK);
                    line.setLayoutParams(new LinearLayout.LayoutParams(1, 90));

                    TextView btn = new TextView(this);

                    btn.setBackgroundColor(Color.rgb(231,118,26));





                    btn.setLayoutParams(new LinearLayout.LayoutParams((int) ((sum[i]/ (width*0.8)*100)), 40));

                    TextView text1 = new TextView(this);

                    BigDecimal bd = new BigDecimal(Float.toString(sum[i]));
                    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);

                    final SharedPreferences settings = getSharedPreferences("UserInfo",
                            0);
                    String currency = settings.getString("currency","");


                    text1.setText(bd.toString()+ " " + currency);
                    text1.setTextSize(12);
                    text1.setPadding(10,0,0,0);

                    text1.setTextColor(Color.parseColor("#000000"));
                    text1.setLayoutParams(new LinearLayout.LayoutParams(400, 40));
                    lay1.setOrientation(LinearLayout.HORIZONTAL);
                    lay1.setGravity(Gravity.CENTER_VERTICAL);
                    lay1.setPadding(0, 0, 0, 0);
                    lay1.addView(text);
                    lay1.addView(line);
                    lay1.addView(btn);
                    lay1.addView(text1);
                    lay.setOrientation(LinearLayout.VERTICAL);
                    lay.addView(lay1);
                    linear.addView(lay);
                }
            }
        }


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
                int x;
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
            GraphicalView graficoJuegosCompletados = ChartFactory.getPieChartView(this,
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
                int x;
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
            GraphicalView graficoJuegosFormato = ChartFactory.getPieChartView(this,
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

        Cursor cursorJuegosGenero;

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

            LinearLayout footL = (LinearLayout) findViewById(R.id.linear_estadisticas_2_foot_left);
            LinearLayout footR = (LinearLayout) findViewById(R.id.linear_estadisticas_2_foot_right);

            for (int j = 0; j < numeroJuegos.length; j++) {
                int x;
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
            GraphicalView graficoJuegosGenero = ChartFactory.getPieChartView(this,
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

            LinearLayout footL = (LinearLayout) findViewById(R.id.linear_estadisticas_1_foot_left);
            LinearLayout footR = (LinearLayout) findViewById(R.id.linear_estadisticas_1_foot_right);

            for (int j = 0; j < numeroJuegos.length; j++) {
                int x;

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


            GraphicalView graficoJuegosPlataforma = ChartFactory.getPieChartView(this,
                    serieJuegosPlataforma, rendererJuegosPlataforma);


            layout.addView(graficoJuegosPlataforma);


            cursorJuegosPlataforma.close();
        }
    }

    private void collectionSummary(String codigoIdioma) {
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


    /**
     * PieChart by platform
     *
     * @param codigoIdioma
     */
    private void gamesByYearGraphic(String codigoIdioma) {

        rendererJuegosAnyo.setApplyBackgroundColor(true);
        rendererJuegosAnyo.setLabelsTextSize(pxLabelSize);
        rendererJuegosAnyo.setLabelsColor(Color.BLACK);
        rendererJuegosAnyo.setShowLegend(false);
        rendererJuegosAnyo.setPanEnabled(false);
        rendererJuegosAnyo.setZoomEnabled(false);
        rendererJuegosAnyo.setShowLabels(false);
        rendererJuegosAnyo.setScale(1.35f);
        rendererJuegosAnyo.setStartAngle(0);
        rendererJuegosAnyo.setShowLabels(false);
        rendererJuegosAnyo.setAntialiasing(true);


        Cursor cursorJuegosPlataforma = juegosSQLH.getCountPorAnyo();



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

            LinearLayout footL = (LinearLayout) findViewById(R.id.linear_estadisticas_6_foot_left);
            LinearLayout footR = (LinearLayout) findViewById(R.id.linear_estadisticas_6_foot_right);

            for (int j = 0; j < numeroJuegos.length; j++) {


                int x;

                try {
                    x = numeroJuegos[j];
                } catch (NumberFormatException e) {
                    x = 0;
                }

                if(!"1970".equals(etiquetas[j])){
                    serieJuegosAnyo.add(etiquetas[j], x);
                } else {
                    serieJuegosAnyo.add(getString(R.string.other_year), x);
                }



                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();

                int color = COLORS[(serieJuegosAnyo.getItemCount() - 1)
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

                if(!"1970".equals(etiquetas[j])){
                    tmp.setText(etiquetas[j] + " (" + numeroJuegos[j] + ")");
                } else {
                    tmp.setText(getString(R.string.other_year) + " (" + numeroJuegos[j] + ")");
                }

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

                rendererJuegosAnyo.addSeriesRenderer(renderer);


            }

            LinearLayout layout = (LinearLayout) findViewById(R.id.linea_estadisticas_6_layout_pie);


            GraphicalView graficoJuegosPlataforma = ChartFactory.getPieChartView(this,
                    serieJuegosAnyo, rendererJuegosAnyo);


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
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    /**
     * Lanza la actividad para crear un juego
     *
     * @param view
     */
    public void toggleValueByPlatform(View view) {
        LinearLayout layoutValueDetails = (LinearLayout) findViewById(R.id.layoutValueDetails);
        Button buttonShowValueByPlatform = (Button) findViewById(R.id.buttonShowValueByPlatform);

        if(View.VISIBLE == layoutValueDetails.getVisibility()) {
            layoutValueDetails.setVisibility(View.GONE);
            buttonShowValueByPlatform.setText("+");
        } else {
            layoutValueDetails.setVisibility(View.VISIBLE);
            buttonShowValueByPlatform.setText("-");
        }
    }
    //
}
