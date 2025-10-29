package com.concesionario.repository;

import com.concesionario.model.Rol;
import com.concesionario.model.Trabajador;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrabajadorRepository extends MongoRepository<Trabajador, String> {
    Optional<Trabajador> findByCorreo(String correo);
    Optional<Trabajador> findByIdentificacion(String identificacion);
    boolean existsByCorreo(String correo);
    boolean existsByIdentificacion(String identificacion);
    // Buscar por correo





    List<Trabajador> findByRolesContaining(Rol rol);
}