package com.juegoteca.basedatos;

public class Plataforma {

    private Integer id;
    private String nombre;

    public Plataforma(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public String toString()  {
        return nombre;
    }
}
