package com.juegoteca.igdb;

/**
 * Created by alvaro on 3/7/18.
 */

public class IGDBFilter {
    private String titulo; //name
    private String plataforma; //platforms
    private String genero;

    public IGDBFilter(String titulo, String plataforma, String genero) {
        this.titulo = titulo;
        this.plataforma = plataforma;
        this.genero = genero; //genres
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public String getGenero() {
        return genero;
    }
}