package com.springboot.MyTodoList.controller.bot.handler;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.controller.bot.state.ConversationStateManager;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotLabels;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TodoListCommandHandler implements CommandHandler {
    
    private final ToDoItemService toDoItemService;
    private final ConversationStateManager stateManager;
    
    public TodoListCommandHandler(ToDoItemService toDoItemService, ConversationStateManager stateManager) {
        this.toDoItemService = toDoItemService;
        this.stateManager = stateManager;
    }
    
    @Override
    public boolean canHandle(String command) {
        return command.equals(BotCommands.TODO_LIST.getCommand())
                || command.equals(BotLabels.LIST_ALL_ITEMS.getLabel())
                || command.equals(BotLabels.MY_TODO_LIST.getLabel());
    }
    
    @Override
    public SendMessage handle(Update update, long chatId) {
        stateManager.clearState(chatId);
        
        // Fetch all items once and partition them in memory
        List<ToDoItem> allItems = toDoItemService.findAll();
        
        // Partition items in memory to avoid multiple database calls
        List<ToDoItem> activeItems = allItems.stream()
                .filter(item -> !item.isDone())
                .collect(Collectors.toList());
                
        List<ToDoItem> completedItems = allItems.stream()
                .filter(ToDoItem::isDone)
                .collect(Collectors.toList());
        
        ReplyKeyboardMarkup keyboardMarkup = buildKeyboard(activeItems, completedItems);
        
        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setChatId(chatId);
        messageToTelegram.setText(BotLabels.MY_TODO_LIST.getLabel());
        messageToTelegram.setReplyMarkup(keyboardMarkup);
        
        return messageToTelegram;
    }
    
    private ReplyKeyboardMarkup buildKeyboard(List<ToDoItem> activeItems, List<ToDoItem> completedItems) {
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

        // Add active items
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (ToDoItem item : activeItems) {
            KeyboardRow currentRow = new KeyboardRow();
            
            String itemText = item.getDescription();
            if (item.getDeadline() != null) {
                itemText += " (Fecha límite: " + item.getDeadline().format(formatter) + ")";
            }
            
            currentRow.add(itemText);
            currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
            keyboard.add(currentRow);
        }

        // Add completed items
        for (ToDoItem item : completedItems) {
            KeyboardRow currentRow = new KeyboardRow();
            
            String itemText = item.getDescription();
            if (item.getDeadline() != null) {
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
        return keyboardMarkup;
    }
}