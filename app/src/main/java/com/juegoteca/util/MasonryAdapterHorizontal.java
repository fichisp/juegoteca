package com.juegoteca.util;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.mijuegoteca.R;

import java.io.File;

/**
 * Created by Suleiman on 26-07-2015.
 */
public class MasonryAdapterHorizontal extends RecyclerView.Adapter<MasonryAdapterHorizontal.MasonryView> {

    private Context context;

    private Juego[] datosJuegos;
    private int[] gridCellColor;

    private Utilidades utilidades;

    private int gridItem;


    public MasonryAdapterHorizontal(Context context) {
        this.context = context;
        utilidades = new Utilidades(this.context);
    }

    @Override
    public MasonryView onCreateViewHolder(ViewGroup parent, int viewType) {
        gridItem = R.layout.grid_item_horizontal;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(gridItem, parent, false);

        MasonryView masonryView = new MasonryView(layoutView);
        layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                detalleJuego(arg0);
            }
        });
        layoutView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {


                view.findViewById(R.id.img_name).setVisibility(View.VISIBLE);

                view.postDelayed(new Runnable() {
                    public void run() {
                        view.findViewById(R.id.img_name).setVisibility(View.INVISIBLE);
                    }
                }, 1500);

                return true;

            }
        });
        masonryView.setIsRecyclable(false);

        return masonryView;
    }

    @Override
    public void onBindViewHolder(MasonryView holder, int position) {

        if(datosJuegos[position].getCaratula()!=null && !"".equals(datosJuegos[position].getCaratula())) {
            holder.imageView.setImageBitmap(utilidades.decodeFile(new File(context.getFilesDir().getPath() + "/" + datosJuegos[position].getCaratula())));
        } else {
            //holder.imageView.setImageDrawable((context.getResources().getDrawable(R.drawable.sinimagen)));
            holder.imageView.setImageBitmap(utilidades.redimensionarImagen(BitmapFactory.decodeResource(context.getResources(), R.drawable.sinimagen), 300));

        }
        holder.textView.setText(datosJuegos[position].getTitulo());
        holder.idView.setText(String.valueOf(datosJuegos[position].getId()));
        //Ajustar el texto a la imagen
        holder.textView.setWidth(holder.imageView.getWidth());
        holder.textView.setHeight(holder.imageView.getHeight());
    }

    @Override
    public int getItemCount() {
        return datosJuegos.length;
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
            intent.putExtra("GRID", false);
        } else {
            intent = new Intent(context, DetalleJuego.class);
            intent.putExtra("ID_JUEGO", String.valueOf(id.getText()));
            intent.putExtra("NUEVO_JUEGO", false);
            intent.putExtra("GRID", false);
        }
        context.startActivity(intent);
    }

    public void setDatosJuegos(Juego[] datosJuegos) {
        this.datosJuegos = datosJuegos;
    }
}