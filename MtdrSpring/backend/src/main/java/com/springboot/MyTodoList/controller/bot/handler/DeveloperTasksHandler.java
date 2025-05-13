package com.springboot.MyTodoList.controller.bot.handler;

import com.springboot.MyTodoList.controller.bot.state.ConversationStateManager;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.TaskAssignmentService;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DeveloperTasksHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(DeveloperTasksHandler.class);
    
    private final TaskAssignmentService taskAssignmentService;
    private final UserService userService;
    private final ConversationStateManager stateManager;
    
    public DeveloperTasksHandler(TaskAssignmentService taskAssignmentService,
                               UserService userService,
                               ConversationStateManager stateManager) {
        this.taskAssignmentService = taskAssignmentService;
        this.userService = userService;
        this.stateManager = stateManager;
    }
    
    @Override
    public boolean canHandle(String command) {
        return command.equals(BotCommands.VIEW_DEV_TASKS.getCommand())
                || command.equals(BotLabels.VIEW_DEV_TASKS.getLabel());
    }
    
    @Override
    public SendMessage handle(Update update, long chatId) {
        // Verificar si el usuario es manager
        Optional<User> userOpt = userService.findByChatId(chatId);
        if (!userOpt.isPresent()) {
            return createErrorMessage(chatId, BotMessages.ACCESS_DENIED.getMessage());
        }
        
        User user = userOpt.get();
        if (!taskAssignmentService.isManager(user.getUserId())) {
            return createErrorMessage(chatId, BotMessages.NOT_AUTHORIZED_VIEW_DEV_TASKS.getMessage());
        }
        
        // Solicitar el ID del desarrollador
        stateManager.setState(chatId, ConversationStateManager.STATE_WAITING_DEVELOPER_VIEW);

        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setChatId(chatId);
        messageToTelegram.setText(BotMessages.SELECT_DEVELOPER_VIEW_TASKS.getMessage());
        ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove(true);
        messageToTelegram.setReplyMarkup(keyboardMarkup);

        return messageToTelegram;
    }
    
    public SendMessage handleDeveloperSelection(String developerId, long chatId) {
        // Verificar que el desarrollador existe
        if (!userService.validateUser(developerId)) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(BotMessages.INVALID_DEVELOPER_ID.getMessage());
            return message;
        }
        
        // Verificar que es un desarrollador
        Optional<User> developerOpt = userService.getUserById(developerId);
        if (!developerOpt.isPresent() || !"developer".equalsIgnoreCase(developerOpt.get().getRol())) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(BotMessages.INVALID_DEVELOPER_ID.getMessage());
            return message;
        }
        
        // Obtener las tareas del desarrollador
        List<ToDoItem> developerTasks = taskAssignmentService.getTasksAssignedToUser(developerId);
        
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        
        if (developerTasks.isEmpty()) {
            message.setText(BotMessages.NO_TASKS_FOR_DEVELOPER.getMessage());
        } else {
            StringBuilder taskList = new StringBuilder();
            taskList.append(BotMessages.DEVELOPER_TASKS_TITLE.getMessage())
                    .append(developerOpt.get().getName())
                    .append(" (ID: ")
                    .append(developerId)
                    .append("):\n\n");
            
            // Separar tareas activas y completadas
            List<ToDoItem> activeTasks = developerTasks.stream()
                    .filter(task -> !task.isDone())
                    .collect(Collectors.toList());
            
            List<ToDoItem> completedTasks = developerTasks.stream()
                    .filter(ToDoItem::isDone)
                    .collect(Collectors.toList());
            
            // Mostrar tareas activas
            if (!activeTasks.isEmpty()) {
                taskList.append("📋 Tareas Pendientes:\n");
                for (ToDoItem task : activeTasks) {
                    formatTaskInList(taskList, task);
                }
            }
            
            // Mostrar tareas completadas
            if (!completedTasks.isEmpty()) {
                taskList.append("\n✅ Tareas Completadas:\n");
                for (ToDoItem task : completedTasks) {
                    formatTaskInList(taskList, task);
                }
            }
            
            message.setText(taskList.toString());
        }
        
        stateManager.clearState(chatId);
        return message;
    }
    
    private void formatTaskInList(StringBuilder taskList, ToDoItem task) {
        taskList.append("• ")
                .append(task.getDescription());
        
        if (task.getDeadline() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            taskList.append(" (Fecha límite: ")
                    .append(task.getDeadline().format(formatter))
                    .append(")");
        }
        
        if (task.getEstimated_hours() > 0) {
            taskList.append(" [Est: ")
                    .append(task.getEstimated_hours())
                    .append("h]");
        }
        
        if (task.isDone() && task.getReal_time() != null) {
            taskList.append(" [Real: ")
                    .append(task.getReal_time())
                    .append("h]");
        }
        
        taskList.append("\n");
    }
    
    private SendMessage createErrorMessage(long chatId, String errorText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(errorText);
        return message;
    }
}