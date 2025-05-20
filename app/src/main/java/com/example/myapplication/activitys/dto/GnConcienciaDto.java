package com.example.myapplication.activitys.dto;

public class GnConcienciaDto {

    private int idConciencia;
    private String desConciencia;
    private int idTema;

    public GnConcienciaDto() {
    }

    public GnConcienciaDto(int idConciencia, String desConciencia, int idTema) {
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

}
