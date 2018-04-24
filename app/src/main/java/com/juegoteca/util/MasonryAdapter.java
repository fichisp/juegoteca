package com.juegoteca.util;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juegoteca.actividades.DetalleJuego;
import com.juegoteca.actividades.DetalleJuegoImagenGrande;
import com.juegoteca.actividades.NuevoJuego;
import com.juegoteca.basedatos.Juego;
import com.juegoteca.basedatos.JuegosSQLHelper;
import com.mijuegoteca.R;

import java.io.File;

/**
 * Created by Suleiman on 26-07-2015.
 */
public class MasonryAdapter extends RecyclerView.Adapter<MasonryAdapter.MasonryView> {

    private Context context;

    private Juego[] datosJuegos;
    private int[] gridCellColor;
    private Utilidades utilidades;
    private int position = 0;


    public MasonryAdapter(Context context) {
        this.context = context;
        utilidades = new Utilidades(this.context);


        JuegosSQLHelper juegosSQLH = new JuegosSQLHelper(context);
        //Cursor c = juegosSQLH.getUltimosJuegosAnadidos();

        final SharedPreferences settings = context.getSharedPreferences("UserInfo",
                0);

        Cursor c = null;

        if(settings.contains("orden_ultimos_fecha_compra") && settings.getBoolean("orden_ultimos_fecha_compra", true)) {
            c = juegosSQLH.getUltimosJuegosAnadidosFechaCompra();
        } else {
            c = juegosSQLH.getUltimosJuegosAnadidos();
        }


        if (c != null & c.moveToFirst()) {
            datosJuegos = new Juego[c.getCount()];
            int i = 0;
            do {
                datosJuegos[i] = new Juego();
                datosJuegos[i].setId(c.getInt(0));
                datosJuegos[i].setCaratula(c.getString(13));
                datosJuegos[i].setTitulo(c.getString(2));
                i++;
            } while (c.moveToNext());

            c.close();
        } else {
            datosJuegos = new Juego[1];
            datosJuegos[0] = new Juego();
            datosJuegos[0].setId(-1);
            datosJuegos[0].setCaratula("");
        }

        juegosSQLH.close();
    }

    @Override
    public MasonryView onCreateViewHolder(final ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        final MasonryView masonryView = new MasonryView(layoutView);


        layoutView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                position = masonryView.getAdapterPosition();
                detalleJuego(arg0);
            }
        });

        masonryView.setIsRecyclable(false);

        return masonryView;
    }

    @Override
    public void onBindViewHolder(MasonryView holder, int position) {

        if (datosJuegos[position].getCaratula() != null && !"".equals(datosJuegos[position].getCaratula())) {
            holder.imageView.setImageBitmap(utilidades.decodeFile(new File(context.getFilesDir().getPath() + "/" + datosJuegos[position].getCaratula())));
        } else {
            //holder.imageView.setImageDrawable((context.getResources().getDrawable(R.drawable.sinimagen)));
            holder.imageView.setImageBitmap(utilidades.redimensionarImagen(BitmapFactory.decodeResource(context.getResources(), R.drawable.sinimagen), 300));

        }
        holder.textView.setText(datosJuegos[position].getTitulo());
        holder.idView.setText(String.valueOf(datosJuegos[position].getId()));
    }

    @Override
    public int getItemCount() {
        return datosJuegos.length;
    }

    /**
     * Lanza la actividad para ver la ficha de un juego
     *
     * @param view
     */
    public void detalleJuego(View view) {
        TextView id = (TextView) view.findViewById(R.id.id_juego);
        Intent intent;
        final SharedPreferences settings = context.getSharedPreferences("UserInfo",
                0);



        if (Integer.parseInt(String.valueOf(id.getText())) == -1) {
            intent = new Intent(context, NuevoJuego.class);
        } else if (settings.contains("detalle_imagen") && settings.getBoolean("detalle_imagen", true)) {

            intent = new Intent(context, DetalleJuegoImagenGrande.class);
            intent.putExtra("ID_JUEGO", String.valueOf(id.getText()));
            intent.putExtra("NUEVO_JUEGO", false);
            intent.putExtra("GRID", true);
            intent.putExtra("SCROLL_Y",  position);
        } else {
            intent = new Intent(context, DetalleJuego.class);
            intent.putExtra("ID_JUEGO", String.valueOf(id.getText()));
            intent.putExtra("NUEVO_JUEGO", false);
            intent.putExtra("GRID", true);
            intent.putExtra("SCROLL_Y", position);
        }
        context.startActivity(intent);
    }

    class MasonryView extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView idView;


        public MasonryView(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.img);
            textView = (TextView) itemView.findViewById(R.id.img_name);
            idView = (TextView) itemView.findViewById(R.id.id_juego);

        }
    }
}