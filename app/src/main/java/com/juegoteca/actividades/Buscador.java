package com.juegoteca.actividades;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Buscador de juegos
 */
public class Buscador extends Activity {

    private static final String INTENT_PARAMETER_GRID = "GRID";
    private static final String INTENT_PARAMETER_VALORES = "VALORES";
    private static final String INTENT_PARAMETER_SCAN_MODE = "SCAN_MODE";
    private static final String INTENT_PARAMETER_SCAN_RESULT = "SCAN_RESULT";
    private final String[] valoresCampos = new String[9];// 5 campos de búsqueda
    private EditText editEan, editTitulo, releaseStart, releaseEnd, buyStart, buyEnd;
    private Spinner spinnerGenero, spinnerPlataforma, spinnerFormato;
    private Utilidades utilidades;

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
        utilidades = new Utilidades(this);
        // Inicializar los controles
        editEan = ((EditText) this.findViewById(R.id.edit_ean_buscador));
        editTitulo = ((EditText) this.findViewById(R.id.edit_titulo_buscador));
        spinnerGenero = (Spinner) findViewById(R.id.spinner_genero_buscador);
        spinnerPlataforma = (Spinner) findViewById(R.id.spinner_plataforma_buscador);
        spinnerFormato = (Spinner) findViewById(R.id.spinner_formato_nuevo);
        releaseStart = ((EditText) this.findViewById(R.id.search_date_start));
        releaseEnd = ((EditText) this.findViewById(R.id.search_date_end));

        buyStart = ((EditText) this.findViewById(R.id.search_date_buy_start));
        buyEnd = ((EditText) this.findViewById(R.id.search_date_buy_end));


        releaseStart.setOnClickListener(new View.OnClickListener() {
            private final Calendar myCalendar = Calendar.getInstance();
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Buscador.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {

                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        releaseStart.setText(sdf.format(myCalendar.getTime()));
                    }
                }, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        releaseEnd.setOnClickListener(new View.OnClickListener() {
            private final Calendar myCalendar = Calendar.getInstance();
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Buscador.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        releaseEnd.setText(sdf.format(myCalendar.getTime()));
                    }
                }, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        buyStart.setOnClickListener(new View.OnClickListener() {
            private final Calendar myCalendar = Calendar.getInstance();
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Buscador.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        buyStart.setText(sdf.format(myCalendar.getTime()));
                    }
                }, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        buyEnd.setOnClickListener(new View.OnClickListener() {
            private final Calendar myCalendar = Calendar.getInstance();
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Buscador.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        buyEnd.setText(sdf.format(myCalendar.getTime()));
                    }
                }, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


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
    public boolean onNavigateUp() {
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
            com.juegoteca.basedatos.Plataforma p = (com.juegoteca.basedatos.Plataforma) spinnerPlataforma.getSelectedItem();
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
//Relase date

        //start
        String date = releaseStart.getText().toString();
        if (date.length() == 0) {
            valoresCampos[5] = "0";//min timesptamp
        } else {
            if (utilidades.validaFecha(date) != 0) {
                Toast toast = Toast
                        .makeText(
                                getApplicationContext(),
                                getString(R.string.campo_fecha_lanzamiento_invalido),
                                Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                valoresCampos[5] = utilidades
                        .convertirFechaAMilisegundos(date);
            }
        }


        date = releaseEnd.getText().toString();
        if (date.length() == 0) {
            valoresCampos[6] = "9999999999";//max
        } else {
            if (utilidades.validaFecha(date) != 0) {
                Toast toast = Toast
                        .makeText(
                                getApplicationContext(),
                                getString(R.string.campo_fecha_lanzamiento_invalido),
                                Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                valoresCampos[6] = utilidades
                        .convertirFechaAMilisegundos(date);
            }
        }


        //buy date
        date = buyStart.getText().toString();
        if (date.length() == 0) {
            valoresCampos[7] = "0";
        } else {
            if (utilidades.validaFecha(date) != 0) {
                Toast toast = Toast
                        .makeText(
                                getApplicationContext(),
                                getString(R.string.campo_fecha_compra_invalido),
                                Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                valoresCampos[7] = utilidades
                        .convertirFechaAMilisegundos(date);
            }
        }

        date = buyEnd.getText().toString();
        if (date.length() == 0) {
            valoresCampos[8] = "9999999999";
        } else {
            if (utilidades.validaFecha(date) != 0) {
                Toast toast = Toast
                        .makeText(
                                getApplicationContext(),
                                getString(R.string.campo_fecha_compra_invalido),
                                Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                valoresCampos[8] = utilidades
                        .convertirFechaAMilisegundos(date);
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
