package com.juegoteca.actividades;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mijuegoteca.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * AcercaDe
 */
public class AcercaDe extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);
        // Show the Up button in the action bar.
        setupActionBar();
        //Cargar el fichero con las licencias
        TextView textoAcercaDe = (TextView) this.findViewById(R.id.texto_acercade);
        InputStream fraw = this.getResources().openRawResource(R.raw.licencias);
        BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));
        StringBuilder texto = new StringBuilder();
        String linea;
        try {
            while ((linea = brin.readLine()) != null) {
                texto.append(linea).append("\n");
            }
            fraw.close();
        } catch (IOException e) {
            texto = new StringBuilder();
        }
        textoAcercaDe.setText(texto.toString());
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.acerca_de, menu);
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

}
