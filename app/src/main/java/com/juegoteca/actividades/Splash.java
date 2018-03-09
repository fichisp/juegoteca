package com.juegoteca.actividades;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

public class Splash extends Activity {

    private Utilidades utilidades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        utilidades = new Utilidades(this);
        //utilidades.cargarAnuncio();
        handler.postDelayed(getRunnableStartApp(), 1500);
    }

    /**
     * Metodo en el cual se debe incluir dentro de run(){Tu codigo} el codigo que se quiere realizar una
     * vez ha finalizado el tiempo que se desea mostrar la actividad de splash
     *
     * @return
     */
    private Runnable getRunnableStartApp() {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, Inicio.class);
                startActivity(intent);
                finish();
            }
        };
        return runnable;
    }

}
