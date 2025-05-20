package com.example.myapplication.activitys.dto;

public class GnRetoRecuReenDto {

    private int idRetoRecuReen;
    private String asunto;
    private int idTema;
    public GnRetoRecuReenDto() {

    }

    public GnRetoRecuReenDto(int idRetoRecuReen, String proEstadoAnimo, int idTema) {
        this.idRetoRecuReen = idRetoRecuReen;
        this.asunto = asunto;
        this.idTema = idTema;
    }

    public int getIdRetoRecuReen() {
        return idRetoRecuReen;
    }

    public void setIdRetoRecuReen(int idRetoRecuReen) {
        this.idRetoRecuReen = idRetoRecuReen;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public int getIdTema() {
        return idTema;
    }

    public void setIdTema(int idTema) {
        this.idTema = idTema;
    }

}
