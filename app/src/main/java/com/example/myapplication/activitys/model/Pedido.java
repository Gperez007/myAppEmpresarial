package com.example.myapplication.activitys.model;

import java.io.Serializable;

public class Pedido implements Serializable {
    private String pedidoId;
    private String clienteId;
    private String clienteNombre;
    private String empresaId;
    private String direccionEntrega;
    private String estado; // "pendiente", "en camino", "entregado"
    private double total;

    // 🔹 Nuevos campos
    private Double latitud;   // <- ahora Double
    private Double longitud;  // <- ahora Double

    // Constructor vacío (requerido por Firestore y serialización)
    public Pedido() {}

    // Constructor con parámetros
    public Pedido(String pedidoId, String clienteId, String clienteNombre,
                  String empresaId, String direccionEntrega,
                  String estado, double total,
                  double latitud, double longitud) {
        this.pedidoId = pedidoId;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.empresaId = empresaId;
        this.direccionEntrega = direccionEntrega;
        this.estado = estado;
        this.total = total;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Getters y Setters
    public String getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(String pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // 🔹 Nuevos getters y setters
    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
