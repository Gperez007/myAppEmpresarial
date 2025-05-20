package com.example.myapplication.activitys.dto;

public class TrabajoDto {

    private int idTrabajo;
    private String asuntoTrabajo;
    private int idTema;
    private String descripcionTrabajo;

    public TrabajoDto() {
    }

    public TrabajoDto(int idTrabajo, String asuntoTrabajo, int idTema, String descripcionTrabajo) {
        this.idTrabajo = idTrabajo;
        this.asuntoTrabajo = asuntoTrabajo;
        this.idTema = idTema;
        this.descripcionTrabajo = descripcionTrabajo;
    }

    public int getIdTrabajo() {
        return idTrabajo;
    }

    public void setIdTrabajo(int idTrabajo) {
        this.idTrabajo = idTrabajo;
    }

    public String getAsuntoTrabajo() {
        return asuntoTrabajo;
    }

    public void setAsuntoTrabajo(String asuntoTrabajo) {
        this.asuntoTrabajo = asuntoTrabajo;
    }

    public int getIdTema() {
        return idTema;
    }

    public void setIdTema(int idTema) {
        this.idTema = idTema;
    }

    public String getDescripcionTrabajo() {
        return descripcionTrabajo;
    }

    public void setDescripcionTrabajo(String descripcionTrabajo) {
        this.descripcionTrabajo = descripcionTrabajo;
    }
}
