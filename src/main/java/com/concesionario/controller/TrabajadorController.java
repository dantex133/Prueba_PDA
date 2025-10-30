package com.concesionario.controller;

import com.concesionario.dto.ProspectoDTO;
import com.concesionario.model.Cita;
import com.concesionario.model.Trabajador;
import com.concesionario.model.Vehiculo;
import com.concesionario.repository.CitaRepository;
import com.concesionario.repository.TrabajadorRepository;
import com.concesionario.repository.UsuarioRepository;
import com.concesionario.repository.VehiculoRepository;
import com.concesionario.service.ProspectoService;
import com.concesionario.service.TrabajadorDetailsService;
import com.concesionario.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
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

    @Autowired
    private TrabajadorDetailsService trabajadorDetailsService; // ✅ AÑADIR ESTO

    @Autowired
    private ProspectoService prospectoService;

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

    @GetMapping("/perfil_asesor")
    public String perfilAsesor(Model model, Principal principal) {
        try {
            Trabajador asesor = trabajadorDetailsService.findByCorreo(principal.getName());
            model.addAttribute("asesorId", asesor.getId());
            model.addAttribute("nombreAsesor", asesor.getNombre());

            // ✅ Usar ProspectoService que ya tienes
            long totalProspectos = prospectoService.obtenerProspectosParaAsesor(asesor.getId()).size();
            model.addAttribute("totalProspectos", totalProspectos);

            return "perfil_asesor";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    // ✅ MOVER ESTOS ENDPOINTS A UN CONTROLADOR API SEPARADO O MANTENERLOS AQUÍ PERO CON RUTAS CORRECTAS
    @GetMapping("/asesor/prospectos")
    @ResponseBody
    public List<Map<String, Object>> obtenerProspectos(Principal principal) {
        try {
            Trabajador asesor = trabajadorDetailsService.findByCorreo(principal.getName());

            // ✅ DEBUG MEJORADO: Ver datos del usuario embebido
            List<Cita> citas = citaRepository.findByTrabajadorId(asesor.getId());
            System.out.println("=== DEBUG PROSPECTOS MEJORADO ===");
            System.out.println("Total citas: " + citas.size());
            citas.forEach(cita -> {
                System.out.println("Cita ID: " + cita.getId());
                System.out.println("Usuario embebido: " + (cita.getUsuario() != null ? "Sí" : "No"));
                if (cita.getUsuario() != null) {
                    System.out.println("Nombre usuario: '" + cita.getUsuario().getNombre() + "'");
                    System.out.println("Apellido usuario: '" + cita.getUsuario().getApellido() + "'");
                    System.out.println("Email usuario: '" + cita.getUsuario().getCorreo() + "'");
                }
                System.out.println("Nombres cita: '" + cita.getNombres() + "'");
                System.out.println("Apellidos cita: '" + cita.getApellidos() + "'");
                System.out.println("Teléfono: '" + cita.getTelefono() + "'");
                System.out.println("Vehículo embebido: " + (cita.getVehiculo() != null ?
                        cita.getVehiculo().getMarca() + " " + cita.getVehiculo().getModelo() : "null"));
                System.out.println("---");
            });

            return trabajadorDetailsService.obtenerProspectosParaAsesor(asesor.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener prospectos: " + e.getMessage());
        }
    }

    @PostMapping("/asesor/prospectos/contactar")
    @ResponseBody
    public ResponseEntity<?> marcarComoContactado(@RequestParam String citaId) {
        try {
            prospectoService.cambiarEstadoContactado(citaId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar estado: " + e.getMessage());
        }
    }

    // ✅ AÑADIR ENDPOINT PARA DATOS DEL ASESOR (DASHBOARD)
    @GetMapping("/asesor/datos")
    @ResponseBody
    public ResponseEntity<?> obtenerDatosAsesor(Principal principal) {
        try {
            Trabajador asesor = trabajadorDetailsService.findByCorreo(principal.getName());

            // Obtener prospectos para calcular métricas
            List<ProspectoDTO> prospectos = prospectoService.obtenerProspectosParaAsesor(asesor.getId());

            // Calcular métricas
            long totalProspectos = prospectos.size();
            long ventasMes = prospectos.stream()
                    .filter(p -> "Aprobada".equals(p.getEstado()))
                    .count();
            double tasaConversion = totalProspectos > 0 ?
                    (ventasMes * 100.0) / totalProspectos : 0;
            double comisiones = ventasMes * 850.0; // Ejemplo: $850 por venta

            // Crear respuesta
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("nombre", asesor.getNombre());
            response.put("totalProspectos", totalProspectos);
            response.put("ventasMes", ventasMes);
            response.put("tasaConversion", Math.round(tasaConversion));
            response.put("comisiones", comisiones);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al obtener datos: " + e.getMessage());
        }
    }

    // ✅ AÑADIR ENDPOINT PARA CITAS DEL ASESOR
    @GetMapping("/asesor/citas")
    @ResponseBody
    public List<Map<String, Object>> obtenerCitasAsesor(Principal principal) {
        try {
            Trabajador asesor = trabajadorDetailsService.findByCorreo(principal.getName());
            List<Cita> citas = citaRepository.findByTrabajadorId(asesor.getId());

            return citas.stream().map(cita -> {
                Map<String, Object> citaMap = new HashMap<>();

                // Nombre del cliente
                String nombreCompleto = "";
                if (cita.getUsuario() != null) {
                    nombreCompleto = (cita.getUsuario().getNombre() != null ? cita.getUsuario().getNombre() : "") + " " +
                            (cita.getUsuario().getApellido() != null ? cita.getUsuario().getApellido() : "");
                }
                citaMap.put("id", cita.getId());
                citaMap.put("cliente", nombreCompleto.trim());
                citaMap.put("tipo", cita.getTipo() != null ? cita.getTipo() : "No especificado");

                // Vehículo
                String vehiculo = "No especificado";
                if (cita.getVehiculo() != null && cita.getVehiculo().getModelo() != null) {
                    vehiculo = cita.getVehiculo().getMarca() + " " + cita.getVehiculo().getModelo();
                } else if (cita.getNombreVehiculo() != null && !cita.getNombreVehiculo().isEmpty()) {
                    vehiculo = cita.getNombreVehiculo();
                }
                citaMap.put("vehiculo", vehiculo);

                // Fechas
                citaMap.put("fechaSolicitud", cita.getFechaCreacion() != null ? cita.getFechaCreacion() : "");
                citaMap.put("fechaAsignada", cita.getFechaAsignada() != null ? cita.getFechaAsignada() : "");

                // Otros campos
                citaMap.put("comentario", cita.getComentario() != null ? cita.getComentario() : "Sin comentario");
                citaMap.put("estado", cita.getEstado() != null ? cita.getEstado() : "Pendiente");
                citaMap.put("notasAdmin", cita.getNotasAdmin() != null ? cita.getNotasAdmin() : "Sin notas");

                return citaMap;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener citas: " + e.getMessage());
        }
    }

    @PostMapping("/asesor/citas/{id}/cambiar-estado")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoCita(@PathVariable String id, @RequestParam String estado) {
        try {
            Cita cita = citaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            cita.setEstado(estado);
            citaRepository.save(cita);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/asesor/citas/{id}/asignar-fecha")
    @ResponseBody
    public ResponseEntity<?> asignarFechaCita(@PathVariable String id,
                                              @RequestParam String fecha,
                                              @RequestParam String hora) {
        try {
            Cita cita = citaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            // Combinar fecha y hora
            LocalDateTime fechaHora = LocalDateTime.parse(fecha + "T" + hora);
            cita.setFechaAsignada(fechaHora);
            citaRepository.save(cita);

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/asesor/citas/{id}/guardar-notas")
    @ResponseBody
    public ResponseEntity<?> guardarNotasCita(@PathVariable String id, @RequestParam String notas) {
        try {
            Cita cita = citaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            cita.setNotasAdmin(notas);
            citaRepository.save(cita);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
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