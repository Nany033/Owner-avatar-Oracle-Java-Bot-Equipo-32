package com.springboot.MyTodoList.controller.bot.handler;

import com.springboot.MyTodoList.controller.bot.state.ConversationStateManager;
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

import java.util.Optional;

@Component
public class TaskAssignmentHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(TaskAssignmentHandler.class);
    
    private final TaskAssignmentService taskAssignmentService;
    private final UserService userService;
    private final ConversationStateManager stateManager;
    
    public TaskAssignmentHandler(TaskAssignmentService taskAssignmentService,
                               UserService userService,
                               ConversationStateManager stateManager) {
        this.taskAssignmentService = taskAssignmentService;
        this.userService = userService;
        this.stateManager = stateManager;
    }
    
    @Override
    public boolean canHandle(String command) {
        return command.equals(BotCommands.ASSIGN_ITEM.getCommand())
                || command.equals(BotLabels.ASSIGN_ITEM.getLabel());
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
            return createErrorMessage(chatId, BotMessages.NOT_AUTHORIZED_ASSIGN_TASKS.getMessage());
        }
        
        // Iniciar proceso de asignación (primero crear la tarea)
        stateManager.setState(chatId, ConversationStateManager.STATE_WAITING_DESCRIPTION);

        SendMessage messageToTelegram = new SendMessage();
        messageToTelegram.setChatId(chatId);
        messageToTelegram.setText(BotMessages.TYPE_NEW_TODO_ITEM.getMessage());
        ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove(true);
        messageToTelegram.setReplyMarkup(keyboardMarkup);

        return messageToTelegram;
    }
    
    private SendMessage createErrorMessage(long chatId, String errorText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(errorText);
        return message;
    }
}