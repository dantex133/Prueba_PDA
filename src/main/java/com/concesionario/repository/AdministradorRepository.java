package com.concesionario.repository;

import com.concesionario.model.Administrador;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdministradorRepository extends MongoRepository<Administrador, String> {
    boolean existsByCorreoAdmin(String correoAdmin);
    boolean existsByIdentificacionAdmin(String identificacionAdmin);
    Optional<Administrador> findByCorreoAdmin(String correoAdmin);
}