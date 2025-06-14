package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.repository.UsersRepository;
import com.springboot.MyTodoList.repository.SprintsRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskAnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(TaskAnalyticsService.class);

    @Autowired
    private ToDoItemRepository todoItemRepository;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private SprintsRepository sprintsRepository;
    
    private String openAiApiKey = System.getenv("OPENAI_API_KEY");
    
    private OpenAiService openAiService;
    
    public String generateAdvancedAnalytics() {
        logger.info("=== Iniciando generateAdvancedAnalytics ===");
        
        try {
            // Paso 1: Verificar repositorios
            logger.info("Paso 1: Verificando acceso a base de datos...");
            
            List<ToDoItem> allTasks = todoItemRepository.findAll();
            logger.info("✅ Tareas encontradas: {}", allTasks.size());
            
            List<User> allUsers = usersRepository.findAll();
            logger.info("✅ Usuarios encontrados: {}", allUsers.size());
            
            List<Sprints> allSprints = sprintsRepository.findAll();
            logger.info("✅ Sprints encontrados: {}", allSprints.size());
            
            // Paso 2: Verificar API key
            logger.info("Paso 2: Verificando configuración...");
            boolean hasApiKey = openAiApiKey != null && !openAiApiKey.trim().isEmpty();
            logger.info("API Key configurada: {}", hasApiKey ? "SÍ" : "NO");
            
            // Paso 3: Decidir tipo de análisis
            if (hasApiKey) {
                logger.info("Paso 3: Generando análisis AVANZADO con GPT-4o...");
                return generateAIAnalytics(allTasks, allUsers, allSprints);
            } else {
                logger.info("Paso 3: Generando análisis básico...");
                return generateBasicAnalytics(allTasks, allUsers, allSprints);
            }
            
        } catch (Exception e) {
            logger.error("❌ Error en generateAdvancedAnalytics: {}", e.getMessage(), e);
            
            // Fallback a análisis básico si falla OpenAI
            try {
                logger.info("🔄 Fallback: Generando análisis básico...");
                List<ToDoItem> allTasks = todoItemRepository.findAll();
                List<User> allUsers = usersRepository.findAll();
                List<Sprints> allSprints = sprintsRepository.findAll();
                
                return generateBasicAnalytics(allTasks, allUsers, allSprints) + 
                       "\n\n---\n\n❌ **Nota:** El análisis avanzado con IA falló debido a timeout. Se generó análisis básico como alternativa.";
            } catch (Exception fallbackError) {
                logger.error("❌ Error en fallback: {}", fallbackError.getMessage());
                throw fallbackError;
            }
        }
    }
    
    private String generateAIAnalytics(List<ToDoItem> tasks, List<User> users, List<Sprints> sprints) {
        try {
            // Inicializar OpenAI service con timeout extendido si es necesario
            if (openAiService == null) {
                logger.info("🤖 Inicializando OpenAI Service con timeout extendido...");
                openAiService = new OpenAiService(openAiApiKey, Duration.ofSeconds(60));
            }

            // Construir prompt completo (no reducido)
            String prompt = buildOptimizedPrompt(tasks, users, sprints);
            String model = "gpt-4o";

            logger.info("🚀 Intentando con {} (prompt completo)...", model);
            try {
                String result = callOpenAI(model, prompt);
                if (result != null) {
                    return "# 🤖 Análisis Avanzado con IA (" + model + ")\n\n" +
                        "*Generado el: " + java.time.LocalDateTime.now() + "*\n\n" +
                        result +
                        "\n\n---\n\n*Análisis generado con " + model + " usando datos de: " +
                        tasks.size() + " tareas, " + users.size() + " usuarios, " +
                        sprints.size() + " sprints.*";
                }
            } catch (Exception e) {
                logger.warn("⚠️ {} falló: {}", model, e.getMessage());
                // No intentamos con prompt reducido
            }

            // Fallback: intentar con gpt-4o-mini usando el mismo prompt completo
            model = "gpt-4o-mini";
            logger.info("🚀 Fallback: Intentando con {} (prompt completo)...", model);

            String result = callOpenAI(model, prompt);
            if (result != null) {
                return "# 🤖 Análisis con IA (" + model + " - Fallback)\n\n" +
                    "*Generado el: " + java.time.LocalDateTime.now() + "*\n\n" +
                    "⚠️ *Nota: Se usó " + model + " como fallback porque GPT-4o tuvo problemas.*\n\n" +
                    result +
                    "\n\n---\n\n*Análisis generado con " + model + " usando datos de: " +
                    tasks.size() + " tareas, " + users.size() + " usuarios, " +
                    sprints.size() + " sprints.*";
            }

            throw new RuntimeException("Ambos modelos de IA fallaron");

        } catch (Exception e) {
            logger.error("❌ Error en análisis de IA: {}", e.getMessage());

            if (e.getMessage().contains("timeout")) {
                throw new RuntimeException("⏱️ **Timeout de OpenAI**: El análisis está tardando más de lo esperado. Esto puede ser debido a alta demanda en los servidores de OpenAI.");
            } else if (e.getMessage().contains("401") || e.getMessage().contains("Unauthorized")) {
                throw new RuntimeException("🔑 **Error de Autenticación**: La API key no es válida o ha expirado.");
            } else if (e.getMessage().contains("429")) {
                throw new RuntimeException("⚠️ **Límite Excedido**: Se excedió el límite de la API. Intenta en unos minutos.");
            } else if (e.getMessage().contains("insufficient_quota")) {
                throw new RuntimeException("💳 **Sin Créditos**: No hay créditos suficientes en tu cuenta de OpenAI.");
            } else {
                throw new RuntimeException("❌ **Error de IA**: " + e.getMessage());
            }
        }
    }

    
    private String callOpenAI(String model, String prompt) {
        try {
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(List.of(
                        new ChatMessage("system", getOptimizedSystemPrompt()),
                        new ChatMessage("user", prompt)
                    ))
                    .maxTokens(3000)
                    .temperature(0.3)
                    .build();
            
            logger.info("📡 Enviando request a OpenAI {}...", model);
            long startTime = System.currentTimeMillis();
            
            String response = openAiService.createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("✅ Respuesta recibida en {}ms, longitud: {} caracteres", duration, response.length());
            
            return response;
            
        } catch (Exception e) {
            logger.error("❌ Error llamando a {}: {}", model, e.getMessage());
            throw e;
        }
    }
    
    private String buildOptimizedPrompt(List<ToDoItem> tasks, List<User> users, List<Sprints> sprints) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("## DATOS DEL PROYECTO PARA ANÁLISIS\n\n");
        
        // Información del equipo (resumida)
        prompt.append("### EQUIPO (").append(users.size()).append(" personas):\n");
        for (User user : users.stream().limit(10).collect(Collectors.toList())) { // Limitar a 10 usuarios
            prompt.append(String.format("- %s (%s)\n", user.getName(), user.getRol()));
        }
        
        // Información de sprints (resumida)
        prompt.append("\n### SPRINTS (").append(sprints.size()).append(" sprints):\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        for (Sprints sprint : sprints) {
            prompt.append(String.format("- Sprint %d: %s a %s\n", 
                sprint.getSprint_id(), 
                sprint.getTime_start().format(formatter),
                sprint.getTime_end().format(formatter)));
        }
        
        // Tareas más importantes (completadas con tiempo real)
        List<ToDoItem> importantTasks = tasks.stream()
            .filter(task -> task.isDone() && task.getReal_time() != null)
            .limit(20) // Solo las 20 más importantes
            .collect(Collectors.toList());
        
        prompt.append("\n### TAREAS COMPLETADAS CON TIEMPO REAL (").append(importantTasks.size()).append("):\n");
        for (ToDoItem task : importantTasks) {
            String userName = users.stream()
                .filter(u -> task.getUser_id() != null && u.getUserId().equals(String.valueOf(task.getUser_id())))
                .findFirst()
                .map(User::getName)
                .orElse("N/A");
            
            prompt.append(String.format("- \"%s\" | %s | Sprint %s | %dh est → %dh real\n",
                task.getDescription().length() > 50 ? task.getDescription().substring(0, 50) + "..." : task.getDescription(),
                userName,
                task.getSprint_id() != null ? task.getSprint_id() : "N/A",
                task.getEstimated_hours(),
                task.getReal_time()));
        }
        
        // Resumen estadístico
        long completed = tasks.stream().filter(ToDoItem::isDone).count();
        int totalEstimated = tasks.stream().mapToInt(ToDoItem::getEstimated_hours).sum();
        int totalReal = tasks.stream()
            .filter(task -> task.getReal_time() != null)
            .mapToInt(ToDoItem::getReal_time)
            .sum();
        
        prompt.append(String.format("\n### RESUMEN:\n"));
        prompt.append(String.format("- Total: %d tareas (%d completadas)\n", tasks.size(), completed));
        prompt.append(String.format("- Tiempo: %dh estimadas vs %dh reales\n", totalEstimated, totalReal));
        
        return prompt.toString();
    }
    
    private String buildShorterPrompt(List<ToDoItem> tasks, List<User> users, List<Sprints> sprints) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("## DATOS RESUMIDOS PARA ANÁLISIS\n\n");
        
        // Solo estadísticas clave
        long completed = tasks.stream().filter(ToDoItem::isDone).count();
        int totalEstimated = tasks.stream().mapToInt(ToDoItem::getEstimated_hours).sum();
        int totalReal = tasks.stream()
            .filter(task -> task.getReal_time() != null)
            .mapToInt(ToDoItem::getReal_time)
            .sum();
        
        prompt.append(String.format("### ESTADÍSTICAS:\n"));
        prompt.append(String.format("- %d tareas totales (%d completadas)\n", tasks.size(), completed));
        prompt.append(String.format("- %d usuarios, %d sprints\n", users.size(), sprints.size()));
        prompt.append(String.format("- %dh estimadas vs %dh reales\n\n", totalEstimated, totalReal));
        
        // Solo las 10 tareas más representativas
        List<ToDoItem> sampleTasks = tasks.stream()
            .filter(task -> task.isDone() && task.getReal_time() != null)
            .limit(10)
            .collect(Collectors.toList());
        
        prompt.append("### MUESTRA DE TAREAS:\n");
        for (ToDoItem task : sampleTasks) {
            prompt.append(String.format("- \"%s\" (%dh → %dh)\n",
                task.getDescription().length() > 30 ? task.getDescription().substring(0, 30) + "..." : task.getDescription(),
                task.getEstimated_hours(),
                task.getReal_time()));
        }
        
        return prompt.toString();
    }
    
    private String getOptimizedSystemPrompt() {
    return "Eres un analista de proyectos de software. Genera un análisis conciso y actionable que incluya:\n\n" +
           "1. CATEGORIZACIÓN: Clasifica TODAS las tareas en estas categorías (elige la más cercana si no es exacta): Back-end, Front-end, Database, DevOps, Security, Testing, Quality, Documentation, Bugs\n" +
           "2. TIEMPO POR CATEGORÍA POR SPRINT: Para cada sprint, muestra el desglose porcentual de tareas por categoría. Debe sumar 100%. Presenta esto como texto, NO uses tablas ni ningún formato tabular.\n" +
           "3. DESVIACIONES POR SPRINT: Para cada sprint muestra: 'Sprint X - Horas estimadas: Y vs Horas reales: Z (W% sobreestimación/subestimación)'. Debes presentar esta información en formato de párrafos.\n" +
           "4. DISTRIBUCIÓN POR DESARROLLADOR: Muestra el porcentaje de participación de cada desarrollador (en horas) en cada sprint. Para calcular este porcentaje, primero suma las horas trabajadas por todos los desarrolladores en ese sprint y luego calcula el porcentaje de participación de cada desarrollador con la fórmula: (Horas trabajadas por el desarrollador / Total de horas trabajadas en el sprint) * 100. Si un desarrollador no participó en un sprint, indícalo como 0%. Presenta esta información como texto, NO en tablas.\n" +
           "5. DESVIACIÓN POR DESARROLLADOR: Describe las desviaciones por cada desarrollador en todos los sprints. Si no participó en un sprint, indícalo como 'N/A'. No uses tablas.\n" +
           "6. SUGERENCIAS: Proporciona recomendaciones basadas en las tareas. Evita mencionar nombres de desarrolladores, y enfócate en mejorar la distribución de tareas, estimaciones, etc.\n" +
           "7. PRIORIZACIÓN MoSCoW: Clasifica las mejoras propuestas según Must / Should / Could / Won’t. Presenta esto en texto, sin usar tablas.\n\n" +
           "Estructura esperada:\n" +
           "### 🏷️ CATEGORIZACIÓN\n" +
           "### ⏱️ TIEMPO POR CATEGORÍA POR SPRINT\n" +
           "### 📈 DESVIACIONES POR SPRINT\n" +
           "### 👥 DISTRIBUCIÓN POR DESARROLLADOR\n" +
           "### 📊 DESVIACIÓN POR DESARROLLADOR\n" +
           "### 🎯 SUGERENCIAS\n" +
           "### 🏆 PRIORIZACIÓN MoSCoW\n\n" +
           "REQUISITOS DE FORMATO:\n" +
           "- NO uses tablas Markdown ni tablas ASCII.\n" +
           "- Usa solo texto claro y bien estructurado.\n" +
           "- Asegúrate de que todas las secciones estén bien diferenciadas por subtítulos.\n";
}





    
    private String generateBasicAnalytics(List<ToDoItem> tasks, List<User> users, List<Sprints> sprints) {
        logger.info("Generando análisis básico SIN categorización manual...");
        
        StringBuilder analytics = new StringBuilder();
        
        try {
            analytics.append("## 📊 ANÁLISIS BÁSICO DE PRODUCTIVIDAD\n\n");
            analytics.append("*Análisis generado automáticamente el: ").append(java.time.LocalDateTime.now()).append("*\n\n");
            
            // Estadísticas generales
            long totalTasks = tasks.size();
            long completedTasks = tasks.stream().filter(ToDoItem::isDone).count();
            long pendingTasks = totalTasks - completedTasks;
            
            analytics.append("### 📈 Estadísticas Generales\n\n");
            analytics.append(String.format("- **Total de tareas:** %d\n", totalTasks));
            analytics.append(String.format("- **Tareas completadas:** %d (%.1f%%)\n", 
                completedTasks, totalTasks > 0 ? (double)completedTasks/totalTasks*100 : 0));
            analytics.append(String.format("- **Tareas pendientes:** %d (%.1f%%)\n\n", 
                pendingTasks, totalTasks > 0 ? (double)pendingTasks/totalTasks*100 : 0));
            
            // Información del equipo
            analytics.append("### 👥 Equipo de Desarrollo\n\n");
            for (User user : users) {
                List<ToDoItem> userTasks = tasks.stream()
                    .filter(task -> task.getUser_id() != null && task.getUser_id().equals(Integer.valueOf(user.getUserId())))
                    .collect(Collectors.toList());
                
                long userCompleted = userTasks.stream().filter(ToDoItem::isDone).count();
                double completionRate = userTasks.isEmpty() ? 0 : (double)userCompleted / userTasks.size() * 100;
                
                analytics.append(String.format("- **%s** (%s) - %d tareas (%.1f%% completadas)\n", 
                    user.getName(), user.getRol(), userTasks.size(), completionRate));
            }
            analytics.append("\n");
            
            // Análisis por sprint
            analytics.append("### 🏃‍♂️ Análisis por Sprint\n\n");
            if (!sprints.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                for (Sprints sprint : sprints) {
                    List<ToDoItem> sprintTasks = tasks.stream()
                        .filter(task -> task.getSprint_id() != null && task.getSprint_id().equals(sprint.getSprint_id()))
                        .collect(Collectors.toList());
                    
                    long sprintCompleted = sprintTasks.stream().filter(ToDoItem::isDone).count();
                    int totalEstimated = sprintTasks.stream().mapToInt(ToDoItem::getEstimated_hours).sum();
                    int totalReal = sprintTasks.stream()
                        .filter(task -> task.getReal_time() != null)
                        .mapToInt(ToDoItem::getReal_time)
                        .sum();
                    
                    analytics.append(String.format(
                        "#### Sprint %d (%s - %s)\n" +
                        "- **Tareas:** %d (%d completadas)\n" +
                        "- **Tiempo estimado:** %d horas\n" +
                        "- **Tiempo real:** %d horas\n" +
                        "- **Desviación:** %+d horas (%.1f%%)\n\n",
                        sprint.getSprint_id(),
                        sprint.getTime_start().format(formatter),
                        sprint.getTime_end().format(formatter),
                        sprintTasks.size(),
                        sprintCompleted,
                        totalEstimated,
                        totalReal,
                        totalReal - totalEstimated,
                        totalEstimated > 0 ? ((double)(totalReal - totalEstimated) / totalEstimated * 100) : 0
                    ));
                }
            } else {
                analytics.append("*No hay sprints configurados*\n\n");
            }
            
            // Análisis de desviaciones (SIN categorización manual)
            analytics.append("### 📈 Análisis de Desviaciones\n\n");
            List<ToDoItem> completedWithTime = tasks.stream()
                .filter(task -> task.isDone() && 
                              task.getEstimated_hours() > 0 && 
                              task.getReal_time() != null)
                .collect(Collectors.toList());
            
            if (!completedWithTime.isEmpty()) {
                double avgEstimated = completedWithTime.stream()
                    .mapToInt(ToDoItem::getEstimated_hours)
                    .average().orElse(0);
                
                double avgReal = completedWithTime.stream()
                    .mapToInt(ToDoItem::getReal_time)
                    .average().orElse(0);
                
                double deviation = avgEstimated > 0 ? ((avgReal - avgEstimated) / avgEstimated) * 100 : 0;
                
                analytics.append(String.format("- **Promedio estimado:** %.1f horas\n", avgEstimated));
                analytics.append(String.format("- **Promedio real:** %.1f horas\n", avgReal));
                analytics.append(String.format("- **Desviación promedio:** %+.1f%% (%s)\n\n", 
                    deviation, 
                    deviation > 0 ? "Subestimación" : "Sobreestimación"));
                
                // Tareas con mayor desviación
                analytics.append("**Tareas con mayor desviación:**\n");
                completedWithTime.stream()
                    .sorted((a, b) -> {
                        double deviationA = ((double)a.getReal_time() - a.getEstimated_hours()) / a.getEstimated_hours();
                        double deviationB = ((double)b.getReal_time() - b.getEstimated_hours()) / b.getEstimated_hours();
                        return Double.compare(Math.abs(deviationB), Math.abs(deviationA));
                    })
                    .limit(5)
                    .forEach(task -> {
                        double taskDeviation = ((double)task.getReal_time() - task.getEstimated_hours()) / task.getEstimated_hours() * 100;
                        analytics.append(String.format("- \"%s\": %dh → %dh (%+.0f%%)\n", 
                            task.getDescription().length() > 50 ? task.getDescription().substring(0, 50) + "..." : task.getDescription(), 
                            task.getEstimated_hours(), 
                            task.getReal_time(), 
                            taskDeviation));
                    });
                analytics.append("\n");
            } else {
                analytics.append("*No hay suficientes datos de tiempo completado para análisis de desviaciones*\n\n");
            }
            
            // Tareas más complejas
            analytics.append("### 🔥 Tareas más Complejas (Por Tiempo Real)\n\n");
            tasks.stream()
                .filter(task -> task.getReal_time() != null)
                .sorted((a, b) -> Integer.compare(b.getReal_time(), a.getReal_time()))
                .limit(5)
                .forEach(task -> {
                    String userName = users.stream()
                        .filter(u -> task.getUser_id() != null && u.getUserId().equals(String.valueOf(task.getUser_id())))
                        .findFirst()
                        .map(User::getName)
                        .orElse("Sin asignar");
                    
                    analytics.append(String.format("- **\"%s\"** - %dh reales (%s)\n", 
                        task.getDescription(), 
                        task.getReal_time(),
                        userName));
                });
            analytics.append("\n");
            
            analytics.append("### 🎯 SUGERENCIAS DE MEJORA\n\n");
            analytics.append("1. **Estimaciones:** Agregar buffer del 20-30% para tareas complejas\n");
            analytics.append("2. **Balanceo:** Distribuir carga equitativamente entre desarrolladores\n");
            analytics.append("3. **Tracking:** Registrar tiempo real para todas las tareas\n");
            analytics.append("4. **Planning:** Dividir tareas grandes en subtareas más manejables\n\n");
            
            analytics.append("### 📋 PRÓXIMO SPRINT - PROPUESTAS\n\n");
            analytics.append("**MUST (Crítico):**\n");
            analytics.append("- Refactoring de código con alta desviación temporal\n");
            analytics.append("- Tests para áreas problemáticas\n\n");
            
            analytics.append("**SHOULD (Importante):**\n");
            analytics.append("- Documentación de procesos de estimación\n");
            analytics.append("- Revisión de metodología de planning\n\n");
            
            analytics.append("**COULD (Deseable):**\n");
            analytics.append("- Herramientas de tracking automático\n");
            analytics.append("- Análisis predictivo de tiempos\n\n");
            
            // Nota sobre funcionalidad avanzada
            analytics.append("---\n\n");
            analytics.append("🤖 **Para Categorización Automática Inteligente:**\n");
            analytics.append("Este análisis básico se centra en métricas de tiempo y productividad. ");
            analytics.append("Para obtener **categorización automática avanzada con IA** (Back-end, Front-end, Database, etc.) ");
            analytics.append("y análisis más sofisticado, se requiere conexión con OpenAI ChatGPT.\n\n");
            analytics.append("💡 **ChatGPT puede inferir categorías mucho mejor** que las expresiones regulares, ");
            analytics.append("entendiendo contexto, sinónimos y el propósito real de cada tarea.\n");
            
            logger.info("Análisis básico (sin categorización manual) generado exitosamente");
            return analytics.toString();
            
        } catch (Exception e) {
            logger.error("Error generando análisis básico: {}", e.getMessage(), e);
            throw new RuntimeException("Error generando análisis: " + e.getMessage(), e);
        }
    }

    }