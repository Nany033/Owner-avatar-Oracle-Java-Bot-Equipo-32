package com.springboot.MyTodoList.controller.bot.handler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.springboot.MyTodoList.controller.bot.state.ConversationStateManager;
import com.springboot.MyTodoList.controller.bot.state.ConversationStateManager.TaskData;
import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.AssignItemToSprintService;
import com.springboot.MyTodoList.service.DeadlineService;
import com.springboot.MyTodoList.service.EstimatedHoursService;
import com.springboot.MyTodoList.service.RealTimeService;
import com.springboot.MyTodoList.service.TaskAssignmentService;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.util.BotMessages;

@Component
public class ConversationHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConversationHandler.class);
    
    private final ToDoItemService toDoItemService;
    private final DeadlineService deadlineService;
    private final EstimatedHoursService estimatedHoursService;
    private final AssignItemToSprintService assignItemToSprintService;
    private final RealTimeService realTimeService;
    private final TaskAssignmentService taskAssignmentService;
    private final UserService userService;
    private final ConversationStateManager stateManager;
    private final List<CommandHandler> commandHandlers;  // Añadir esta línea
    
    public ConversationHandler(ToDoItemService toDoItemService,
                             DeadlineService deadlineService,
                             EstimatedHoursService estimatedHoursService,
                             AssignItemToSprintService assignItemToSprintService,
                             RealTimeService realTimeService,
                             TaskAssignmentService taskAssignmentService,
                             UserService userService,
                             ConversationStateManager stateManager,
                             List<CommandHandler> commandHandlers) {
        this.toDoItemService = toDoItemService;
        this.deadlineService = deadlineService;
        this.estimatedHoursService = estimatedHoursService;
        this.assignItemToSprintService = assignItemToSprintService;
        this.realTimeService = realTimeService;
        this.taskAssignmentService = taskAssignmentService;
        this.userService = userService;
        this.stateManager = stateManager;
        this.commandHandlers = commandHandlers;
    }
    
    public SendMessage handleConversation(String messageText, long chatId) {
        String state = stateManager.getState(chatId);
        TaskData taskData = stateManager.getTaskData(chatId);
        
        switch (state) {
            case ConversationStateManager.STATE_WAITING_DESCRIPTION:
                return handleDescriptionInput(messageText, chatId, taskData);
            case ConversationStateManager.STATE_WAITING_DEADLINE:
                return handleDeadlineInput(messageText, chatId, taskData);
            case ConversationStateManager.STATE_WAITING_ESTIMATED_HOURS:
                return handleEstimatedHoursInput(messageText, chatId, taskData);
            case ConversationStateManager.STATE_WAITING_REAL_TIME:
                return handleRealTimeInput(messageText, chatId, taskData);
            case ConversationStateManager.STATE_WAITING_DEVELOPER_ID:
                return handleDeveloperIdInput(messageText, chatId, taskData);
            case ConversationStateManager.STATE_WAITING_DEVELOPER_VIEW:
                return handleDeveloperViewInput(messageText, chatId);
            default:
                return createErrorMessage(chatId, "Estado de conversación no reconocido.");
        }
    }
    
    private SendMessage handleDeveloperViewInput(String developerId, long chatId) {
        // Delegar al handler específico
        for (CommandHandler handler : commandHandlers) {
            if (handler instanceof DeveloperTasksHandler) {
                return ((DeveloperTasksHandler) handler).handleDeveloperSelection(developerId, chatId);
            }
        }
        return createErrorMessage(chatId, "Error al procesar la solicitud.");
    }
    
    private SendMessage handleDescriptionInput(String description, long chatId, TaskData taskData) {
        taskData.setDescription(description);
        stateManager.setState(chatId, ConversationStateManager.STATE_WAITING_DEADLINE);
        
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Por favor, introduce la fecha límite para esta tarea (formato: YYYY-MM-DD), o escribe 'NINGUNA' si no hay fecha límite:");
        return message;
    }
    
    private SendMessage handleDeadlineInput(String deadlineText, long chatId, TaskData taskData) {
        if (!deadlineText.equalsIgnoreCase("NINGUNA")) {
            if (!deadlineService.isValidDeadlineFormat(deadlineText)) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Formato de fecha inválido. Usa YYYY-MM-DD o escribe 'NINGUNA':");
                return message;
            }
            taskData.setDeadline(deadlineText);
        } else {
            taskData.setDeadline("NINGUNA");
        }
        
        stateManager.setState(chatId, ConversationStateManager.STATE_WAITING_ESTIMATED_HOURS);
        
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("¿Cuántas horas estimas que tomará esta tarea?:");
        return message;
    }
    
    private SendMessage handleEstimatedHoursInput(String hoursText, long chatId, TaskData taskData) {
        try {
            int estimatedHours = Integer.parseInt(hoursText);
            if (estimatedHours <= 0 || estimatedHours > 100) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Por favor, introduce un número válido de horas estimadas (entre 1 y 100):");
                return message;
            }
            
            taskData.setEstimatedHours(estimatedHours);
            
            // Verificar si el usuario es manager y está asignando una tarea
            Optional<User> userOpt = userService.findByChatId(chatId);
            if (userOpt.isPresent() && taskAssignmentService.isManager(userOpt.get().getUserId())) {
                // Crear la tarea y pasar al estado de asignación
                ToDoItem task = createTaskAndReturn(chatId, taskData);
                if (task != null) {
                    taskData.setItemId(task.getID());
                    stateManager.setState(chatId, ConversationStateManager.STATE_WAITING_DEVELOPER_ID);
                    
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText(BotMessages.TYPE_DEVELOPER_ID_TO_ASSIGN.getMessage());
                    return message;
                } else {
                    stateManager.clearState(chatId);
                    return createErrorMessage(chatId, "Error al crear la tarea.");
                }
            } else {
                // Usuario normal, crear la tarea y finalizar
                return createTask(chatId, taskData);
            }
            
        } catch (NumberFormatException e) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Formato inválido. Introduce un número entre 1 y 100:");
            return message;
        }
    }
    
    private SendMessage handleDeveloperIdInput(String developerId, long chatId, TaskData taskData) {
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
        
        // Asignar la tarea
        if (taskData.getItemId() != null) {
            ToDoItem assignedTask = taskAssignmentService.assignTaskToUser(taskData.getItemId(), developerId);
            if (assignedTask != null) {
                stateManager.clearState(chatId);
                return createSuccessMessage(chatId, BotMessages.TASK_ASSIGNED_SUCCESS.getMessage() + developerId);
            }
        }
        
        stateManager.clearState(chatId);
        return createErrorMessage(chatId, "Error al asignar la tarea.");
    }
    
    private ToDoItem createTaskAndReturn(long chatId, TaskData taskData) {
        try {
            // Create the base task
            ToDoItem newItem = new ToDoItem();
            newItem.setDescription(taskData.getDescription());
            newItem.setCreation_ts(OffsetDateTime.now());
            newItem.setDone(false);
            newItem.setCompletion_date(null);
            newItem.setReal_time(null);
            
            // Save the task to get an ID
            ToDoItem savedItem = toDoItemService.addToDoItem(newItem);
            int itemId = savedItem.getID();
            
            // Set the deadline if exists
            if (!"NINGUNA".equals(taskData.getDeadline())) {
                savedItem = deadlineService.setDeadlineFromString(itemId, taskData.getDeadline());
            }
            
            // Set the estimated hours
            if (estimatedHoursService.checkEstimatedHours(taskData.getEstimatedHours())) {
                savedItem = estimatedHoursService.setEstimatedHours(itemId, taskData.getEstimatedHours());
                return savedItem;
            } else {
                // Task needs division
                toDoItemService.deleteToDoItem(itemId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error creating task: " + e.getMessage(), e);
            return null;
        }
    }
    
    private SendMessage createTask(long chatId, TaskData taskData) {
        // Código existente de createTask...
        try {
            // Create the base task
            ToDoItem newItem = new ToDoItem();
            newItem.setDescription(taskData.getDescription());
            newItem.setCreation_ts(OffsetDateTime.now());
            newItem.setDone(false);
            newItem.setCompletion_date(null);
            newItem.setReal_time(null);
            
            
            // Save the task to get an ID
            ToDoItem savedItem = toDoItemService.addToDoItem(newItem);
            int itemId = savedItem.getID();
            
            // Set the deadline if exists
            if (!"NINGUNA".equals(taskData.getDeadline())) {
                savedItem = deadlineService.setDeadlineFromString(itemId, taskData.getDeadline());
            }
            
            // Set the estimated hours
            if (estimatedHoursService.checkEstimatedHours(taskData.getEstimatedHours())) {
                // Simple task
                savedItem = estimatedHoursService.setEstimatedHours(itemId, taskData.getEstimatedHours());
                
                // Try to assign to a sprint using the already loaded savedItem
                String successMessage;
                
                if (savedItem != null) {
                    Sprints appropriateSprint = assignItemToSprintService.findAppropriateSprint(savedItem);
                    
                    if (appropriateSprint != null) {
                        ToDoItem assignedTask = assignItemToSprintService.assignItemToSprint(itemId, appropriateSprint.getSprint_id());
                        if (assignedTask != null) {
                            successMessage = String.format("Tarea creada con duración de %d horas y asignada automáticamente al sprint %d.", 
                                taskData.getEstimatedHours(), appropriateSprint.getSprint_id());
                        } else {
                            successMessage = String.format("Tarea creada con duración de %d horas. Error al asignar al sprint %d.", 
                                taskData.getEstimatedHours(), appropriateSprint.getSprint_id());
                        }
                    } else {
                        successMessage = String.format("Tarea creada con duración de %d horas. No se encontró un sprint adecuado para la fecha límite.", 
                            taskData.getEstimatedHours());
                    }
                } else {
                    successMessage = "Tarea creada pero no se pudo obtener para asignar a sprint.";
                }
                
                stateManager.clearState(chatId);
                return createSuccessMessage(chatId, successMessage);
                
            } else {
                // Task needs division - OPTIMIZED VERSION
                savedItem.setEstimated_hours(taskData.getEstimatedHours());
                savedItem = toDoItemService.updateToDoItem(itemId, savedItem);
                
                // Generate subtasks
                List<ToDoItem> splitTasks = estimatedHoursService.splitTask(savedItem);
                
                // Save all subtasks in batch
                List<ToDoItem> savedSubtasks = toDoItemService.saveAll(splitTasks);
                
                // Assign all subtasks to sprints in batch
                Map<Integer, Integer> assignmentResults = assignItemToSprintService.assignItemsToSprints(savedSubtasks);
                
                // Count successful assignments
                long assignedCount = assignmentResults.values().stream()
                    .filter(sprintId -> sprintId != null)
                    .count();
                
                // Delete the original task
                toDoItemService.deleteToDoItem(itemId);
                
                String successMessage;
                if (assignedCount == savedSubtasks.size()) {
                    successMessage = String.format("Tarea dividida en %d partes y todas fueron asignadas automáticamente a sprints.", savedSubtasks.size());
                } else if (assignedCount > 0) {
                    successMessage = String.format("Tarea dividida en %d partes. %d subtareas fueron asignadas automáticamente a sprints.", 
                        savedSubtasks.size(), assignedCount);
                } else {
                    successMessage = String.format("Tarea dividida en %d partes. No se encontraron sprints adecuados para las fechas límite.", savedSubtasks.size());
                }
                
                stateManager.clearState(chatId);
                return createSuccessMessage(chatId, successMessage);
            }
            
        } catch (Exception e) {
            logger.error("Error creating task: " + e.getMessage(), e);
            stateManager.clearState(chatId);
            return createErrorMessage(chatId, "Ocurrió un error al crear la tarea. Por favor, inténtalo de nuevo.");
        }
    }
    
    private SendMessage handleRealTimeInput(String hoursText, long chatId, TaskData taskData) {
        String itemIdStr = stateManager.getTempData(chatId, "itemId");
        if (itemIdStr == null) {
            return createErrorMessage(chatId, "Error al recuperar la tarea.");
        }
        
        try {
            int realTime = Integer.parseInt(hoursText);
            if (!realTimeService.isValidRealTime(realTime)) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Por favor, introduce un número válido de horas reales (entre 1 y 100):");
                return message;
            }
            
            realTimeService.setRealTime(Integer.parseInt(itemIdStr), realTime);
            stateManager.clearState(chatId);
            
            return createSuccessMessage(chatId, "¡Tarea completada! Se registraron " + realTime + " horas reales.");
            
        } catch (NumberFormatException e) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Formato inválido. Introduce un número entre 1 y 100:");
            return message;
        }
    }
    
    private SendMessage createErrorMessage(long chatId, String errorText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(errorText);
        return message;
    }
    
    private SendMessage createSuccessMessage(long chatId, String successText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(successText);
        return message;
    }
}