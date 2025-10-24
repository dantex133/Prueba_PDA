package com.concesionario.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "trabajadores")
public class Trabajador {
    @Id
    private String id;

    private String nombre;
    private String apellido;
    private String identificacion;
    private String correo;
    private String password;

    // ✅ CAMBIO: De rol único a lista de roles
    private List<Rol> roles = new ArrayList<>();

    private LocalTime horaInicioTrabajo;
    private LocalTime horaFinTrabajo;
    private List<String> diasTrabajo; // Ej: ["LUNES", "MARTES", "MIERCOLES"]

    // Constructores
    public Trabajador() {
    }

    // ✅ NUEVO: Constructor con parámetros básicos
    public Trabajador(String nombre, String apellido, String identificacion, String correo, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.identificacion = identificacion;
        this.correo = correo;
        this.password = password;
        this.roles = new ArrayList<>();
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ✅ CAMBIO: Getters y setters para lista de roles
    public List<Rol> getRoles() {
        return roles;
    }

    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }

    public LocalTime getHoraInicioTrabajo() {
        return horaInicioTrabajo;
    }

    public void setHoraInicioTrabajo(LocalTime horaInicioTrabajo) {
        this.horaInicioTrabajo = horaInicioTrabajo;
    }

    public LocalTime getHoraFinTrabajo() {
        return horaFinTrabajo;
    }

    public void setHoraFinTrabajo(LocalTime horaFinTrabajo) {
        this.horaFinTrabajo = horaFinTrabajo;
    }

    public List<String> getDiasTrabajo() {
        return diasTrabajo;
    }

    public void setDiasTrabajo(List<String> diasTrabajo) {
        this.diasTrabajo = diasTrabajo;
    }

    // ✅ NUEVO: Métodos útiles para manejar roles
    public void agregarRol(Rol rol) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        if (!this.roles.contains(rol)) {
            this.roles.add(rol);
        }
    }

    public void removerRol(Rol rol) {
        if (this.roles != null) {
            this.roles.remove(rol);
        }
    }

    public boolean tieneRol(Rol rol) {
        return this.roles != null && this.roles.contains(rol);
    }

    public boolean tieneAlgunRol(Rol... roles) {
        if (this.roles == null) return false;
        for (Rol rol : roles) {
            if (this.roles.contains(rol)) {
                return true;
            }
        }
        return false;
    }

    public void limpiarRoles() {
        if (this.roles != null) {
            this.roles.clear();
        }
    }

    // ✅ NUEVO: Método para obtener nombres de roles como lista de strings
    public List<String> getNombresRoles() {
        List<String> nombres = new ArrayList<>();
        if (this.roles != null) {
            for (Rol rol : this.roles) {
                nombres.add(rol.name());
            }
        }
        return nombres;
    }

    @Override
    public String toString() {
        return "Trabajador{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", identificacion='" + identificacion + '\'' +
                ", correo='" + correo + '\'' +
                ", roles=" + roles +
                ", horaInicioTrabajo=" + horaInicioTrabajo +
                ", horaFinTrabajo=" + horaFinTrabajo +
                ", diasTrabajo=" + diasTrabajo +
                '}';
    }
}