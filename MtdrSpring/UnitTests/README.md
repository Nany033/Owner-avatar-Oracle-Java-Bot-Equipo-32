# Telegram Bot Proyecto

Este proyecto es un bot de Telegram desarrollado en **Java**, utilizando la API de Telegram Bots en su version 8.2.0.  

## 📌 Estructura del proyecto  

- **`Main.java`** → Punto de entrada del bot.  
- **`MiBot.java`** → Implementación del bot y manejo de mensajes.  
- **`BotTester.java`** → Pruebas y simulaciones del bot en Telegram.  

│   pom.xml
│
└───src
    ├───main
    │   └───java
    │       └───com
    │           └───example
    │               ├───bot
    │               │       MiBot.java
    │               │
    │               └───main
    │                       Main.java
    │
    └───test
        └───java
            └───com
                └───example
                    └───bot
                            MiBotTest.java

## 🚀 Cómo ejecutar el bot  

### 1️⃣ Requisitos previos  
- Tener instalado **Java 17+**  
- Configurar un bot en [@BotFather](https://core.telegram.org/bots#botfather) para obtener un `TOKEN`.  
- Configurar el archivo `config.properties` con el token del bot.  

### 2️⃣ Construcción y ejecución  
Para compilar el proyecto y generar el `.jar` ejecutable:  
```sh
mvn clean test