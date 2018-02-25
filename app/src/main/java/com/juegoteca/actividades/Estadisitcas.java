package com.juegoteca.actividades;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
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

import java.util.Locale;

public class Estadisitcas extends Activity {

	public static final String TYPE = "type";
	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,
			Color.MAGENTA, Color.CYAN, Color.rgb(230, 95, 0), Color.RED,
			Color.GRAY, Color.YELLOW };
	private CategorySeries serieJuegosPlataforma = new CategorySeries("");
	private CategorySeries serieJuegosCompletados = new CategorySeries("");
	private CategorySeries serieJuegosGenero = new CategorySeries("");
	private CategorySeries serieJuegosFormato = new CategorySeries("");
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
		rendererJuegosPlataforma.setMargins(new int[] { 20, 30, 15, 0 });
		rendererJuegosPlataforma.setStartAngle(270);
		Cursor cursorJuegos = juegosSQLH.getJuegos();
		int total = cursorJuegos.getCount();
		cursorJuegos.close();
		Float valorColeccion = juegosSQLH.getValorColeccion();
		Integer juegosConPrecio = juegosSQLH.getCountJuegosConPrecio();


		TextView resumenTotal = (TextView) findViewById(R.id.text_view_resumen_total);
		TextView resumenSum = (TextView) findViewById(R.id.text_view_resumen_sum);
		TextView resumenAVG = (TextView) findViewById(R.id.text_view_resumen_avg);

		resumenTotal.setText(getString(R.string.total_juegos) + ": "
				+ total);
		if(valorColeccion!=null){
			resumenSum.setText(getString(R.string.valor_total)+ ": " + valorColeccion);
			resumenAVG.setText(getString(R.string.valor_medio)+ ": " + valorColeccion/juegosConPrecio);

		} else {
			resumenSum.setText(getString(R.string.valor_total)+ ": " + "-");
			resumenAVG.setText(getString(R.string.valor_medio)+ ": " + "-");
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
			for (int j = 0; j < numeroJuegos.length; j++) {
				int x = 0;
				try {
					x = numeroJuegos[j];
				} catch (NumberFormatException e) {
					// TODO
					return;
				}
				serieJuegosPlataforma.add(etiquetas[j] + "(" + x + ")", x);
				SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
				renderer.setColor(COLORS[(serieJuegosPlataforma.getItemCount() - 1)
						% COLORS.length]);
				rendererJuegosPlataforma.addSeriesRenderer(renderer);
				rendererJuegosPlataforma.setShowLegend(false);
				rendererJuegosPlataforma.setPanEnabled(false);
				rendererJuegosPlataforma.setZoomEnabled(false);
				/*rendererJuegosPlataforma
						.setChartTitle(getString(R.string.total_juegos) + ": "
								+ total+ " - " + valor);*/
				rendererJuegosPlataforma.setChartTitleTextSize(36);

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
		rendererJuegosGenero.setMargins(new int[] { 20, 30, 15, 0 });
		rendererJuegosGenero.setStartAngle(180);

		Cursor cursorJuegosGenero = null;

		if (!"spa".equalsIgnoreCase(codigoIdioma)) {
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
			for (int j = 0; j < numeroJuegos.length; j++) {
				int x = 0;
				try {
					x = numeroJuegos[j];
				} catch (NumberFormatException e) {
					// TODO
					return;
				}
				serieJuegosGenero.add(etiquetas[j] + "(" + x + ")", x);
				SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
				renderer.setColor(COLORS[(serieJuegosGenero.getItemCount() - 1)
						% COLORS.length]);
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
		rendererJuegosFormato.setMargins(new int[] { 20, 30, 15, 0 });
		rendererJuegosFormato.setStartAngle(180);
		Cursor cursorJuegosFormato = juegosSQLH.getJuegosPorFormato();
		if (cursorJuegosFormato != null && cursorJuegosFormato.moveToFirst()) {

			numeroJuegos = new int[cursorJuegosFormato.getCount()];
			etiquetas = new String[cursorJuegosFormato.getCount()];
			int i = 0;
			do {
				numeroJuegos[i] = cursorJuegosFormato.getInt(0);
				if (!"spa".equalsIgnoreCase(codigoIdioma)) {
					if (cursorJuegosFormato.getString(1).equals("F")) {
						etiquetas[i] = "Retail";
					}
					if (cursorJuegosFormato.getString(1).equals("D")) {
						etiquetas[i] = "Digital";
					}
					if (cursorJuegosFormato.getString(1).equals("")) {
						etiquetas[i] = "N/D";
					}
				} else {
					if (cursorJuegosFormato.getString(1).equals("F")) {
						etiquetas[i] = "Fisico";
					}
					if (cursorJuegosFormato.getString(1).equals("D")) {
						etiquetas[i] = "Digital";
					}
					if (cursorJuegosFormato.getString(1).equals("")) {
						etiquetas[i] = "N/D";
					}
				}

				i++;
			} while (cursorJuegosFormato.moveToNext());
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
				renderer.setColor(COLORS[(serieJuegosFormato.getItemCount() - 1)
						% COLORS.length]);
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
		rendererJuegosCompletados.setMargins(new int[] { 20, 30, 15, 0 });
		rendererJuegosCompletados.setStartAngle(180);
		Cursor cursorJuegosCompletados = juegosSQLH.getJuegosCompletados();
		if (cursorJuegosCompletados != null
				&& cursorJuegosCompletados.moveToFirst()) {

			numeroJuegos = new int[cursorJuegosCompletados.getCount()];
			etiquetas = new String[cursorJuegosCompletados.getCount()];
			int i = 0;
			do {
				numeroJuegos[i] = cursorJuegosCompletados.getInt(0);

				if (!"spa".equalsIgnoreCase(codigoIdioma)) {
					if (cursorJuegosCompletados.getString(1).compareTo("0") == 0) {
						etiquetas[i] = "PENDING";
					} else {
						etiquetas[i] = "COMPLETED";
					}
				} else {
					if (cursorJuegosCompletados.getString(1).compareTo("0") == 0) {
						etiquetas[i] = "NO COMPLETADO";
					} else {
						etiquetas[i] = "COMPLETADO";
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
				serieJuegosCompletados.add(etiquetas[j] + "(" + x + ")", x);
				SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
				renderer.setColor(COLORS[(serieJuegosCompletados.getItemCount() - 1)
						% COLORS.length]);
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
