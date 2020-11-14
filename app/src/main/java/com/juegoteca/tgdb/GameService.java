package com.juegoteca.tgdb;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
public interface GameService {

    @GET("/v1.1/Games/ByGameName")
    Call< List<Game> > getGames();
}