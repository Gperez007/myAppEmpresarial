package com.example.myapplication.activitys.model;

import com.example.myapplication.R;

import java.io.Serializable;

public class Tema implements Serializable {

    private int idTema;
    private String nombreTema;
    private int imagen = R.drawable.the_wolf_of_wall_street;


    public Tema() {

    }

    public Tema(int idTema, String nombreTema, int imagen) {
        this.idTema = idTema;
        this.nombreTema = nombreTema;
        this.imagen = imagen;
    }

    public int getIdTema() {
        return idTema;
    }

    public void setIdProducto(int idTema) {
        this.idTema = idTema;
    }

    public int getImagen() {
        return imagen;
    }

    public int setImagen(int imagen) {
        this.imagen = imagen;
        return imagen;
    }

    public String getNombreTema() {
        return nombreTema;
    }

    public void setNombreTema(String nombreTema) {
        this.nombreTema = nombreTema;
    }

    public void setIdTema(int idTema) {
        this.idTema = idTema;
    }


    @Override
    public String toString() {
        return "idProducto:" + getIdTema() + ", nombreTema " + getNombreTema()+ ", imagen " + getImagen();
    }
}