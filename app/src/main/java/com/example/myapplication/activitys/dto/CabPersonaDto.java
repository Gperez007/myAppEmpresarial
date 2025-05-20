package com.example.myapplication.activitys.dto;

import java.math.BigDecimal;

public class CabPersonaDto {

    private String nombre;
    private String apellidoP;
    private BigDecimal telefono;
    private String fechaNacimiento;
    private String genero;

    private String correo;

    private String password;

    public CabPersonaDto() {

    }

    public CabPersonaDto(String nombre, String apellidoP, BigDecimal telefono, String fechaNacimiento, String genero, String correo, String password) {
        this.nombre = nombre;
        this.apellidoP = apellidoP;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.correo = correo;
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoP() {
        return apellidoP;
    }

    public void setApellidoP(String apellidoP) {
        this.apellidoP = apellidoP;
    }

    public BigDecimal getTelefono() {
        return telefono;
    }

    public void setTelefono(BigDecimal telefono) {
        this.telefono = telefono;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String correo) {
        this.password = correo;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

}
