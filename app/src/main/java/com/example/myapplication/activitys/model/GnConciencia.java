package com.example.myapplication.activitys.model;

public class GnConciencia {

    private int idConciencia;
    private String desConciencia;
    private int idTema;

    public GnConciencia() {
    }

    public GnConciencia(int idConciencia, String desConciencia, int idTema) {
        this.idConciencia = idConciencia;
        this.desConciencia = desConciencia;
        this.idTema = idTema;
    }

    public int getIdConciencia() {
        return idConciencia;
    }

    public void setIdConciencia(int idConciencia) {
        this.idConciencia = idConciencia;
    }

    public String getDesConciencia() {
        return desConciencia;
    }

    public void setDesConciencia(String desConciencia) {
        this.desConciencia = desConciencia;
    }

    public int getIdTema() {
        return idTema;
    }

    public void setIdTema(int idTema) {
        this.idTema = idTema;
    }

    @Override
    public String toString() {
        return "idConciencia:" + getIdConciencia() + ", desConciencia " + getDesConciencia() + ", idTema  " + getIdTema();
    }

}
