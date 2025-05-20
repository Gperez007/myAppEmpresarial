package com.example.myapplication.activitys.model;

public class Mensaje {
    private String emisor;
    private String contenido;

    public Mensaje(String emisor, String contenido) {
        this.emisor = emisor;
        this.contenido = contenido;
    }

    public String getEmisor() {
        return emisor;
    }

    public String getContenido() {
        return contenido;
    }
}
