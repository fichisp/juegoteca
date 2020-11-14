package com.juegoteca.tgdb;

import android.database.Cursor;
import android.widget.ArrayAdapter;

import com.juegoteca.basedatos.Plataforma;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

//    private void getPosts() {
//
//        List<Game> games = new ArrayList<>();
//
//
//        // Step 2: Create and fill an ArrayAdapter with a bunch of "State" objects
//        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, games.toArray());
//        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Step 3: Tell the spinner about our adapter
//        plataforma.setAdapter(spinnerArrayAdapter);
//
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.thegamesdb.net")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        GameService postService = retrofit.create(GameService.class);
//        Call<List<Game>> call = postService.getGames();
//        call.enqueue(new Callback<List<Game>>() {
//            @Override
//            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
//                for(Game post : response.body()) {
//                    //titles.add(post.getTitle());
//                }
//                //arrayAdapter.notifyDataSetChanged();
//            }
//            @Override
//            public void onFailure(Call<List<Game>> call, Throwable t) {
//            }
//        });
//    }
}
