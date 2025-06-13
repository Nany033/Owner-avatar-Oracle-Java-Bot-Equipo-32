package com.springboot.MyTodoList.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.controller.bot.handler.AuthenticationHandler;
import com.springboot.MyTodoList.controller.bot.handler.CommandHandler;
import com.springboot.MyTodoList.controller.bot.handler.ConversationHandler;
import com.springboot.MyTodoList.controller.bot.handler.CreateTaskHandler;
import com.springboot.MyTodoList.controller.bot.handler.DeveloperTasksHandler;
import com.springboot.MyTodoList.controller.bot.handler.StartCommandHandler;
import com.springboot.MyTodoList.controller.bot.handler.TaskAssignmentHandler;
import com.springboot.MyTodoList.controller.bot.handler.TaskCompletionHandler;
import com.springboot.MyTodoList.controller.bot.handler.TaskListHandler;
import com.springboot.MyTodoList.controller.bot.handler.TodoListCommandHandler;
import com.springboot.MyTodoList.controller.bot.state.ConversationStateManager;
import com.springboot.MyTodoList.service.AssignItemToSprintService;
import com.springboot.MyTodoList.service.DeadlineService;
import com.springboot.MyTodoList.service.EstimatedHoursService;
import com.springboot.MyTodoList.service.RealTimeService;
import com.springboot.MyTodoList.service.TaskAssignmentService;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.util.BotLabels;

public class ToDoItemBotController extends TelegramLongPollingBot {
    
