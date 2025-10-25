package com.concesionario.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailBienvenidaService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    public EmailBienvenidaService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCorreoBienvenida(String email, String nombre, String apellido) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("¡Bienvenido a Motors NextGen!");
        message.setText(crearMensajeBienvenida(nombre, apellido));

        mailSender.send(message);
        System.out.println(" Correo de bienvenida enviado a: " + email);
    }

    private String crearMensajeBienvenida(String nombre, String apellido) {
        return "Hola " + nombre + " " + apellido + ",\n\n" +
                "¡Bienvenido a NextGen Motors!\n\n" +
                "Estamos emocionados de tenerte como parte de nuestra comunidad. " +
                "Tu cuenta ha sido creada exitosamente.\n\n" +
                "Saludos cordiales,\n" +
                "El equipo de  NextGen Motors \n\n" +
                "---\n" +
                "Este es un correo automático, por favor no respondas directamente.";
    }
}