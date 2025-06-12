package com.springboot.MyTodoList.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.service.TaskAnalyticsService;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "*") 
public class AnalyticsController {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);

    @Autowired
    private TaskAnalyticsService taskAnalyticsService;

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        logger.info("Analytics test endpoint called");
        return ResponseEntity.ok("✅ Analytics Controller funciona correctamente");
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generateAnalytics() {
        logger.info("=== Iniciando generación de analytics ===");
        
        try {
            logger.info("Llamando al servicio de analytics...");
            String analytics = taskAnalyticsService.generateAdvancedAnalytics();
            
            logger.info("Analytics generado exitosamente, longitud: {} caracteres", analytics.length());
            return ResponseEntity.ok(analytics);
            
        } catch (Exception e) {
            logger.error("ERROR en generateAnalytics: {}", e.getMessage(), e);
            
            // Devolver error detallado para debugging
            String errorMessage = String.format(
                "❌ Error interno del servidor:\n\n" +
                "**Tipo:** %s\n" +
                "**Mensaje:** %s\n" +
                "**Causa:** %s\n\n" +
                "Revisa los logs del servidor para más detalles.",
                e.getClass().getSimpleName(),
                e.getMessage(),
                e.getCause() != null ? e.getCause().getMessage() : "No especificada"
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}