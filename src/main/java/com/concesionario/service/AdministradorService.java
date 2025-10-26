package com.concesionario.service;

import com.concesionario.model.Administrador;
import com.concesionario.model.Rol;
import com.concesionario.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registrarAdministrador(String nombre, String apellido, String identificacion,
                                       String email, String password) {

        // Verificar si ya existe el correo
        if (administradorRepository.existsByCorreoAdmin(email)) {
            throw new RuntimeException("El correo ya está registrado para un administrador");
        }

        // Verificar si ya existe la identificación
        if (administradorRepository.existsByIdentificacionAdmin(identificacion)) {
            throw new RuntimeException("La identificación ya está registrada para un administrador");
        }

        // Crear y guardar el administrador
        Administrador admin = new Administrador();
        admin.setNombreAdmin(nombre);
        admin.setApellidoAdmin(apellido);
        admin.setIdentificacionAdmin(identificacion);
        admin.setCorreoAdmin(email);
        admin.setPasswordAdmin(passwordEncoder.encode(password));
        admin.setRol(Rol.ADMINISTRADOR);

        administradorRepository.save(admin);
    }
}