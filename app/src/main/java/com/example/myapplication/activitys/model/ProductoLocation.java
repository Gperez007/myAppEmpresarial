package com.example.myapplication.activitys.model;

public class ProductoLocation {
    private String nombre;
    private double lat;
    private double lng;

    public ProductoLocation(String nombre, double lat, double lng) {
        this.nombre = nombre;
        this.lat = lat;
        this.lng = lng;
    }

    public String getNombre() { return nombre; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
}
