package com.springboot.MyTodoList.controller;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.Sprints;
import com.springboot.MyTodoList.service.DeadlineService;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.service.EstimatedHoursService;
import com.springboot.MyTodoList.service.AssignItemToSprintService;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;

public class ToDoItemBotController extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);
    private ToDoItemService toDoItemService;
    private DeadlineService deadlineService;
    private EstimatedHoursService estimatedHoursService;
    private AssignItemToSprintService assignItemToSprintService;
    private String botName;

    // Mapa para seguir el estado de conversación de cada usuario
    private Map<Long, String> userStates = new HashMap<>();

    // Mapa para almacenar temporalmente datos durante una conversación
    private Map<String, String> tempData = new HashMap<>();

    // Constantes para los estados de conversación
    private static final String STATE_WAITING_DESCRIPTION = "WAITING_DESCRIPTION";
    private static final String STATE_WAITING_DEADLINE = "WAITING_DEADLINE";
    private static final String STATE_WAITING_ESTIMATED_HOURS = "WAITING_ESTIMATED_HOURS";
    private static final String STATE_WAITING_SPRINT = "WAITING_SPRINT";

    public ToDoItemBotController(String botToken, String botName, ToDoItemService toDoItemService,
            DeadlineService deadlineService, EstimatedHoursService estimatedHoursService,
            AssignItemToSprintService assignItemToSprintService) {
        super(botToken);
        logger.info("Bot Token: " + botToken);
        logger.info("Bot name: " + botName);
        this.toDoItemService = toDoItemService;
        this.botName = botName;
        this.deadlineService = deadlineService;
        this.estimatedHoursService = estimatedHoursService;
        this.assignItemToSprintService = assignItemToSprintService;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageTextFromTelegram = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            logger.info("Chat ID: {}", chatId);

            if (messageTextFromTelegram.equals(BotCommands.START_COMMAND.getCommand())
                    || messageTextFromTelegram.equals(BotLabels.SHOW_MAIN_SCREEN.getLabel())) {
                handleStartCommand(chatId);
            } else if (messageTextFromTelegram.indexOf(BotLabels.DONE.getLabel()) != -1) {
                handleDoneCommand(messageTextFromTelegram, chatId);
            } else if (messageTextFromTelegram.indexOf(BotLabels.UNDO.getLabel()) != -1) {
                handleUndoCommand(messageTextFromTelegram, chatId);
            } else if (messageTextFromTelegram.indexOf(BotLabels.DELETE.getLabel()) != -1) {
                handleDeleteCommand(messageTextFromTelegram, chatId);
            } else if (messageTextFromTelegram.equals(BotCommands.HIDE_COMMAND.getCommand())
                    || messageTextFromTelegram.equals(BotLabels.HIDE_MAIN_SCREEN.getLabel())) {
                handleHideCommand(chatId);
            } else if (messageTextFromTelegram.equals(BotCommands.TODO_LIST.getCommand())
                    || messageTextFromTelegram.equals(BotLabels.LIST_ALL_ITEMS.getLabel())
                    || messageTextFromTelegram.equals(BotLabels.MY_TODO_LIST.getLabel())) {
                handleTodoListCommand(chatId);
            } else if (messageTextFromTelegram.equals(BotCommands.ADD_ITEM.getCommand())
                    || messageTextFromTelegram.equals(BotLabels.ADD_NEW_ITEM.getLabel())) {
                handleAddItemCommand(chatId);
            } else if (messageTextFromTelegram.equals(BotCommands.UPCOMING_DEADLINES.getCommand())
                    || messageTextFromTelegram.equals(BotLabels.UPCOMING_DEADLINES.getLabel())) {
                handleUpcomingDeadlinesCommand(chatId);
            } else if (messageTextFromTelegram.equals(BotCommands.OVERDUE_TASKS.getCommand())
                    || messageTextFromTelegram.equals(BotLabels.OVERDUE_TASKS.getLabel())) {
                handleOverdueTasksCommand(chatId);
            } else if (userStates.containsKey(chatId)) {
                handleConversationState(messageTextFromTelegram, chatId);
            } else {
                handleUnknownCommand(chatId);
            }
        }
    }

    private void handleStartCommand(long chatId) {
        userStates.remove(chatId);
        tempData.remove(chatId);

        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setChatId(chatId);
        messageToTelegram.setText(BotMessages.HELLO_MYTODO_BOT.getMessage());

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(BotLabels.LIST_ALL_ITEMS.getLabel());
        row.add(BotLabels.ADD_NEW_ITEM.getLabel());
        keyboard.add(row);

        row = new KeyboardRow();
        row.add(BotLabels.UPCOMING_DEADLINES.getLabel());
        row.add(BotLabels.OVERDUE_TASKS.getLabel());
        keyboard.add(row);

        row = new KeyboardRow();
        row.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        row.add(BotLabels.HIDE_MAIN_SCREEN.getLabel());
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        messageToTelegram.setReplyMarkup(keyboardMarkup);

        try {
            execute(messageToTelegram);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void handleDoneCommand(String messageText, long chatId) {
        String done = messageText.substring(0, messageText.indexOf(BotLabels.DASH.getLabel()));
        Integer id = Integer.valueOf(done);

        try {
            ToDoItem item = getToDoItemById(id).getBody();
            item.setDone(true);
            updateToDoItem(item, id);
            BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_DONE.getMessage(), this);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void handleUndoCommand(String messageText, long chatId) {
        String undo = messageText.substring(0, messageText.indexOf(BotLabels.DASH.getLabel()));
        Integer id = Integer.valueOf(undo);

        try {
            ToDoItem item = getToDoItemById(id).getBody();
            item.setDone(false);
            updateToDoItem(item, id);
            BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_UNDONE.getMessage(), this);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void handleDeleteCommand(String messageText, long chatId) {
        String delete = messageText.substring(0, messageText.indexOf(BotLabels.DASH.getLabel()));
        Integer id = Integer.valueOf(delete);

        try {
            deleteToDoItem(id).getBody();
            BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_DELETED.getMessage(), this);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void handleHideCommand(long chatId) {
        userStates.remove(chatId);
        tempData.remove(chatId);
        BotHelper.sendMessageToTelegram(chatId, BotMessages.BYE.getMessage(), this);
    }

    
    private void handleTodoListCommand(long chatId) {
        userStates.remove(chatId);
        tempData.remove(chatId);
        
        List<ToDoItem> allItems = getAllToDoItems();
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow mainScreenRowTop = new KeyboardRow();
        mainScreenRowTop.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        keyboard.add(mainScreenRowTop);

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(BotLabels.ADD_NEW_ITEM.getLabel());
        keyboard.add(firstRow);

        KeyboardRow myTodoListTitleRow = new KeyboardRow();
        myTodoListTitleRow.add(BotLabels.MY_TODO_LIST.getLabel());
        keyboard.add(myTodoListTitleRow);

        List<ToDoItem> activeItems = allItems.stream().filter(item -> !item.isDone())
                .collect(Collectors.toList());

        for (ToDoItem item : activeItems) {
            KeyboardRow currentRow = new KeyboardRow();
            
            String itemText = item.getDescription();
            if (item.getDeadline() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                itemText += " (Fecha límite: " + item.getDeadline().format(formatter) + ")";
            }
            
            currentRow.add(itemText);
            currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
            keyboard.add(currentRow);
        }

        List<ToDoItem> doneItems = allItems.stream().filter(ToDoItem::isDone)
                .collect(Collectors.toList());

        for (ToDoItem item : doneItems) {
            KeyboardRow currentRow = new KeyboardRow();
            
            String itemText = item.getDescription();
            if (item.getDeadline() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                itemText += " (Fecha: " + item.getDeadline().format(formatter) + ")";
            }
            
            currentRow.add(itemText);
            currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.UNDO.getLabel());
            currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DELETE.getLabel());
            keyboard.add(currentRow);
        }

        KeyboardRow mainScreenRowBottom = new KeyboardRow();
        mainScreenRowBottom.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        keyboard.add(mainScreenRowBottom);

        keyboardMarkup.setKeyboard(keyboard);

        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setChatId(chatId);
        messageToTelegram.setText(BotLabels.MY_TODO_LIST.getLabel());
        messageToTelegram.setReplyMarkup(keyboardMarkup);

        try {
            execute(messageToTelegram);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }


    private void handleAddItemCommand(long chatId) {
        userStates.put(chatId, STATE_WAITING_DESCRIPTION);

        try {
            SendMessage messageToTelegram = new SendMessage();
            messageToTelegram.setChatId(chatId);
            messageToTelegram.setText(BotMessages.TYPE_NEW_TODO_ITEM.getMessage());
            ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove(true);
            messageToTelegram.setReplyMarkup(keyboardMarkup);

            execute(messageToTelegram);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void handleUpcomingDeadlinesCommand(long chatId) {
        // Recuperar tareas con fechas límite próximas (próximos 7 días)
        List<ToDoItem> upcomingItems = deadlineService.getUpcomingDeadlines(7);

        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setChatId(chatId);

        if (upcomingItems.isEmpty()) {
            messageToTelegram.setText(BotMessages.NO_UPCOMING_DEADLINES.getMessage());
        } else {
            StringBuilder message = new StringBuilder(BotMessages.UPCOMING_DEADLINES_TITLE.getMessage() + "\n\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (ToDoItem item : upcomingItems) {
                message.append("• ")
                        .append(item.getDescription())
                        .append(" (Fecha límite: ")
                        .append(item.getDeadline().format(formatter))
                        .append(")\n");
            }

            messageToTelegram.setText(message.toString());
        }

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        messageToTelegram.setReplyMarkup(keyboardMarkup);

        try {
            execute(messageToTelegram);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void handleOverdueTasksCommand(long chatId) {
        List<ToDoItem> overdueItems = deadlineService.getOverdueItems();

        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setChatId(chatId);

        if (overdueItems.isEmpty()) {
            messageToTelegram.setText(BotMessages.NO_OVERDUE_TASKS.getMessage());
        } else {
            StringBuilder message = new StringBuilder(BotMessages.OVERDUE_TASKS_TITLE.getMessage() + "\n\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (ToDoItem item : overdueItems) {
                message.append("• ")
                        .append(item.getDescription())
                        .append(" (Fecha límite: ")
                        .append(item.getDeadline().format(formatter))
                        .append(")\n");
            }

            messageToTelegram.setText(message.toString());
        }

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        messageToTelegram.setReplyMarkup(keyboardMarkup);

        try {
            execute(messageToTelegram);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    // Handle conversation state based on user input
    // This method will be called when the user is in a conversation state

    private void handleConversationState(String messageText, long chatId) {
        String state = userStates.get(chatId); // use api method to get chatId
        if (state == null) {
            sendErrorMessage(chatId, "Estado de conversación no válido. Inicia el proceso de nuevo.");
            return;
        }

        try {
            switch (state) {
                case STATE_WAITING_DESCRIPTION:
                    handleDescriptionInput(messageText, chatId);
                    break;
                case STATE_WAITING_DEADLINE:
                    handleDeadlineInput(messageText, chatId);
                    break;
                case STATE_WAITING_ESTIMATED_HOURS:
                    handleEstimatedHoursInput(messageText, chatId);
                    break;
                case STATE_WAITING_SPRINT:
                    handleSprintInput(messageText, chatId);
                    break;
                default:
                    logger.warn("Estado desconocido: " + state + " para chatId: " + chatId);
                    sendErrorMessage(chatId, "Estado de conversación no reconocido. Inicia el proceso de nuevo.");
                    userStates.remove(chatId);
                    tempData.remove(String.valueOf(chatId));
            }
        } catch (Exception e) {
            logger.error("Error in handleConversationState: " + e.getLocalizedMessage(), e);
            sendErrorMessage(chatId, "Ocurrió un error inesperado. Inténtalo de nuevo.");
            // Clean up state on error
            userStates.remove(chatId);
            tempData.remove(String.valueOf(chatId));
        }
    }

    private void handleDescriptionInput(String description, long chatId) throws TelegramApiException {
        // Save the description temporarily
        tempData.put(String.valueOf(chatId), description);

        // Move to next state
        userStates.put(chatId, STATE_WAITING_DEADLINE);

        // Ask for deadline
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(
                "Por favor, introduce la fecha límite para esta tarea (formato: YYYY-MM-DD), o escribe 'NINGUNA' si no hay fecha límite:");
        execute(message);

        logger.info("Saved description for chatId: " + chatId + " - Description: " + description);
    }

    private void handleDeadlineInput(String deadlineText, long chatId) throws TelegramApiException {
        logger.info("In state waiting deadline for chatId: " + chatId);

        // Get description from temporary storage
        String description = tempData.get(String.valueOf(chatId));
        logger.info("Retrieving description from tempData: " + description);

        if (description == null) {
            logger.warn("Description is null for chatId: " + chatId);
            sendErrorMessage(chatId, "Error al recuperar la descripción de la tarea. Inténtalo de nuevo.");
            userStates.remove(chatId);
            tempData.remove(String.valueOf(chatId));
            return;
        }

        // Create new task with description only
        ToDoItem newItem = new ToDoItem();
        newItem.setDescription(description);
        newItem.setCreation_ts(OffsetDateTime.now());
        newItem.setDone(false);

        logger.info("Creating newItem with values - Description: " + newItem.getDescription() +
                ", Timestamp: " + newItem.getCreation_ts() +
                ", Done: " + newItem.isDone());

        // Save task to get an ID
        try {
            ResponseEntity<?> entity = addToDoItem(newItem);
            Integer newItemId = getNewItemIdFromResponse(entity);
            // Store the item ID for the next step
            tempData.put(String.valueOf(chatId) + "_itemId", String.valueOf(newItemId));

            // Validate and set deadline if provided
            if (!deadlineText.equalsIgnoreCase("NINGUNA")) {
                if (deadlineService.isValidDeadlineFormat(deadlineText)) {
                    deadlineService.setDeadlineFromString(newItemId, deadlineText);
                    // Store deadline for potential use later
                    tempData.put(String.valueOf(chatId) + "_deadline", deadlineText);
                } else {
                    SendMessage messageToTelegram = new SendMessage();
                    messageToTelegram.setChatId(chatId);
                    messageToTelegram.setText("Formato de fecha inválido. Usa YYYY-MM-DD o escribe 'NINGUNA':");
                    execute(messageToTelegram);
                    return; // Stay in same state
                }
            }
        } catch (Exception e) {
            logger.error("Error adding to-do item: " + e.getMessage(), e);
            sendErrorMessage(chatId, "Hubo un error al crear la tarea. Por favor, inténtalo de nuevo.");
            userStates.remove(chatId);
            tempData.remove(String.valueOf(chatId));
            return;
        }

        // Move to next state
        userStates.put(chatId, STATE_WAITING_ESTIMATED_HOURS);

        // Ask for estimated hours
        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setChatId(chatId);
        messageToTelegram.setText("¿Cuántas horas estimas que tomará esta tarea?:");
        execute(messageToTelegram);
    }

    private void handleEstimatedHoursInput(String hoursText, long chatId) throws TelegramApiException {
        logger.info("In state estimated hours for chatId: " + chatId);
        logger.info("Temp data (hours): " + hoursText);

        // Get the task ID from temporary storage
        String itemIdStr = tempData.get(String.valueOf(chatId) + "_itemId");
        if (itemIdStr == null) {
            sendErrorMessage(chatId, "Error al recuperar la tarea. Inténtalo de nuevo.");
            userStates.remove(chatId);
            tempData.remove(String.valueOf(chatId));
            return;
        }

        int itemId = Integer.valueOf(itemIdStr);

        // Validate hours input
        int estimatedHours;
        try {
            estimatedHours = Integer.parseInt(hoursText);
            if (estimatedHours <= 0 || estimatedHours > 100) {
                logger.info("Chat ID: " + chatId + " - Invalid estimated hours: " + estimatedHours);
                SendMessage messageToTelegram = new SendMessage();
                messageToTelegram.setChatId(chatId);
                messageToTelegram.setText("Por favor, introduce un número válido de horas estimadas (entre 1 y 100):");
                execute(messageToTelegram);
                return; // Stay in same state
            }
        } catch (NumberFormatException e) {
            SendMessage messageToTelegram = new SendMessage();
            messageToTelegram.setChatId(chatId);
            messageToTelegram.setText("Formato inválido. Introduce un número entre 1 y 100:");
            execute(messageToTelegram);
            return; // Stay in same state
        }

        // Set estimated hours or split task based on service logic
        if (estimatedHoursService.checkEstimatedHours(estimatedHours)) {
            // Case 1: Valid hours - update existing task
            estimatedHoursService.setEstimatedHours(itemId, estimatedHours);
            
            // Get the task and try to assign it to a sprint
            ToDoItem task = toDoItemService.getItemById(itemId).getBody();
            if (task != null) {
                Sprints appropriateSprint = assignItemToSprintService.findAppropriateSprint(task);
                if (appropriateSprint != null) {
                    ToDoItem assignedTask = assignItemToSprintService.assignItemToSprint(itemId, appropriateSprint.getSprint_id());
                    if (assignedTask != null) {
                        SendMessage messageToTelegram = new SendMessage();
                        messageToTelegram.setChatId(chatId);
                        messageToTelegram.setText("Tarea creada con duración de " + estimatedHours + " horas y asignada automáticamente al sprint " + appropriateSprint.getSprint_id() + ".");
                        execute(messageToTelegram);
                    }
                } else {
                    SendMessage messageToTelegram = new SendMessage();
                    messageToTelegram.setChatId(chatId);
                    messageToTelegram.setText("Tarea creada con duración de " + estimatedHours + " horas. No se encontró un sprint adecuado para la fecha límite.");
                    execute(messageToTelegram);
                }
            }
            cleanupState(chatId);
        } else {
            // Case 2: Needs splitting
            ToDoItem existingItem = getToDoItemById(itemId).getBody();
            if (existingItem == null) {
                sendErrorMessage(chatId, "Error al recuperar la tarea para dividirla. Inténtalo de nuevo.");
                cleanupState(chatId);
                return;
            }

            logger.info("Splitting task with ID: {} and hours: {}", itemId, estimatedHours);

            // Set the estimated hours on the original task before splitting
            existingItem.setEstimated_hours(estimatedHours);
            existingItem = toDoItemService.updateToDoItem(itemId, existingItem);

            List<ToDoItem> splitTasks = estimatedHoursService.splitTask(existingItem);
            logger.info("Split into {} subtasks", splitTasks.size());

            if (splitTasks.isEmpty()) {
                sendErrorMessage(chatId, "Error: No se pudieron crear subtareas");
                cleanupState(chatId);
                return;
            }

            List<Integer> addedSubtaskIds = new ArrayList<>();
            try {
                // Add all subtasks
                for (ToDoItem task : splitTasks) {
                    logger.info("Adding subtask with description: {}", task.getDescription());
                    ResponseEntity<?> responseEntity = addToDoItem(task);
                    ToDoItem savedTask = (ToDoItem) responseEntity.getBody();
                    if (savedTask != null) {
                        logger.info("Successfully added subtask with ID: {}", savedTask.getID());
                        addedSubtaskIds.add(savedTask.getID());
                    } else {
                        logger.error("Failed to add subtask: response body was null");
                    }
                }

                logger.info("Total subtasks added: {}", addedSubtaskIds.size());

                // Only delete original if ALL subtasks succeeded
                if (!addedSubtaskIds.isEmpty()) {
                    deleteToDoItem(existingItem.getID());
                    logger.info("Deleted original task with ID: {}", existingItem.getID());
                }

            } catch (Exception e) {
                // Rollback any created subtasks
                for (Integer id : addedSubtaskIds) {
                    deleteToDoItem(id);
                }
                logger.error("Error adding split tasks: " + e.getMessage(), e);
                sendErrorMessage(chatId, "Error al crear subtareas. Se ha revertido la operación.");
                cleanupState(chatId);
                return;
            }

            // Try to assign each subtask to a sprint
            int assignedCount = 0;
            for (Integer taskId : addedSubtaskIds) {
                ToDoItem task = getToDoItemById(taskId);
                if (task == null) {
                    logger.error("Failed to retrieve subtask with ID: {}", taskId);
                    continue; // Skip this task
                }
                Sprints appropriateSprint = assignItemToSprintService.findAppropriateSprint(task);
                if (appropriateSprint != null) {
                    ToDoItem assignedTask = assignItemToSprintService.assignItemToSprint(taskId, appropriateSprint.getSprint_id());
                    if (assignedTask != null) {
                        assignedCount++;
                        logger.info("Assigned subtask {} to sprint {}", taskId, appropriateSprint.getSprint_id());
                    }
                }
            }

            // Build success message
            int successCount = addedSubtaskIds.size();
            logger.info("Final counts - Total subtasks: {}, Assigned to sprints: {}", successCount, assignedCount);
            
            SendMessage messageToTelegram = new SendMessage();
            messageToTelegram.setChatId(chatId);
            if (successCount == 0) {
                messageToTelegram.setText("Error: No se pudieron crear subtareas. Por favor, inténtalo de nuevo.");
            } else if (assignedCount == successCount) {
                messageToTelegram.setText("Tarea dividida en " + successCount + " partes y todas fueron asignadas automáticamente a sprints.");
            } else if (assignedCount > 0) {
                messageToTelegram.setText("Tarea dividida en " + successCount + " partes. " + assignedCount + " subtareas fueron asignadas automáticamente a sprints.");
            } else {
                messageToTelegram.setText("Tarea dividida en " + successCount + " partes. No se encontraron sprints adecuados para las fechas límite.");
            }
            execute(messageToTelegram);
            cleanupState(chatId);
        }
    }

    private void handleSprintInput(String sprintText, long chatId) throws TelegramApiException {
        logger.info("In state sprint assignment for chatId: " + chatId);
        
        // Get the task ID(s) from temporary storage
        String itemIdStr = tempData.get(String.valueOf(chatId) + "_itemId");
        String splitTaskIdsStr = tempData.get(String.valueOf(chatId) + "_splitTaskIds");
        
        if (itemIdStr == null && splitTaskIdsStr == null) {
            sendErrorMessage(chatId, "Error al recuperar la tarea. Inténtalo de nuevo.");
            cleanupState(chatId);
            return;
        }

        // Handle "NINGUNO" case
        if (sprintText.equalsIgnoreCase("NINGUNO")) {
            SendMessage messageToTelegram = new SendMessage();
            messageToTelegram.setChatId(chatId);
            messageToTelegram.setText("Tarea(s) creada(s) sin asignación a sprint.");
            execute(messageToTelegram);
            cleanupState(chatId);
            return;
        }

        // Parse sprint ID
        int sprintId;
        try {
            sprintId = Integer.parseInt(sprintText);
        } catch (NumberFormatException e) {
            SendMessage messageToTelegram = new SendMessage();
            messageToTelegram.setChatId(chatId);
            messageToTelegram.setText("Formato inválido. Introduce un número de sprint válido o 'NINGUNO':");
            execute(messageToTelegram);
            return;
        }

        // Handle single task or split tasks
        if (splitTaskIdsStr != null) {
            // Handle split tasks
            List<Integer> taskIds = Arrays.stream(splitTaskIdsStr.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            
            boolean allAssigned = true;
            for (Integer taskId : taskIds) {
                ToDoItem assignedItem = assignItemToSprintService.assignItemToSprint(taskId, sprintId);
                if (assignedItem == null) {
                    allAssigned = false;
                    break;
                }
            }
            
            SendMessage messageToTelegram = new SendMessage();
            messageToTelegram.setChatId(chatId);
            if (allAssigned) {
                messageToTelegram.setText("Todas las tareas han sido asignadas al sprint " + sprintId + ".");
            } else {
                messageToTelegram.setText("Error al asignar algunas tareas al sprint. Verifica que el sprint exista y que las fechas sean compatibles.");
            }
            execute(messageToTelegram);
        } else {
            // Handle single task
            ToDoItem assignedItem = assignItemToSprintService.assignItemToSprint(Integer.parseInt(itemIdStr), sprintId);
            
            SendMessage messageToTelegram = new SendMessage();
            messageToTelegram.setChatId(chatId);
            if (assignedItem != null) {
                messageToTelegram.setText("Tarea asignada al sprint " + sprintId + ".");
            } else {
                messageToTelegram.setText("Error al asignar la tarea al sprint. Verifica que el sprint exista y que las fechas sean compatibles.");
            }
            execute(messageToTelegram);
        }
        
        cleanupState(chatId);
    }

    // Helper method to get an existing task by ID
    private ToDoItem getToDoItemById(Integer itemId) {
        try {
            return toDoItemService.getItemById(itemId).getBody();
        } catch (Exception e) {
            logger.error("Error retrieving ToDoItem with ID: " + itemId, e);
            return null;
        }
    }

    private void cleanupState(long chatId) {
        // Clean user state
        userStates.remove(chatId);

        // Clean all temp data entries for this chat
        tempData.remove(String.valueOf(chatId)); // Main entry
        tempData.remove(String.valueOf(chatId) + "_itemId");
        tempData.remove(String.valueOf(chatId) + "_deadline");

        logger.info("Cleaned state for chatId: " + chatId);
    }

    private void handleUnknownCommand(long chatId) {
        StringBuilder helpMessage = new StringBuilder("Comando no válido. Comandos disponibles:\n\n");
        helpMessage.append("/start - Iniciar el bot\n");
        helpMessage.append("/todolist - Ver todas tus tareas\n");
        helpMessage.append("/additem - Añadir una nueva tarea\n");
        helpMessage.append("/upcoming - Ver tareas con fechas límite próximas\n");
        helpMessage.append("/overdue - Ver tareas con fechas límite vencidas\n");
        helpMessage.append("/hide - Ocultar el teclado\n");

        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setChatId(chatId);
        messageToTelegram.setText(helpMessage.toString());

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        row.add(BotLabels.LIST_ALL_ITEMS.getLabel());
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        messageToTelegram.setReplyMarkup(keyboardMarkup);

        try {
            execute(messageToTelegram);
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    public List<ToDoItem> getAllToDoItems() {
        return toDoItemService.findAll();
    }

    public ResponseEntity<ToDoItem> getToDoItemById(@PathVariable int id) {
        try {
            ResponseEntity<ToDoItem> responseEntity = toDoItemService.getItemById(id);
            return new ResponseEntity<ToDoItem>(responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ToDoItem> addToDoItem(ToDoItem todoItem) throws Exception {
        ToDoItem td = toDoItemService.addToDoItem(todoItem);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + td.getID());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        return ResponseEntity.ok().headers(responseHeaders).body(td);
    }

    public ResponseEntity updateToDoItem(@RequestBody ToDoItem toDoItem, @PathVariable int id) {
        try {
            ToDoItem toDoItem1 = toDoItemService.updateToDoItem(id, toDoItem);
            System.out.println(toDoItem1.toString());
            return new ResponseEntity<>(toDoItem1, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Boolean> deleteToDoItem(@PathVariable("id") int id) {
        Boolean flag = false;
        try {
            flag = toDoItemService.deleteToDoItem(id);
            return new ResponseEntity<>(flag, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
        }
    }

    private Integer getNewItemIdFromResponse(ResponseEntity<?> entity) {
        String locationHeader = entity.getHeaders().getFirst("location");
        if (locationHeader == null || locationHeader.isEmpty()) {
            logger.error("Error: Missing location header in response.");
            return null;
        }

        try {
            return Integer.valueOf(locationHeader);
        } catch (NumberFormatException e) {
            logger.error("Error parsing location header to integer: " + locationHeader, e);
            return null;
        }
    }

    private void sendErrorMessage(long chatId, String errorMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(errorMessage);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send error message: " + e.getLocalizedMessage(), e);
        }
    }

}