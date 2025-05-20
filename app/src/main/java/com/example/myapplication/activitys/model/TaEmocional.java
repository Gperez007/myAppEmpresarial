package com.example.myapplication.activitys.model;

public class TaEmocional {

    private int idEmocional;
    private String emocional;
    private int idTema;

    public TaEmocional() {

    }

    public TaEmocional(int idEmocional, String emocional, int idTema) {
        this.idEmocional = idEmocional;
        this.emocional = emocional;
        this.idTema = idTema;
    }

    public int getIdEmocional() {
        return idEmocional;
    }

    public String getEmocional() {
        return emocional;
    }

    public int getIdTema() {
        return idTema;
    }


    @Override
    public String toString() {
        return "idEmocional:" + getIdEmocional() + ", emocional " + getEmocional() + ", idTema  " + getIdTema();
    }

}
