package com.springboot.MyTodoList.controller.bot.builder;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.util.BotLabels;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KeyboardBuilder {
    
    public static ReplyKeyboardMarkup buildMainMenu() {
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
        return keyboardMarkup;
    }
    
    public static ReplyKeyboardMarkup buildBackButton() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
    
    public static ReplyKeyboardMarkup buildTaskListKeyboard(List<ToDoItem> items) {
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

        List<ToDoItem> activeItems = items.stream()
                .filter(item -> !item.isDone())
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

        List<ToDoItem> doneItems = items.stream()
                .filter(ToDoItem::isDone)
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
        return keyboardMarkup;
    }
}