package com.juegoteca.actividades;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

public class Buscador extends Activity {

    private String[] valoresCampos = new String[5];// 5 campos de búsqueda
    private Utilidades utilidades;
    private EditText editEan, editTitulo;
    private Spinner spinnerGenero, spinnerPlataforma, spinnerFormato;
    // private CheckBox checkOnline;
    private AdView adView;

    /**
     * Llamada cuando se inicializa la actividad. En este caso, se cargan en
     * este punto los spinner con los datos correspondientes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscador);

        // Inicializar los controles
        editEan = ((EditText) this.findViewById(R.id.edit_ean_buscador));
        editTitulo = ((EditText) this.findViewById(R.id.edit_titulo_buscador));
        spinnerGenero = (Spinner) findViewById(R.id.spinner_genero_buscador);
        spinnerPlataforma = (Spinner) findViewById(R.id.spinner_plataforma_buscador);
        spinnerFormato = (Spinner) findViewById(R.id.spinner_formato_nuevo);
        // checkOnline =
        // ((CheckBox)this.findViewById(R.id.check_online_buscador));
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        utilidades = new Utilidades(this);

        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-5590574021757982/9422268351");

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_banner_Ads);
        layout.addView(adView);

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device.
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// Emulador
                .addTestDevice("358240057325789").build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
        utilidades.cargarGenerosBuscador(spinnerGenero);
        utilidades.cargarPlataformasBuscador(spinnerPlataforma);
        // Inicializar el combo de formatos
        utilidades.cargarFormatosBuscador(spinnerFormato);
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Acciones por defecto al pulsar enter
        editEan.setOnKeyListener(buscar);
        editTitulo.setOnKeyListener(buscar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.buscador, menu);
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

    /**
     * Captura un código de barras con la librería Zxing
     *
     * @param view
     */
    public void capturarCodigo(View view) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
        startActivityForResult(intent, 0);
    }

    /**
     * Recopila los valores de los campos y llama a a la actividad ListadoJuego
     * para que ejecute la consulta y muestre los resultados
     *
     * @param view
     */
    public void buscarJuego(View view) {
        // Cargar el array con los valores del formulario
        valoresCampos[0] = editEan.getText().toString();
        valoresCampos[1] = editTitulo.getText().toString();
        // Plataforma
        if (((Spinner) findViewById(R.id.spinner_plataforma_buscador))
                .getSelectedItemPosition() == 0) {
            valoresCampos[2] = "%";
        } else {
            valoresCampos[2] = String.valueOf(spinnerPlataforma
                    .getSelectedItemPosition());
        }
        // Genero a buscar
        if (spinnerGenero.getSelectedItemPosition() == 0) {
            valoresCampos[3] = "%";
        } else {
            valoresCampos[3] = String.valueOf(spinnerGenero
                    .getSelectedItemPosition());
        }
        // Formato
        if (spinnerFormato.getSelectedItemPosition() == 1) {
            valoresCampos[4] = "F";
        } else {
            if (spinnerFormato.getSelectedItemPosition() == 2) {
                valoresCampos[4] = "D";
            } else {
                valoresCampos[4] = "%";
            }
        }
        for (int a = 0; a < valoresCampos.length; a++) {
            Log.v("VALORES BUSCAR", a + " --> " + valoresCampos[a]);
        }

        // Iniciar la actividad listado
        final Intent intent = new Intent(this, ListadoJuego.class);
        intent.putExtra("VALORES", valoresCampos);

        // if (checkOnline.isChecked()){
        // intent.putExtra("ONLINE", true);
        // }
        startActivity(intent);

    }

    /**
     * Lanza la busqueda al pulsar enter
     */
    private final View.OnKeyListener buscar = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                buscarJuego(view);
                return true;
            }
            return false;
        }
    };

    /**
     * Procesar el escaneo de la cámara para obtener el código leído
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                ((EditText) this.findViewById(R.id.edit_ean_buscador))
                        .setText(contents);
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.v("DETALLE JUEGO", "Juego nuevo");
        Intent intent = new Intent(this, Inicio.class);
        startActivity(intent);
        finish();
    }
}
