package com.springboot.MyTodoList.controller.bot.handler;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.DeadlineService;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.controller.bot.state.ConversationStateManager;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class TaskListHandler implements CommandHandler {
    
    private final ToDoItemService toDoItemService;
    private final DeadlineService deadlineService;
    private final ConversationStateManager stateManager;
    
    public TaskListHandler(ToDoItemService toDoItemService, 
                          DeadlineService deadlineService,
                          ConversationStateManager stateManager) {
        this.toDoItemService = toDoItemService;
        this.deadlineService = deadlineService;
        this.stateManager = stateManager;
    }
    
    @Override
    public boolean canHandle(String command) {
        return command.equals(BotCommands.UPCOMING_DEADLINES.getCommand())
                || command.equals(BotLabels.UPCOMING_DEADLINES.getLabel())
                || command.equals(BotCommands.OVERDUE_TASKS.getCommand())
                || command.equals(BotLabels.OVERDUE_TASKS.getLabel());
    }
    
    @Override
    public SendMessage handle(Update update, long chatId) {
        String messageText = update.getMessage().getText();
        
        if (messageText.equals(BotCommands.UPCOMING_DEADLINES.getCommand())
                || messageText.equals(BotLabels.UPCOMING_DEADLINES.getLabel())) {
            return handleUpcomingDeadlines(chatId);
        } else if (messageText.equals(BotCommands.OVERDUE_TASKS.getCommand())
                || messageText.equals(BotLabels.OVERDUE_TASKS.getLabel())) {
            return handleOverdueTasks(chatId);
        }
        
        return null;
    }
    
    private SendMessage handleUpcomingDeadlines(long chatId) {
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
        
        messageToTelegram.setReplyMarkup(createBackButton());
        return messageToTelegram;
    }
    
    private SendMessage handleOverdueTasks(long chatId) {
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
        
        messageToTelegram.setReplyMarkup(createBackButton());
        return messageToTelegram;
    }
    
    private ReplyKeyboardMarkup createBackButton() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}