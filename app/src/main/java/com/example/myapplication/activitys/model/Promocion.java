package com.example.myapplication.activitys.model;

public class Promocion {
    private String titulo;
    private String descripcion;
    private String fecha;

    public Promocion() {
        // Necesario para Firebase
    }

    public Promocion(String titulo, String descripcion, String fecha) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }
}
