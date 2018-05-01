package com.juegoteca.actividades;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.juegoteca.util.MasonryAdapter;
import com.juegoteca.util.SpacesItemDecoration;
import com.juegoteca.util.Utilidades;
import com.mijuegoteca.R;

public class InicioMasonry extends Activity {

    RecyclerView mRecyclerView;

    ImageButton backTopButton;

    private Utilidades utilidades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        utilidades = new Utilidades(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_masonry);
        // Show the Up button in the action bar.
        //setupActionBar();

        backTopButton = (ImageButton) findViewById(R.id.boton_top);

        mRecyclerView = (RecyclerView) findViewById(R.id.masonry_grid);


        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(false);
        mRecyclerView.setLayoutManager(layoutManager);

        MasonryAdapter adapter = new MasonryAdapter(this);
        mRecyclerView.setAdapter(adapter);
        SpacesItemDecoration decoration = new SpacesItemDecoration(1);
        mRecyclerView.addItemDecoration(decoration);

        ImageButton layoutBotones = (ImageButton) findViewById(R.id.boton_top);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) backTopButton.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin + layoutBotones.getLayoutParams().height);
        backTopButton.setLayoutParams(params);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] firstVisibleItemPositions = new int[3];
                int pastVisiblesItems = ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPositions(firstVisibleItemPositions)[0];

                if (pastVisiblesItems > 15) {
                    backTopButton.setVisibility(View.VISIBLE);
                } else {
                    backTopButton.setVisibility(View.GONE);
                }
            }

        });


  /*      if(getIntent().getIntExtra("SCROLL_Y",0)>0){
            mRecyclerView.smoothScrollToPosition( getIntent().getIntExtra("SCROLL_Y",0));
        }*/




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.grid, menu);
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

    public void backTop(View view) {
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * Lanza la actividad para crear un juego
     *
     * @param view
     */
    public void nuevoJuego(View view) {
        Intent intent = new Intent(this, NuevoJuego.class);
        intent.putExtra("GRID", true);
        startActivity(intent);
    }

    /**
     * Lanza la actividad para listar los juegos
     *
     * @param view
     */
    public void listarJuegos(View view) {
        Intent intent = new Intent(this, Buscador.class);
        intent.putExtra("GRID", true);
        startActivity(intent);
    }

    /**
     * Lanza la actividad para ver las estadísitcas
     *
     * @param view
     */
    public void estadisticas(View view) {
        Intent intent = new Intent(this, Estadisitcas.class);
        intent.putExtra("GRID", true);
        startActivity(intent);
    }

    /**
     * Lanza la actividad para ver el listado de favoritos
     *
     * @param view
     */
    public void favoritos(View view) {
        Intent intent = new Intent(this, Favoritos.class);
        intent.putExtra("GRID", true);
        startActivity(intent);
    }

    /**
     * Lanza la actividad para ver el listado de pendientes
     *
     * @param view
     */
    public void pendientes(View view) {
        Intent intent = new Intent(this, Pendientes.class);
        intent.putExtra("GRID", true);
        startActivity(intent);
    }

    /**
     * Lanza la actividad para ver las opciones
     *
     * @param view
     */
    public void opciones(View view) {
        Intent intent = new Intent(this, Opciones.class);
        intent.putExtra("GRID", true);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(InicioMasonry.this,
                Inicio.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utilidades.unbindDrawables(findViewById(R.id.linear_layout_grid_juegos));
        System.gc();
    }


}
