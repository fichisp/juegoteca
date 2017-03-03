package com.juegoteca.actividades;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.juegoteca.basedatos.JuegosSQLHelper;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

import java.io.InputStream;

public class Plataforma extends Activity {

	private String idPlataforma = "";
	private JuegosSQLHelper juegosSQLH;
	private TextView nombrePlataforma, fabricantePlataforma, fechaLanzamientoPlataforma, resumenPlataforma;
	private ImageView imagenPlataforma;
	private Utilidades utilidades;
	/**
	 * Llamada cuando se inicializa la actividad.
	 * Carga la información de la plataforma en los componentes
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plataforma);
		Intent intent = getIntent();
		utilidades = new Utilidades(this);
		//utilidades.cargarAnuncio();
		idPlataforma= intent.getStringExtra("ID_PLATAFORMA");
		imagenPlataforma=(ImageView)findViewById(R.id.imagePlataforma);
		nombrePlataforma = (TextView)findViewById(R.id.textViewNombrePlataforma);
		fabricantePlataforma = (TextView)findViewById(R.id.textViewFabricantePlataforma);
		fechaLanzamientoPlataforma = (TextView)findViewById(R.id.textViewFechaLanzamientoPlataforma);
		resumenPlataforma = (TextView)findViewById(R.id.textViewResumen);
		juegosSQLH = new JuegosSQLHelper(this);
		Cursor c = juegosSQLH.buscarPlataformaID(idPlataforma);
		if(c!=null && c.moveToFirst()){
			Log.v("Cursor plataforma",""+c.getColumnCount());
			nombrePlataforma.setText(c.getString(1));
			this.setTitle(c.getString(1));
			fabricantePlataforma.setText(c.getString(2));
			fechaLanzamientoPlataforma.setText(c.getString(3));
			resumenPlataforma.setText(c.getString(4).replace("\\r\\n", "\n"));
			try{
				// get input stream
				InputStream ims = getAssets().open(c.getString(6));
				// load image as Drawable
				Drawable d = Drawable.createFromStream(ims, null);
				// set image to ImageView
				imagenPlataforma.setImageDrawable(d);
			} catch (Exception e){}
			//			imagenPlataforma.setImageURI(Uri.parse(this.getFilesDir().getPath()+"/"+c.getString(6)));
			c.close();
		}
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.plataforma, menu);
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
			finish();
			return true;
		case android.R.id.home:
			onBackPressed();
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
