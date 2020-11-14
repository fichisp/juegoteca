package com.juegoteca.util;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.juegoteca.basedatos.Juego;
import com.mijuegoteca.R;

public class ListadoJuegosArrayAdapter extends ArrayAdapter<Juego> {

    private final Activity context;
    private Juego[] juegos;

    /**
     * Contructor parametrizado
     *  @param context
     * @param juegos
     */
    public ListadoJuegosArrayAdapter(Activity context, Juego[] juegos) {
        super(context, R.layout.listado_juego, juegos);
        this.context = context;
        this.juegos = juegos;
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
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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

        return item;
    }

    @Override
    public int getCount() {
        return juegos.length;
    }
}
