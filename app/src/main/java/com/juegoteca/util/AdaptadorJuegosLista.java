package com.juegoteca.util;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juegoteca.basedatos.Juego;
import com.mijuegoteca.R;

import java.io.File;

public class AdaptadorJuegosLista extends ArrayAdapter<Juego> {

    private Activity context;
    private Juego[] juegos;
    private Utilidades utilidades;

    /**
     * Contructor parametrizado
     *
     * @param context
     * @param juegos
     * @param esJuegoOnline
     */
    public AdaptadorJuegosLista(Activity context, Juego[] juegos, boolean esJuegoOnline) {
        super(context, R.layout.listado_juego, juegos);
        this.context = context;
        this.juegos = juegos;
        utilidades = new Utilidades(context);
    }

    /**
     * Actualiza los datos del adaptador
     *
     * @param juegos
     */
    public void actualizar(Juego[] juegos) {
        this.juegos = juegos;
        this.notifyDataSetChanged();
    }

    /**
     * Carha los datos en los compomentes
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.listado_juego_sin_caratula, null);

        TextView id = (TextView) item.findViewById(R.id.id_juego);
        id.setText(String.valueOf(juegos[position].getId()));
        id.setVisibility(View.GONE);
        id.setWidth(0);

        TextView nombre = (TextView) item.findViewById(R.id.nombre_juego);
        nombre.setText(juegos[position].getTitulo());

        TextView compania = (TextView) item.findViewById(R.id.compania_juego);
        compania.setText(juegos[position].getCompania());

        TextView plataforma = (TextView) item.findViewById(R.id.plataforma_juego);
        plataforma.setText(juegos[position].getNombrePlataforma());

        ImageView caratula = (ImageView) item.findViewById(R.id.image_caratula_listado_caratula);

        if ((juegos[position].getCaratula()).length() == 0) {
            if (juegos[position].getId() == -1) {
                caratula.setImageDrawable((context.getResources().getDrawable(R.drawable.cargar_nuevo_juego)));
            } else {
                caratula.setImageDrawable((context.getResources().getDrawable(R.drawable.sinimagen)));
            }
        } else {
            try {
                caratula.setImageBitmap(utilidades.decodeFile(new File(context.getFilesDir().getPath() + "/" + juegos[position].getCaratula())));

                //				caratula.setImageURI(Uri.parse(context.getFilesDir().getPath()+"/"+juegos[position].getCaratula()));
            } catch (Exception e) {
                Log.e("CARGAR CARATULA LISTADO", e.getMessage());
            }
        }

        return item;
    }

    @Override
    public int getCount() {
        return juegos.length;
    }
}
