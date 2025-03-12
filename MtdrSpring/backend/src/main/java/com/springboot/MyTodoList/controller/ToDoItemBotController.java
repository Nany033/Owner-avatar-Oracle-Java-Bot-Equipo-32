package com.springboot.MyTodoList.controller;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
import com.springboot.MyTodoList.service.DeadlineService;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;

public class ToDoItemBotController extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);
    private ToDoItemService toDoItemService;
    private DeadlineService deadlineService;
    private String botName;
    
    // Mapa para seguir el estado de conversación de cada usuario
    private Map<Long, String> userStates = new HashMap<>();
    
    // Mapa para almacenar temporalmente datos durante una conversación
    private Map<Long, String> tempData = new HashMap<>();
    
    // Constantes para los estados de conversación
    private static final String STATE_WAITING_DESCRIPTION = "WAITING_DESCRIPTION";
    private static final String STATE_WAITING_DEADLINE = "WAITING_DEADLINE";

    public ToDoItemBotController(String botToken, String botName, ToDoItemService toDoItemService, DeadlineService deadlineService) {
        super(botToken);
        logger.info("Bot Token: " + botToken);
        logger.info("Bot name: " + botName);
        this.toDoItemService = toDoItemService;
        this.botName = botName;
        this.deadlineService = deadlineService;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageTextFromTelegram = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageTextFromTelegram.equals(BotCommands.START_COMMAND.getCommand())
                    || messageTextFromTelegram.equals(BotLabels.SHOW_MAIN_SCREEN.getLabel())) {
                handleStartCommand(chatId);
            } 
            else if (messageTextFromTelegram.indexOf(BotLabels.DONE.getLabel()) != -1) {
                handleDoneCommand(messageTextFromTelegram, chatId);
            } 
            else if (messageTextFromTelegram.indexOf(BotLabels.UNDO.getLabel()) != -1) {
                handleUndoCommand(messageTextFromTelegram, chatId);
            } 
            else if (messageTextFromTelegram.indexOf(BotLabels.DELETE.getLabel()) != -1) {
                handleDeleteCommand(messageTextFromTelegram, chatId);
            } 
            else if (messageTextFromTelegram.equals(BotCommands.HIDE_COMMAND.getCommand())
                    || messageTextFromTelegram.equals(BotLabels.HIDE_MAIN_SCREEN.getLabel())) {
                handleHideCommand(chatId);
            } 
            else if (messageTextFromTelegram.equals(BotCommands.TODO_LIST.getCommand())
                    || messageTextFromTelegram.equals(BotLabels.LIST_ALL_ITEMS.getLabel())
                    || messageTextFromTelegram.equals(BotLabels.MY_TODO_LIST.getLabel())) {
                handleTodoListCommand(chatId);
            } 
            else if (messageTextFromTelegram.equals(BotCommands.ADD_ITEM.getCommand())
                    || messageTextFromTelegram.equals(BotLabels.ADD_NEW_ITEM.getLabel())) {
                handleAddItemCommand(chatId);
            }
            else if (messageTextFromTelegram.equals(BotCommands.UPCOMING_DEADLINES.getCommand())
                    || messageTextFromTelegram.equals(BotLabels.UPCOMING_DEADLINES.getLabel())) {
                handleUpcomingDeadlinesCommand(chatId);
            }
            else if (messageTextFromTelegram.equals(BotCommands.OVERDUE_TASKS.getCommand())
                    || messageTextFromTelegram.equals(BotLabels.OVERDUE_TASKS.getLabel())) {
                handleOverdueTasksCommand(chatId);
            }
            else if (userStates.containsKey(chatId)) {
                handleConversationState(messageTextFromTelegram, chatId);
            }
            else {
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

    private void handleConversationState(String messageText, long chatId) {
        String state = userStates.get(chatId);
        
        if (STATE_WAITING_DESCRIPTION.equals(state)) {
            tempData.put(chatId, messageText);
            userStates.put(chatId, STATE_WAITING_DEADLINE);
            
            try {
                SendMessage messageToTelegram = new SendMessage();
                messageToTelegram.setChatId(chatId);
                messageToTelegram.setText("Por favor, introduce la fecha límite para esta tarea (formato: YYYY-MM-DD), o escribe 'NINGUNA' si no hay fecha límite:");
                
                execute(messageToTelegram);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
        else if (STATE_WAITING_DEADLINE.equals(state)) {
            try {
                String description = tempData.get(chatId);
                ToDoItem newItem = new ToDoItem();
                newItem.setDescription(description);
                newItem.setCreation_ts(OffsetDateTime.now());
                newItem.setDone(false);
                
                // Guardar primero el ítem sin deadline
                ResponseEntity entity = addToDoItem(newItem);
                
                // Extraer el ID del nuevo ítem de la respuesta
                String locationHeader = entity.getHeaders().getFirst("location");
                int newItemId = Integer.parseInt(locationHeader);
                
                // Usar el nuevo servicio para establecer la fecha límite
                if (!messageText.equalsIgnoreCase("NINGUNA")) {
                    // Validar y establecer el deadline usando el servicio
                    if (deadlineService.isValidDeadlineFormat(messageText)) {
                        deadlineService.setDeadlineFromString(newItemId, messageText);
                        
                        // Preparar mensaje de confirmación
                        ToDoItem updatedItem = getToDoItemById(newItemId).getBody();
                        String confirmationText;
                        if (updatedItem.getDeadline() != null) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            confirmationText = "Nueva tarea añadida con fecha límite: " + 
                                updatedItem.getDeadline().format(formatter);
                        } else {
                            confirmationText = BotMessages.NEW_ITEM_ADDED.getMessage();
                        }
                        
                        // Enviar confirmación
                        userStates.remove(chatId);
                        tempData.remove(chatId);
                        BotHelper.sendMessageToTelegram(chatId, confirmationText, this);
                    } else {
                        // Formato de fecha inválido
                        SendMessage errorMessage = new SendMessage();
                        errorMessage.setChatId(chatId);
                        errorMessage.setText("Formato de fecha inválido. Por favor, usa YYYY-MM-DD o escribe 'NINGUNA':");
                        execute(errorMessage);
                    }
                } else {
                    // Sin fecha límite
                    userStates.remove(chatId);
                    tempData.remove(chatId);
                    BotHelper.sendMessageToTelegram(chatId, BotMessages.NEW_ITEM_ADDED.getMessage(), this);
                }
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                userStates.remove(chatId);
                tempData.remove(chatId);
            }
        }
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

    public ResponseEntity addToDoItem(@RequestBody ToDoItem todoItem) throws Exception {
        ToDoItem td = toDoItemService.addToDoItem(todoItem);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + td.getID());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        // URI location = URI.create(""+td.getID())

        return ResponseEntity.ok().headers(responseHeaders).build();
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
}