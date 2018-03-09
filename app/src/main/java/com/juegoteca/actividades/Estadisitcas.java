package com.juegoteca.actividades;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.juegoteca.basedatos.JuegosSQLHelper;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class Estadisitcas extends Activity {

    public static final String TYPE = "type";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_NO_COMPLETADO = "NO COMPLETADO";
    public static final String STATUS_COMPLETADO = "COMPLETADO";
    public static final String FORMAT_N_D = "N/D";
    public static final String FORMAT_RETAIL_EN = "Retail";
    public static final String FORMAT_DIGITAL = "Digital";
    public static final String FORMAT_RETAIL_ES = "Fisico";
    public static final String F = "F";
    public static final String D = "D";
    public static final String EMPTY_STRING = "";
    public static final String ZERO_STRING = "0";
    public static final String SPA = "spa";
    /*private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,
            Color.MAGENTA, Color.CYAN, Color.rgb(230, 95, 0), Color.RED,
            Color.GRAY, Color.YELLOW };*/
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
    private AdView adView;

    /**
     * Llamada cuando se inicializa la actividad. Crea los gráficos que se
     * mostrarán
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisitcas);
        utilidades = new Utilidades(this);
        // utilidades.cargarAnuncio();
        setupActionBar();
        juegosSQLH = new JuegosSQLHelper(this);

        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-5590574021757982/9422268351");

        LinearLayout layoutBanner = (LinearLayout) findViewById(R.id.layout_banner_Ads);
        layoutBanner.addView(adView);

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device.
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// Emulador
                .addTestDevice("358240057325789").build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);


        // Juegos agrupados por plataforma
        rendererJuegosPlataforma.setApplyBackgroundColor(true);
        rendererJuegosPlataforma.setChartTitleTextSize(24);
        rendererJuegosPlataforma.setLabelsTextSize(24);
        rendererJuegosPlataforma.setLegendTextSize(24);
        rendererJuegosPlataforma.setLabelsColor(Color.BLACK);
        rendererJuegosPlataforma.setMargins(new int[]{20, 30, 15, 0});
        rendererJuegosPlataforma.setStartAngle(270);
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
            resumenSum.setText(String.valueOf(valorColeccion));
            BigDecimal bd = new BigDecimal(Float.toString(valorColeccion / juegosConPrecio));
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            resumenAVG.setText(String.valueOf(bd.floatValue()));

        } else {
            resumenSum.setText("");
            resumenAVG.setText("");
        }


        Cursor cursorJuegosPlataforma = juegosSQLH.getJuegosPorPlataforma();
        if (cursorJuegosPlataforma != null
                && cursorJuegosPlataforma.moveToFirst()) {

            // textTotal.setText("Total del juegos: "+total);

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
                    // TODO
                    return;
                }
                //serieJuegosPlataforma.add(etiquetas[j] + "(" + x + ")", x);
                serieJuegosPlataforma.add(etiquetas[j], x);
                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
                int color = COLORS[(serieJuegosPlataforma.getItemCount() - 1)
                        % COLORS.length];
                renderer.setColor(color);

                //TODO Añadimos a la falsa leyenda una entrada

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


                renderer.setDisplayChartValues(true);
                rendererJuegosPlataforma.addSeriesRenderer(renderer);
                rendererJuegosPlataforma.setShowLegend(false);
                rendererJuegosPlataforma.setPanEnabled(false);
                rendererJuegosPlataforma.setZoomEnabled(false);
                rendererJuegosPlataforma.setChartTitleTextSize(36);

                rendererJuegosPlataforma.setScale(1f);
                rendererJuegosPlataforma.setStartAngle(270);


            }

            LinearLayout layout = (LinearLayout) findViewById(R.id.linear_estadisticas_1);


            graficoJuegosPlataforma = ChartFactory.getPieChartView(this,
                    serieJuegosPlataforma, rendererJuegosPlataforma);
            layout.addView(graficoJuegosPlataforma);


            cursorJuegosPlataforma.close();
        }

        String codigoIdioma = Locale.getDefault().getISO3Language();

        Log.v("LOCALE ISO", codigoIdioma);

        // Juegos agrupados por genero
        rendererJuegosGenero.setApplyBackgroundColor(true);
        rendererJuegosGenero.setChartTitleTextSize(24);
        rendererJuegosGenero.setLabelsTextSize(24);
        rendererJuegosGenero.setLegendTextSize(24);
        rendererJuegosGenero.setLabelsColor(Color.BLACK);
        rendererJuegosGenero.setMargins(new int[]{20, 30, 15, 0});
        rendererJuegosGenero.setStartAngle(180);

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
                    // TODO
                    return;
                }
//				serieJuegosGenero.add(etiquetas[j] + " (" + x + ")", x);
                serieJuegosGenero.add(etiquetas[j], x);
                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();


                int color = COLORS[(serieJuegosGenero.getItemCount() - 1)
                        % COLORS.length];
                renderer.setColor(color);

                //TODO Añadimos a la falsa leyenda una entrada

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
                rendererJuegosGenero.setShowLegend(false);
                rendererJuegosGenero.setPanEnabled(false);
                rendererJuegosGenero.setZoomEnabled(false);
                rendererJuegosGenero.setStartAngle(270);
            }

            LinearLayout layout = (LinearLayout) findViewById(R.id.linear_estadisticas_2);
            graficoJuegosGenero = ChartFactory.getPieChartView(this,
                    serieJuegosGenero, rendererJuegosGenero);

            layout.addView(graficoJuegosGenero);

            cursorJuegosGenero.close();
        }

        // Juegos agrupados por formato
        rendererJuegosFormato.setApplyBackgroundColor(true);
        rendererJuegosFormato.setChartTitleTextSize(24);
        rendererJuegosFormato.setLabelsTextSize(24);
        rendererJuegosFormato.setLegendTextSize(24);
        rendererJuegosFormato.setLabelsColor(Color.BLACK);
        rendererJuegosFormato.setMargins(new int[]{20, 30, 15, 0});
        rendererJuegosFormato.setStartAngle(180);
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
                    // TODO
                    return;
                }
                serieJuegosFormato.add(etiquetas[j] + "(" + x + ")", x);
                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
                renderer.setColor(coloresFormato[(serieJuegosFormato.getItemCount() - 1)
                        % coloresFormato.length]);
                rendererJuegosFormato.addSeriesRenderer(renderer);
                rendererJuegosFormato.setShowLegend(false);
                rendererJuegosFormato.setPanEnabled(false);
                rendererJuegosFormato.setZoomEnabled(false);
                rendererJuegosFormato.setStartAngle(270);

            }

            LinearLayout layout = (LinearLayout) findViewById(R.id.linear_estadisticas_4);
            graficoJuegosFormato = ChartFactory.getPieChartView(this,
                    serieJuegosFormato, rendererJuegosFormato);

            layout.addView(graficoJuegosFormato);

            cursorJuegosFormato.close();
        }

        // Juegos agrupados por completado
        rendererJuegosCompletados.setApplyBackgroundColor(true);
        rendererJuegosCompletados.setChartTitleTextSize(24);
        rendererJuegosCompletados.setLabelsTextSize(24);
        rendererJuegosCompletados.setLegendTextSize(24);
        rendererJuegosCompletados.setLabelsColor(Color.BLACK);
        rendererJuegosCompletados.setMargins(new int[]{20, 30, 15, 0});
        rendererJuegosCompletados.setStartAngle(180);
        Cursor cursorJuegosCompletados = juegosSQLH.getJuegosCompletados();
        if (cursorJuegosCompletados != null
                && cursorJuegosCompletados.moveToFirst()) {

            numeroJuegos = new int[cursorJuegosCompletados.getCount()];
            etiquetas = new String[cursorJuegosCompletados.getCount()];
            int i = 0;
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
            for (int j = 0; j < numeroJuegos.length; j++) {
                int x = 0;
                try {
                    x = numeroJuegos[j];
                } catch (NumberFormatException e) {
                    // TODO
                    return;
                }
                serieJuegosCompletados.add(etiquetas[j] + " (" + x + ")", x);
                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();

                if (STATUS_NO_COMPLETADO.equals(etiquetas[j]) || STATUS_PENDING.equals(etiquetas[j])) {
                    renderer.setColor(Color.rgb(255, 69, 0));
                } else {
                    renderer.setColor(Color.rgb(0, 255, 0));
                }
                /*renderer.setColor(COLORS[(serieJuegosCompletados.getItemCount() - 1)
						% COLORS.length]);*/

                rendererJuegosCompletados.addSeriesRenderer(renderer);
                rendererJuegosCompletados.setPanEnabled(false);
                rendererJuegosCompletados.setShowLegend(false);
                rendererJuegosCompletados.setZoomEnabled(false);
                rendererJuegosFormato.setStartAngle(270);
            }

            LinearLayout layout = (LinearLayout) findViewById(R.id.linear_estadisticas_3);
            graficoJuegosCompletados = ChartFactory.getPieChartView(this,
                    serieJuegosCompletados, rendererJuegosCompletados);

            layout.addView(graficoJuegosCompletados);

            cursorJuegosCompletados.close();
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
                Log.i("ActionBar", "Home");
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
