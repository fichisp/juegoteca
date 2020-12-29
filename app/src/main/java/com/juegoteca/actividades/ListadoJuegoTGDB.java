package com.juegoteca.actividades;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.juegoteca.basedatos.Juego;
import com.juegoteca.tgdb.TgdbService;
import com.juegoteca.tgdb.json.Game;
import com.juegoteca.tgdb.json.PlatfotmObject;
import com.juegoteca.tgdb.json.TgdbGameByNameResponse;
import com.juegoteca.util.ListadoJuegosArrayAdapter;
import com.mijuegoteca.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListadoJuegoTGDB extends Activity  {

    private ListView listadoJuegos;
    private TextView tituloBusqueda;
    private ProgressDialog dialogoBusqueda;
    private int elementosEncontrados = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_juego_tgdb);
        // Referenciamos los controles
        listadoJuegos = (ListView) findViewById(R.id.listado_juegos);
        tituloBusqueda = (TextView) findViewById(R.id.nombre_busqueda);
        elementosEncontrados = 0;


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.thegamesdb.net")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        TgdbService service = retrofit.create(TgdbService.class);

        Call<TgdbGameByNameResponse> call = service.getGames("62ad16e30271f3417f4f4d172fa62a82e94042acd4d983e6d69c126ac139ca17", "Final Fantasy IX", "boxart,platform", "genres.overview");

        Juego[] datosJuegos = new Juego[0];

        dialogoBusqueda = ProgressDialog.show(this, "", getString(R.string.buscando), true);

        Activity  _this = this;

        call.enqueue(new Callback<TgdbGameByNameResponse>() {


            @Override
            public void onResponse(Call<TgdbGameByNameResponse> call, Response<TgdbGameByNameResponse> response) {
                if (response.isSuccessful()) {
                    TgdbGameByNameResponse tgdbResponse = response.body();
                    StringBuilder games = new StringBuilder();

                    Juego[] datosJuegos = new Juego[tgdbResponse.getData().getCount()];
                    int i = 0;
                    for (Game game : tgdbResponse.getData().getGames()) {


                        PlatfotmObject platform = tgdbResponse.getInclude().getPlatform().getData().get(String.valueOf(game.getPlatform()));
                        games.append(game.getGameTitle() + " - " + platform.getName() + "\n");


                        datosJuegos[i] = new Juego();
                        datosJuegos[i].setId(game.getId());
                        datosJuegos[i].setTitulo(game.getGameTitle());
                        datosJuegos[i].setNombrePlataforma(platform.getName());
                        i++;
                    }
                    elementosEncontrados = tgdbResponse.getData().getCount();
                    dialogoBusqueda.dismiss();
                    if (elementosEncontrados != 0) {
                        ListadoJuegosArrayAdapter adapter = new ListadoJuegosArrayAdapter(_this, datosJuegos);
                        adapter.setHideCompany(true);
                        listadoJuegos.setAdapter(adapter);
                        tituloBusqueda.setText(elementosEncontrados + " " + getString(R.string.juegos_encontrados));
                    } else {
                        tituloBusqueda.setText(R.string.sin_resultados_busqueda);
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<TgdbGameByNameResponse> call, Throwable t) {
                dialogoBusqueda.dismiss();
                tituloBusqueda.setText(R.string.sin_resultados_busqueda);
            }

        });


    }
}