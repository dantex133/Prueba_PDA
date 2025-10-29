package com.concesionario.controller;

import com.concesionario.model.Trabajador;
import com.concesionario.model.Vehiculo;
import com.concesionario.repository.CitaRepository;
import com.concesionario.repository.TrabajadorRepository;
import com.concesionario.repository.UsuarioRepository;
import com.concesionario.repository.VehiculoRepository;
import com.concesionario.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping("/perfil_analisis")
    public String perfilA(Model model, Authentication authentication) {
        long totalClientes = usuarioRepository.count();
        long totalVehiculos = vehiculoRepository.count();
        long totalCitas = citaRepository.count();
        long totalTrabajadores = trabajadorRepository.count();

        String nombreUsuario = "Analista";

        if (authentication != null) {
            String username = authentication.getName();
            Optional<Trabajador> trabajador = trabajadorRepository.findByCorreo(username);
            if (trabajador.isPresent()) {
                nombreUsuario = trabajador.get().getNombre();
            }
        }

        model.addAttribute("nombreUsuario", nombreUsuario);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalVehiculos", totalVehiculos);
        model.addAttribute("totalCitas", totalCitas);
        model.addAttribute("totalTrabajadores", totalTrabajadores);

        return "Perfil_analisis";
    }

    @GetMapping("/perfil_gestor")
    public String perfilG(Model model) {
        // Obtener datos para el dashboard del gestor
        List<Vehiculo> vehiculos = vehiculoService.obtenerVehiculosNormales();
        List<Vehiculo> anuncios = vehiculoService.obtenerDestacados();
        long totalVehiculos = vehiculoRepository.count();
        long totalAnuncios = anuncios.size();



        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("anuncios", anuncios);
        model.addAttribute("totalVehiculos", totalVehiculos);
        model.addAttribute("totalAnuncios", totalAnuncios);


        return "Perfil_gestor";
    }

    // ==================== MÉTODOS PARA VEHÍCULOS ====================

    @PostMapping("/gestor/guardar-vehiculo")
    public String guardarVehiculoNormal(
            @RequestParam String marca,
            @RequestParam String modelo,
            @RequestParam int año,
            @RequestParam double precio,
            @RequestParam String categoria,
            @RequestParam String motor,
            @RequestParam String transmision,
            @RequestParam String combustible,
            @RequestParam int pasajeros,
            @RequestParam String descripcion,
            @RequestParam String colores,
            @RequestParam MultipartFile imagen,
            RedirectAttributes redirectAttributes) throws IOException {

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setAño(año);
        vehiculo.setPrecio(precio);
        vehiculo.setCategoria(categoria);
        vehiculo.setMotor(motor);
        vehiculo.setTransmision(transmision);
        vehiculo.setCombustible(combustible);
        vehiculo.setPasajeros(pasajeros);
        vehiculo.setDescripcion(descripcion);
        vehiculo.setDestacado(false);

        // Procesar los colores
        if (colores != null && !colores.isEmpty()) {
            List<String> listaColores = Arrays.stream(colores.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
            vehiculo.setColores(listaColores);
        }

        vehiculoService.crearVehiculoNormal(vehiculo, imagen);
        redirectAttributes.addFlashAttribute("success", "Vehículo guardado exitosamente");
        return "redirect:/perfil_gestor";
    }

    @GetMapping("/gestor/obtener-vehiculo/{id}")
    @ResponseBody
    public Vehiculo obtenerVehiculoParaEdicion(@PathVariable String id) {
        Vehiculo vehiculo = vehiculoService.obtenerPorId(id);
        if (vehiculo.getColores() == null) {
            vehiculo.setColores(new ArrayList<>());
        }
        return vehiculo;
    }

    @PostMapping("/gestor/editar-vehiculo/{id}")
    public String editarVehiculo(
            @PathVariable String id,
            @ModelAttribute Vehiculo vehiculo,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @RequestParam String motor,
            @RequestParam String transmision,
            @RequestParam String combustible,
            @RequestParam Integer pasajeros,
            @RequestParam String colores,
            @RequestParam String descripcion,
            RedirectAttributes redirectAttributes) {

        try {
            Vehiculo vehiculoExistente = vehiculoService.obtenerPorId(id);

            // Actualizar imagen si se proporciona
            if (imagen != null && !imagen.isEmpty()) {
                vehiculoService.actualizarImagenVehiculo(vehiculoExistente, imagen);
            }

            // Actualizar campos básicos
            vehiculoExistente.setMarca(vehiculo.getMarca());
            vehiculoExistente.setModelo(vehiculo.getModelo());
            vehiculoExistente.setAño(vehiculo.getAño());
            vehiculoExistente.setPrecio(vehiculo.getPrecio());
            vehiculoExistente.setCategoria(vehiculo.getCategoria());
            vehiculoExistente.setMotor(motor);
            vehiculoExistente.setTransmision(transmision);
            vehiculoExistente.setCombustible(combustible);
            vehiculoExistente.setPasajeros(pasajeros);
            vehiculoExistente.setDescripcion(descripcion);

            // Procesar colores
            if (colores != null && !colores.isEmpty()) {
                List<String> listaColores = Arrays.stream(colores.split(","))
                        .map(String::trim)
                        .filter(color -> !color.isEmpty())
                        .collect(Collectors.toList());
                vehiculoExistente.setColores(listaColores);
            } else {
                vehiculoExistente.setColores(new ArrayList<>());
            }

            vehiculoService.guardarVehiculo(vehiculoExistente);
            redirectAttributes.addFlashAttribute("success", "Vehículo actualizado exitosamente");
            return "redirect:/perfil_gestor";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al editar el vehículo: " + e.getMessage());
            return "redirect:/perfil_gestor";
        }
    }

    @GetMapping("/gestor/eliminar-vehiculo/{id}")
    public String eliminarVehiculo(@PathVariable String id, RedirectAttributes redirectAttributes) {
        vehiculoService.eliminarVehiculo(id);
        redirectAttributes.addFlashAttribute("success", "Vehículo eliminado exitosamente");
        return "redirect:/perfil_gestor";
    }

    // ==================== MÉTODOS PARA ANUNCIOS ====================

    @PostMapping("/gestor/guardar-anuncio")
    public String guardarAnuncio(
            @RequestParam String marca,
            @RequestParam String modelo,
            @RequestParam int año,
            @RequestParam double precio,
            @RequestParam String categoria,
            @RequestParam String motor,
            @RequestParam String transmision,
            @RequestParam String combustible,
            @RequestParam int pasajeros,
            @RequestParam String descripcion,
            @RequestParam String colores,
            @RequestParam MultipartFile imagen,
            RedirectAttributes redirectAttributes) throws IOException {

        try {
            Vehiculo anuncio = new Vehiculo();
            anuncio.setMarca(marca);
            anuncio.setModelo(modelo);
            anuncio.setAño(año);
            anuncio.setPrecio(precio);
            anuncio.setCategoria(categoria);
            anuncio.setMotor(motor);
            anuncio.setTransmision(transmision);
            anuncio.setCombustible(combustible);
            anuncio.setPasajeros(pasajeros);
            anuncio.setDescripcion(descripcion);
            anuncio.setDestacado(true); // Es un anuncio

            // Procesar colores
            if (colores != null && !colores.isEmpty()) {
                List<String> listaColores = Arrays.stream(colores.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
                anuncio.setColores(listaColores);
            }

            vehiculoService.crearAnuncio(anuncio, imagen);
            redirectAttributes.addFlashAttribute("success", "Anuncio creado exitosamente");
            return "redirect:/perfil_gestor";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el anuncio: " + e.getMessage());
            return "redirect:/perfil_gestor";
        }
    }

    @GetMapping("/gestor/eliminar-anuncio/{id}")
    public String eliminarAnuncio(@PathVariable String id, RedirectAttributes redirectAttributes) {
        vehiculoService.eliminarVehiculo(id);
        redirectAttributes.addFlashAttribute("success", "Anuncio eliminado exitosamente");
        return "redirect:/perfil_gestor";
    }
}