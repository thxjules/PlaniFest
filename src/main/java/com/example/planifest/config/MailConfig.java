package com.example.planifest.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(System.getenv("MAIL_HOST"));      // host, por ejemplo sandbox.smtp.mailtrap.io
        mailSender.setPort(Integer.parseInt(System.getenv("MAIL_PORT")));  // 587
        mailSender.setUsername(System.getenv("MAIL_USER"));  // tu usuario de Mailtrap
        mailSender.setPassword(System.getenv("MAIL_PASS"));  // tu password de Mailtrap

        // Propiedades extra
        java.util.Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }
}
