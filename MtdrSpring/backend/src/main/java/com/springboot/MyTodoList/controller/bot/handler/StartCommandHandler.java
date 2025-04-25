package com.springboot.MyTodoList.controller.bot.handler;

import com.springboot.MyTodoList.controller.bot.builder.KeyboardBuilder;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.controller.bot.state.ConversationStateManager;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.Optional;

@Component
public class StartCommandHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(StartCommandHandler.class);
    
    private final UserService userService;
    private final ConversationStateManager stateManager;
    
    public StartCommandHandler(UserService userService, ConversationStateManager stateManager) {
        this.userService = userService;
        this.stateManager = stateManager;
    }
    
    @Override
    public boolean canHandle(String command) {
        return command.equals(BotCommands.START_COMMAND.getCommand());
    }
    
    @Override
    public SendMessage handle(Update update, long chatId) {
        // Limpiar estado previo
        stateManager.clearState(chatId);
        
        logger.info("Iniciando proceso de start para chatId: {}", chatId);
        
        // Verificar si el usuario ya está validado
        Optional<User> existingUser = userService.findByChatId(chatId);
        if (existingUser.isPresent()) {
            SendMessage welcomeBackMessage = new SendMessage();
            welcomeBackMessage.setChatId(chatId);
            welcomeBackMessage.setText(BotMessages.WELCOME_BACK.getMessage() + existingUser.get().getUserId());
            
            // Crear el menú principal con opciones según el rol
            boolean isManager = "manager".equalsIgnoreCase(existingUser.get().getRol());
            welcomeBackMessage.setReplyMarkup(KeyboardBuilder.buildMainMenu(isManager));
            
            return welcomeBackMessage;
        } else {
            // Usuario no validado, solicitar número de empleado
            stateManager.setState(chatId, ConversationStateManager.STATE_WAITING_EMPLOYEE_ID);
            SendMessage askForEmployeeId = new SendMessage();
            askForEmployeeId.setChatId(chatId);
            askForEmployeeId.setText("¡Bienvenido/a a MyTodoList Bot! " + BotMessages.ASK_EMPLOYEE_ID.getMessage());
            
            // Eliminar el teclado para esta solicitud
            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true);
            askForEmployeeId.setReplyMarkup(keyboardRemove);
            
            return askForEmployeeId;
        }
    }
}