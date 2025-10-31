package com.concesionario.service;

import com.concesionario.model.Usuario;
import com.concesionario.model.Vehiculo;
import com.concesionario.repository.UsuarioRepository;
import com.concesionario.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public class EmailPromocionalService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    private final JavaMailSender mailSender;
    private final UsuarioRepository usuarioRepository;
    private final VehiculoRepository vehiculoRepository;

    public EmailPromocionalService(JavaMailSender mailSender,
                                   UsuarioRepository usuarioRepository,
                                   VehiculoRepository vehiculoRepository) {
        this.mailSender = mailSender;
        this.usuarioRepository = usuarioRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    public void enviarPromocionVehiculo(String vehiculoId) {
        try {
            Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

            List<Usuario> usuarios = usuarioRepository.findAll();

            if (usuarios.isEmpty()) {
                throw new RuntimeException("No hay usuarios registrados");
            }



            int exitosos = 0;
            int fallidos = 0;

            for (Usuario usuario : usuarios) {
                try {
                    enviarCorreoHtmlConImagen(usuario, vehiculo);
                    exitosos++;
                    // Pausa más corta
                    Thread.sleep(50);

                } catch (Exception e) {
                    System.err.println(" Error enviando a: " + usuario.getCorreoUser() + " - " + e.getMessage());
                    e.printStackTrace();
                    fallidos++;
                }
            }

            System.out.println("Envío masivo completado: " + exitosos + " exitosos, " + fallidos + " fallidos");

        } catch (Exception e) {
            throw new RuntimeException("Error en envío masivo: " + e.getMessage());
        }
    }

    private void enviarCorreoHtmlConImagen(Usuario usuario, Vehiculo vehiculo) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setFrom(fromEmail, "NextGen Motors");
        } catch (UnsupportedEncodingException e) {
            helper.setFrom(fromEmail);
        }

        helper.setTo(usuario.getCorreoUser());
        helper.setSubject("🚗 ¡Nueva Oportunidad! " + vehiculo.getMarca() + " " + vehiculo.getModelo());

        String contenidoHtml = crearContenidoHtmlSimple(usuario, vehiculo);
        helper.setText(contenidoHtml, true);

        mailSender.send(message);

    }

    private String crearContenidoHtmlSimple(Usuario usuario, Vehiculo vehiculo) {
        String nombreUsuario = usuario.getNombreUser() != null ? usuario.getNombreUser() : "Cliente";

        String imagenUrl = vehiculo.getImagenUrl() != null ?
                vehiculo.getImagenUrl() :
                "https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80";

        String urlVehiculo = baseUrl + "/vehiculos/explorar/" + vehiculo.getId();

        // HTML MÁS SIMPLE - sin caracteres problemáticos
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background: #f4f4f4; }" +
                ".container { max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; }" +
                ".header { background: #3b82f6; color: white; padding: 20px; text-align: center; }" +
                ".vehicle-image { width: 100%; height: 250px; object-fit: cover; }" +
                ".content { padding: 20px; }" +
                ".price { color: #10b981; font-size: 24px; font-weight: bold; margin: 10px 0; }" +
                ".cta-button { display: inline-block; background: #10b981; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; }" +
                ".footer { background: #1e293b; color: white; padding: 15px; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'><h1>¡Oferta Especial!</h1></div>" +
                "<img src='" + imagenUrl + "' alt='" + vehiculo.getMarca() + " " + vehiculo.getModelo() + "' class='vehicle-image'>" +
                "<div class='content'>" +
                "<h2>" + vehiculo.getMarca() + " " + vehiculo.getModelo() + "</h2>" +
                "<div class='price'>$" + String.format("%.2f", vehiculo.getPrecio()) + " USD</div>" +
                "<p>Hola <strong>" + nombreUsuario + "</strong>,</p>" +
                "<p>Te presentamos esta increíble oportunidad:</p>" +
                "<p><strong>Especificaciones:</strong><br>" +
                "Año: " + vehiculo.getAño() + "<br>" +
                "Motor: " + (vehiculo.getMotor() != null ? vehiculo.getMotor() : "No especificado") + "<br>" +
                "Transmisión: " + (vehiculo.getTransmision() != null ? vehiculo.getTransmision() : "No especificado") +
                "</p>" +
                "<center><a href='" + urlVehiculo + "' class='cta-button'>Ver Detalles</a></center>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>NextGen Motors</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}