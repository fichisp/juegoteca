package com.juegoteca.tgdb;

import com.juegoteca.tgdb.json.TgdbDeveloperResponse;
import com.juegoteca.tgdb.json.TgdbGameByNameResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TgdbService {

    @GET("/v1.1/Games/ByGameName")
    Call<TgdbGameByNameResponse> getGames(@Query("apikey") String apikey, @Query("name") String name, @Query("include") String include, @Query("fields") String fields);

    @GET("v1/Developers")
    Call<TgdbDeveloperResponse> getDevelopers(@Query("apikey") String apikey);
}