package com.concesionario.controller;

import com.concesionario.repository.UsuarioRepository;
import com.concesionario.repository.VehiculoRepository;
import com.concesionario.repository.CitaRepository;
import com.concesionario.repository.TrabajadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrabajadorController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @GetMapping("/perfil_analisis")
    public String perfilA(Model model) {

        long totalClientes = usuarioRepository.count();
        long totalVehiculos = vehiculoRepository.count();
        long totalCitas = citaRepository.count();
        long totalTrabajadores = trabajadorRepository.count();


        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalVehiculos", totalVehiculos);
        model.addAttribute("totalCitas", totalCitas);
        model.addAttribute("totalTrabajadores", totalTrabajadores);

        return "Perfil_analisis";
    }

    @GetMapping("/perfil_gestor")
    public String perfilG() {
        return "Perfil_gestor";
    }
}