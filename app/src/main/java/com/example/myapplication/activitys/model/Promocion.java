package com.example.myapplication.activitys.model;

public class Promocion {
    private String id;           // 🔹 Este campo es necesario para editar/eliminar
    private String titulo;
    private String descripcion;
    private String fecha;

    public Promocion() {
        // Necesario para Firebase
    }

    public Promocion(String id, String titulo, String descripcion, String fecha) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    // 🔹 Constructor sin ID (por si aún quieres usarlo al crear)
    public Promocion(String titulo, String descripcion, String fecha) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    // 🔹 Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
