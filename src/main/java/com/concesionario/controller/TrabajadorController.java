package com.concesionario.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class TrabajadorController {

    @GetMapping("/perfil_gestor")
    public String perfilG() {
        return "Perfil_gestor"; // Sin carpeta
    }

    @GetMapping("/perfil_analisis")
    public String perfilA() {
        return "Perfil_analisis"; // Sin carpeta
    }
}
