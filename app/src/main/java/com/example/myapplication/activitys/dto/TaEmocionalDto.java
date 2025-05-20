package com.example.myapplication.activitys.dto;

public class TaEmocionalDto {

    private int idEmocional;
    private String emocional;
    private int idTema;

    public TaEmocionalDto() {

    }

    public TaEmocionalDto(int idEmocional, String emocional, int idTema) {
        this.idEmocional = idEmocional;
        this.emocional = emocional;
        this.idTema = idTema;
    }

    public int getIdEmocional() {
        return idEmocional;
    }

    public void setIdEmocional(int idEmocional) {
        this.idEmocional = idEmocional;
    }

    public String getEmocional() {
        return emocional;
    }

    public void setEmocional(String emocional) {
        this.emocional = emocional;
    }

    public int getIdTema() {
        return idTema;
    }

    public void setIdTema(int idTema) {
        this.idTema = idTema;
    }
}
