package com.concesionario.dto;

import java.time.LocalDateTime;

public class ProspectoDTO {
    private String usuarioId;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String vehiculoInteres;
    private String estado;
    private LocalDateTime ultimoContacto;
    private LocalDateTime proximaAccion;
    private String citaId;

    public ProspectoDTO() {
    }

    public ProspectoDTO(String usuarioId, String nombreCompleto, String email, String telefono, String vehiculoInteres, String estado, LocalDateTime ultimoContacto, LocalDateTime proximaAccion, String citaId) {
        this.usuarioId = usuarioId;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.telefono = telefono;
        this.vehiculoInteres = vehiculoInteres;
        this.estado = estado;
        this.ultimoContacto = ultimoContacto;
        this.proximaAccion = proximaAccion;
        this.citaId = citaId;
    }


    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getVehiculoInteres() {
        return vehiculoInteres;
    }

    public void setVehiculoInteres(String vehiculoInteres) {
        this.vehiculoInteres = vehiculoInteres;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getUltimoContacto() {
        return ultimoContacto;
    }

    public void setUltimoContacto(LocalDateTime ultimoContacto) {
        this.ultimoContacto = ultimoContacto;
    }

    public LocalDateTime getProximaAccion() {
        return proximaAccion;
    }

    public void setProximaAccion(LocalDateTime proximaAccion) {
        this.proximaAccion = proximaAccion;
    }

    public String getCitaId() {
        return citaId;
    }

    public void setCitaId(String citaId) {
        this.citaId = citaId;
    }
}