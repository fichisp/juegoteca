package com.juegoteca.actividades;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.naranja_cabeceras));
        }

        Handler handler = new Handler();
        Utilidades utilidades = new Utilidades(this);
        handler.postDelayed(getRunnableStartApp(), 1500);

    }

    /**
     * Metodo en el cual se debe incluir dentro de run(){Tu codigo} el codigo que se quiere realizar una
     * vez ha finalizado el tiempo que se desea mostrar la actividad de splash
     *
     * @return
     */
    private Runnable getRunnableStartApp() {
        return new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, Inicio.class);
                startActivity(intent);
                finish();
            }
        };
    }

}
