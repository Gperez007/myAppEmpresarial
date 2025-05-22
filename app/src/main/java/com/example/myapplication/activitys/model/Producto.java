package com.example.myapplication.activitys.model;

public class Producto {
    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    //private String urlImagen;

    public Producto() {
        // Constructor vacío requerido por Firestore
    }

    public Producto(String nombre, String descripcion, double precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        //this.urlImagen = urlImagen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

//    public String getUrlImagen() {
//        return urlImagen;
//    }
//
//    public void setUrlImagen(String urlImagen) {
//        this.urlImagen = urlImagen;
//    }
}
