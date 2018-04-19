package com.juegoteca.actividades;

import android.os.Bundle;

import com.mijuegoteca.R;

/**
 * Detalle de juego con nuevo layout.
 * Created by alvaro on 28/02/18.
 */

public class DetalleJuegoImagenGrande extends DetalleJuego {
    /**
     * Llamada cuando se inicializa la actividad. Se inicializan los componentes
     * y se carga la información del juego que visualizará
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_juego_imagen_grande);
        super.loadData();

    }
}
