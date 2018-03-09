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

public class AdaptadorJuegosListaCaratula extends ArrayAdapter<Juego> {

    private Activity context;
    private Juego[] juegos;
    private Utilidades utilidades;

    /**
     * @param context
     * @param juegos
     */
    public AdaptadorJuegosListaCaratula(Activity context, Juego[] juegos) {
        super(context, R.layout.listado_juego, juegos);
        this.context = context;
        this.juegos = juegos;
        utilidades = new Utilidades(context);

    }

    /**
     *
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.listado_juego_caratula, parent, false);

        TextView id = (TextView) item.findViewById(R.id.id_juego);

        id.setText(String.valueOf(juegos[position].getId()));

        ImageView caratula = (ImageView) item.findViewById(R.id.image_caratula_listado_caratula);


        utilidades.redimensionarElemento(caratula);

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
                //TODO
                Log.v("CARGAR CARATULA LISTADO", e.getMessage());
            }
        }

        return (item);
    }
}
