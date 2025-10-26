package com.concesionario.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "administradores")
public class Administrador {

    @Id
    private String id;

    private String nombreAdmin;
    private String apellidoAdmin;

    @Indexed(unique = true)
    private String identificacionAdmin;

    @Indexed(unique = true)
    private String correoAdmin;

    private String passwordAdmin;
    private Rol rol = Rol.ADMINISTRADOR;


    public Administrador() {}

    public Administrador(String nombreAdmin, String apellidoAdmin, String identificacionAdmin,
                         String correoAdmin, String passwordAdmin) {
        this.nombreAdmin = nombreAdmin;
        this.apellidoAdmin = apellidoAdmin;
        this.identificacionAdmin = identificacionAdmin;
        this.correoAdmin = correoAdmin;
        this.passwordAdmin = passwordAdmin;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreAdmin() { return nombreAdmin; }
    public void setNombreAdmin(String nombreAdmin) { this.nombreAdmin = nombreAdmin; }

    public String getApellidoAdmin() { return apellidoAdmin; }
    public void setApellidoAdmin(String apellidoAdmin) { this.apellidoAdmin = apellidoAdmin; }

    public String getIdentificacionAdmin() { return identificacionAdmin; }
    public void setIdentificacionAdmin(String identificacionAdmin) { this.identificacionAdmin = identificacionAdmin; }

    public String getCorreoAdmin() { return correoAdmin; }
    public void setCorreoAdmin(String correoAdmin) { this.correoAdmin = correoAdmin; }

    public String getPasswordAdmin() { return passwordAdmin; }
    public void setPasswordAdmin(String passwordAdmin) { this.passwordAdmin = passwordAdmin; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}