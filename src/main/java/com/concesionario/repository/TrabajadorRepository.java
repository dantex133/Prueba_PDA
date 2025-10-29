package com.concesionario.repository;

import com.concesionario.model.Trabajador;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TrabajadorRepository extends MongoRepository<Trabajador, String> {
    Optional<Trabajador> findByCorreo(String correo);
    Optional<Trabajador> findByIdentificacion(String identificacion);
    boolean existsByCorreo(String correo);
    boolean existsByIdentificacion(String identificacion);
    // Buscar por correo


    // O si necesitas buscar por otro campo
    Trabajador findByNombre(String nombre);


}