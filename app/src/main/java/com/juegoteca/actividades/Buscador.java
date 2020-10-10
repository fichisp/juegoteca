package com.juegoteca.actividades;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;

import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

/**
 * Buscador de juegos
 */
public class Buscador extends Activity {

    private static final String INTENT_PARAMETER_GRID = "GRID";
    private static final String INTENT_PARAMETER_VALORES = "VALORES";
    private static final String INTENT_PARAMETER_SCAN_MODE = "SCAN_MODE";
    private static final String INTENT_PARAMETER_SCAN_RESULT = "SCAN_RESULT";
    private final String[] valoresCampos = new String[5];// 5 campos de búsqueda
    private EditText editEan, editTitulo;
    private Spinner spinnerGenero, spinnerPlataforma, spinnerFormato;
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

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Utilidades utilidades = new Utilidades(this);
        utilidades.cargarGenerosBuscador(spinnerGenero);
        utilidades.cargarPlataformasBuscador(spinnerPlataforma);

        // Inicializar el combo de formatos
        utilidades.cargarFormatosBuscador(spinnerFormato);
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        getActionBar().setDisplayHomeAsUpEnabled(true); // In `OnCreate();`
        //Acciones por defecto al pulsar enter
        editEan.setOnKeyListener(buscar);
        editTitulo.setOnKeyListener(buscar);
    }

    @Override
    public boolean onNavigateUp(){
        finish();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.buscador, menu);
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

    /**
     * Captura un código de barras con la librería Zxing
     *
     * @param view
     */
    public void capturarCodigo(View view) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra(INTENT_PARAMETER_SCAN_MODE, "PRODUCT_MODE");
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
//            valoresCampos[2] = String.valueOf(spinnerPlataforma
//                    .getSelectedItemPosition());

            com.juegoteca.basedatos.Plataforma p = (com.juegoteca.basedatos.Plataforma)spinnerPlataforma.getSelectedItem();
            valoresCampos[2] = String.valueOf(p.getId());
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

        // Iniciar la actividad listado
        final Intent intent = new Intent(this, ListadoJuego.class);
        intent.putExtra(INTENT_PARAMETER_VALORES, valoresCampos);

        if (getIntent().getBooleanExtra(INTENT_PARAMETER_GRID, false)) {
            intent.putExtra(INTENT_PARAMETER_GRID, true);
        }

        startActivity(intent);

    }

    /**
     * Procesar el escaneo de la cámara para obtener el código leído
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra(INTENT_PARAMETER_SCAN_RESULT);
                ((EditText) this.findViewById(R.id.edit_ean_buscador))
                        .setText(contents);
                // Handle successful scan
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra(INTENT_PARAMETER_GRID, false)) {
            Intent intent = new Intent(this, InicioMasonry.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, Inicio.class);
            startActivity(intent);
            finish();
        }
    }
}