    private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);
    
    private final String botName;
    private final ConversationStateManager stateManager;
    private final ConversationHandler conversationHandler;
    private final AuthenticationHandler authenticationHandler;
    private final List<CommandHandler> commandHandlers;
    @Value(System.getenv("OPENAI_API_KEY"))
    private String openAiApiKey;
    
    public ToDoItemBotController(String botToken, String botName, 
                                ToDoItemService toDoItemService,
                                DeadlineService deadlineService,
                                EstimatedHoursService estimatedHoursService,
                                RealTimeService realTimeService,
                                AssignItemToSprintService assignItemToSprintService,
                                UserService userService,
                                TaskAssignmentService taskAssignmentService) {
        super(botToken);
        this.botName = botName;
        this.stateManager = new ConversationStateManager();
        
        // Inicializar handlers
        this.commandHandlers = new ArrayList<>();
        commandHandlers.add(new StartCommandHandler(userService, stateManager));
        commandHandlers.add(new TodoListCommandHandler(toDoItemService, stateManager));
        commandHandlers.add(new CreateTaskHandler(toDoItemService, deadlineService, 
                                                  estimatedHoursService, assignItemToSprintService, stateManager));
        commandHandlers.add(new TaskCompletionHandler(toDoItemService, realTimeService, stateManager));
        commandHandlers.add(new TaskListHandler(toDoItemService, deadlineService, stateManager));
        commandHandlers.add(new TaskAssignmentHandler(taskAssignmentService, userService, stateManager));
        commandHandlers.add(new DeveloperTasksHandler(taskAssignmentService, userService, stateManager)); // Nuevo handler
        
        this.conversationHandler = new ConversationHandler(toDoItemService, deadlineService,
                                                         estimatedHoursService, assignItemToSprintService,
                                                         realTimeService, taskAssignmentService, 
                                                         userService, stateManager, commandHandlers); // Añadir commandHandlers
        
        this.authenticationHandler = new AuthenticationHandler(userService, stateManager);
        
        logger.info("Bot Token: " + botToken);
        logger.info("Bot name: " + botName);
        logger.info("OpenAI API Key: " + openAiApiKey);
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            logger.info("Chat ID: {}, Mensaje: {}", chatId, messageText);
            
            try {
                SendMessage response = processMessage(messageText, chatId);
                if (response != null) {
                    execute(response);
                }
            } catch (TelegramApiException e) {
                logger.error("Error al enviar mensaje: {}", e.getMessage());
            }
        }
    }
    
    private SendMessage processMessage(String messageText, long chatId) {
        // Verificar si estamos en un estado de conversación
        String currentState = stateManager.getState(chatId);
        
        // Manejar el estado de autenticación
        if (currentState != null && currentState.equals(ConversationStateManager.STATE_WAITING_EMPLOYEE_ID)) {
            return authenticationHandler.handleEmployeeIdInput(messageText, chatId);
        }
        
        // Verificar si el usuario está autenticado para otros comandos
        if (!authenticationHandler.isUserAuthenticated(chatId)) {
            // Solo permitir el comando /start para usuarios no autenticados
            for (CommandHandler handler : commandHandlers) {
                if (handler.canHandle(messageText) && handler instanceof StartCommandHandler) {
                    return handler.handle(null, chatId);
                }
            }
            return authenticationHandler.createAccessDeniedMessage(chatId);
        }
        
        // Si hay un estado de conversación activo, manejarlo
        if (currentState != null) {
            return conversationHandler.handleConversation(messageText, chatId);
        }
        
        // Verificar si es un comando de acción (Done, Undo, Delete)
        if (messageText.contains(BotLabels.DASH.getLabel())) {
            return handleActionCommand(messageText, chatId);
        }
        
        // Buscar handler para el comando
        for (CommandHandler handler : commandHandlers) {
            if (handler.canHandle(messageText)) {
                return handler.handle(null, chatId);
            }
        }
        
        // Comando no reconocido
        return createUnknownCommandMessage(chatId);
    }
    
    private SendMessage handleActionCommand(String messageText, long chatId) {
        try {
            String[] parts = messageText.split(BotLabels.DASH.getLabel());
            if (parts.length < 2) {
                return createErrorMessage(chatId, "Formato de comando inválido");
            }
            
            int itemId = Integer.parseInt(parts[0]);
            String action = parts[1];
            
            if (action.equals(BotLabels.DONE.getLabel())) {
                return commandHandlers.stream()
                    .filter(h -> h instanceof TaskCompletionHandler)
                    .findFirst()
                    .map(h -> ((TaskCompletionHandler) h).handleDoneAction(itemId, chatId))
                    .orElse(createErrorMessage(chatId, "Error al procesar comando"));
            } else if (action.equals(BotLabels.UNDO.getLabel())) {
                return commandHandlers.stream()
                    .filter(h -> h instanceof TaskCompletionHandler)
                    .findFirst()
                    .map(h -> ((TaskCompletionHandler) h).handleUndoAction(itemId, chatId))
                    .orElse(createErrorMessage(chatId, "Error al procesar comando"));
            } else if (action.equals(BotLabels.DELETE.getLabel())) {
                return commandHandlers.stream()
                    .filter(h -> h instanceof TaskCompletionHandler)
                    .findFirst()
                    .map(h -> ((TaskCompletionHandler) h).handleDeleteAction(itemId, chatId))
                    .orElse(createErrorMessage(chatId, "Error al procesar comando"));
            }
        } catch (NumberFormatException e) {
            logger.error("Error al parsear ID de tarea: {}", e.getMessage());
        }
        
        return createErrorMessage(chatId, "Error al procesar comando");
    }
    
    private SendMessage createUnknownCommandMessage(long chatId) {
        StringBuilder helpMessage = new StringBuilder("Comando no válido. Comandos disponibles:\n\n");
        helpMessage.append("/start - Iniciar el bot\n");
        helpMessage.append("/todolist - Ver todas tus tareas\n");
        helpMessage.append("/additem - Añadir una nueva tarea\n");
        helpMessage.append("/upcoming - Ver tareas con fechas límite próximas\n");
        helpMessage.append("/overdue - Ver tareas con fechas límite vencidas\n");
        helpMessage.append("/assignitem - Asignar una tarea (solo managers)\n");
        helpMessage.append("/viewdevtasks - Ver tareas de desarrolladores (solo managers)\n");
        helpMessage.append("/hide - Ocultar el teclado\n");
        
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(helpMessage.toString());
        return message;
    }
    
    private SendMessage createErrorMessage(long chatId, String errorText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(errorText);
        return message;
    }
    
    @Override
    public String getBotUsername() {
        return botName;
    }
}