package com.example.myapplication.activitys.model;

public class GnImpresion {

    private int idImpresion;
    private String asuntoImpresion;
    private int idTema;

    public GnImpresion() {
    }

    public GnImpresion(int idImpresion, String asuntoImpresion, int idTema) {
        this.idImpresion = idImpresion;
        this.asuntoImpresion = asuntoImpresion;
        this.idTema = idTema;
    }

    public int getIdImpresion() {
        return idImpresion;
    }

    public void setIdImpresion(int idImpresion) {
        this.idImpresion = idImpresion;
    }

    public String getAsuntoImpresion() {
        return asuntoImpresion;
    }

    public void setAsuntoImpresion(String asuntoImpresion) {
        this.asuntoImpresion = asuntoImpresion;
    }

    public int getIdTema() {
        return idTema;
    }

    public void setIdTema(int idTema) {
        this.idTema = idTema;
    }

        @Override
        public String toString() {
            return "idAutoestima:" + getIdImpresion() + ", proAutoestima " + getAsuntoImpresion() + ", idTema  " + getIdTema();
        }

}
