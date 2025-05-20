package com.example.myapplication.activitys.model;


import java.io.Serializable;
import java.math.BigDecimal;

public class Trabajo implements Serializable {

    private BigDecimal idTrabajo;
    private String asuntoTrabajo;
    private BigDecimal  idTema;
    private String descripcionTrabajo;

    public Trabajo() {
    }

    public Trabajo(BigDecimal idTrabajo, String asuntoTrabajo, BigDecimal idTema, String descripcionTrabajo) {
        this.idTrabajo = idTrabajo;
        this.asuntoTrabajo = asuntoTrabajo;
        this.idTema = idTema;
        this.descripcionTrabajo = descripcionTrabajo;
    }

    public BigDecimal getIdTrabajo() {
        return idTrabajo;
    }

    public void setIdTrabajo(BigDecimal idTrabajo) {
        this.idTrabajo = idTrabajo;
    }

    public String getAsuntoTrabajo() {
        return asuntoTrabajo;
    }

    public void setAsuntoTrabajo(String asuntoTrabajo) {
        this.asuntoTrabajo = asuntoTrabajo;
    }

    public BigDecimal getIdTema() {
        return idTema;
    }

    public void setIdTema(BigDecimal idTema) {
        this.idTema = idTema;
    }

    public String getDescripcionTrabajo() {
        return descripcionTrabajo;
    }

    public void setDescripcionTrabajo(String descripcionTrabajo) {
        this.descripcionTrabajo = descripcionTrabajo;
    }

    @Override
    public String toString() {
        return "idTrabajo: " + getIdTrabajo() + ", asuntoTrabajo: " + getAsuntoTrabajo() + ", idTema: " + getIdTema() + ", descripcionTrabajo: " + getDescripcionTrabajo();
    }
}
