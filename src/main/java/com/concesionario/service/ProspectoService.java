package com.concesionario.service;

import com.concesionario.dto.ProspectoDTO;
import com.concesionario.model.Cita;
import com.concesionario.repository.CitaRepository;
import com.concesionario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// En tu CitaService o un nuevo ProspectoService
@Service
public class ProspectoService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<ProspectoDTO> obtenerProspectosParaAsesor(String asesorId) {
        // Obtener todas las citas asignadas a este asesor
        List<Cita> citasAsesor = citaRepository.findByTrabajadorId(asesorId);

        // Agrupar por usuario y obtener la última cita de cada uno
        Map<String, Cita> ultimasCitasPorUsuario = citasAsesor.stream()
                .collect(Collectors.toMap(
                        cita -> cita.getUsuario().getId(),
                        Function.identity(),
                        (cita1, cita2) -> cita1.getFechaCreacion().isAfter(cita2.getFechaCreacion()) ? cita1 : cita2
                ));

        // Convertir a DTOs
        return ultimasCitasPorUsuario.values().stream()
                .map(this::convertirAProspectoDTO)
                .collect(Collectors.toList());
    }

    private ProspectoDTO convertirAProspectoDTO(Cita cita) {
        ProspectoDTO dto = new ProspectoDTO();
        dto.setUsuarioId(cita.getUsuario().getId());

        // ✅ CORREGIR: Usar los campos correctos del UsuarioDTO
        dto.setNombreCompleto(cita.getNombres() + " " + cita.getApellidos());
        dto.setEmail(cita.getCorreoElectronico());
        dto.setTelefono(cita.getTelefono());
        dto.setVehiculoInteres(cita.getNombreVehiculo());
        dto.setEstado(cita.getEstado());
        dto.setUltimoContacto(cita.getFechaCreacion());
        dto.setProximaAccion(cita.getFechaAsignada());
        dto.setCitaId(cita.getId());

        return dto;
    }

    public void cambiarEstadoContactado(String citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setEstado("Contactado");
        citaRepository.save(cita);
    }
}

