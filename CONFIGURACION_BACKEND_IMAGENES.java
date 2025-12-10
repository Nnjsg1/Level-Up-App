// CONFIGURACIÓN NECESARIA PARA TU BACKEND SPRING BOOT
// Archivo: src/main/java/com/tupackage/config/WebConfig.java

package com.tupackage.config; // CAMBIA ESTO por tu paquete

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Esto permite que Spring Boot sirva archivos desde la carpeta uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        // Si tu carpeta uploads está en otro lugar, ajusta la ruta:
        // .addResourceLocations("file:/ruta/completa/a/uploads/");
    }
}

/*
INSTRUCCIONES:

1. Crea este archivo en tu proyecto Spring Boot
2. Asegúrate de que la carpeta "uploads" existe en la raíz de tu proyecto backend
3. Coloca tu imagen en: backend/uploads/carats.png
4. Reinicia tu servidor Spring Boot
5. Prueba accediendo a: http://localhost:8080/uploads/carats.png en tu navegador
6. Si ves la imagen, ¡funcionará en la app!

ESTRUCTURA DE TU PROYECTO BACKEND:
backend/
├── src/
├── uploads/          <-- La carpeta con tus imágenes
│   └── carats.png
├── pom.xml (o build.gradle)
└── ...

*/

