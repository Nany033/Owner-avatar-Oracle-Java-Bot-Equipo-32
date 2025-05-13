package com.springboot.MyTodoList.controller.bot.handler;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.service.DeadlineService;
import com.springboot.MyTodoList.service.EstimatedHoursService;
import com.springboot.MyTodoList.service.AssignItemToSprintService;
import com.springboot.MyTodoList.controller.bot.state.ConversationStateManager;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

@Component
public class CreateTaskHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(CreateTaskHandler.class);
    
    private final ToDoItemService toDoItemService;
    private final DeadlineService deadlineService;
    private final EstimatedHoursService estimatedHoursService;
    private final AssignItemToSprintService assignItemToSprintService;
    private final ConversationStateManager stateManager;
    
    public CreateTaskHandler(ToDoItemService toDoItemService, 
                           DeadlineService deadlineService,
                           EstimatedHoursService estimatedHoursService,
                           AssignItemToSprintService assignItemToSprintService,
                           ConversationStateManager stateManager) {
        this.toDoItemService = toDoItemService;
        this.deadlineService = deadlineService;
        this.estimatedHoursService = estimatedHoursService;
        this.assignItemToSprintService = assignItemToSprintService;
        this.stateManager = stateManager;
    }
    
    @Override
    public boolean canHandle(String command) {
        return command.equals(BotCommands.ADD_ITEM.getCommand())
                || command.equals(BotLabels.ADD_NEW_ITEM.getLabel());
    }
    
    @Override
    public SendMessage handle(Update update, long chatId) {
        stateManager.setState(chatId, ConversationStateManager.STATE_WAITING_DESCRIPTION);

        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setChatId(chatId);
        messageToTelegram.setText(BotMessages.TYPE_NEW_TODO_ITEM.getMessage());
        ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove(true);
        messageToTelegram.setReplyMarkup(keyboardMarkup);

        return messageToTelegram;
    }
}