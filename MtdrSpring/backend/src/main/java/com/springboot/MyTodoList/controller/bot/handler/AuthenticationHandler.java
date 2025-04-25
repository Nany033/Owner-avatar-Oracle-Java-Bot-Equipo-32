package com.springboot.MyTodoList.controller.bot.handler;

import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.controller.bot.state.ConversationStateManager;
import com.springboot.MyTodoList.util.BotMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class AuthenticationHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);
    
    private final UserService userService;
    private final ConversationStateManager stateManager;
    
    public AuthenticationHandler(UserService userService, ConversationStateManager stateManager) {
        this.userService = userService;
        this.stateManager = stateManager;
    }
    
    public boolean isUserAuthenticated(long chatId) {
        return userService.findByChatId(chatId).isPresent();
    }
    
    public SendMessage handleEmployeeIdInput(String employeeId, long chatId) {
        logger.info("Procesando número de empleado: {} para chatId: {}", employeeId, chatId);
        
        
        if (userService.validateUser(employeeId)) {
            User user = userService.associateChatId(employeeId, chatId);
            if (user != null) {
                logger.info("Usuario validado y asociado correctamente: {}", employeeId);
                
                stateManager.clearState(chatId);
                
                SendMessage welcomeMessage = new SendMessage();
                welcomeMessage.setChatId(chatId);
                welcomeMessage.setText(BotMessages.WELCOME_EMPLOYEE.getMessage());
                return welcomeMessage;
            } else {
                logger.error("Error al asociar chatId con usuario: {}", employeeId);
                return createErrorMessage(chatId, "Ocurrió un error al procesar tu solicitud. Por favor, intenta nuevamente.");
            }
        } else {
            logger.info("Número de empleado inválido: {}", employeeId);
            SendMessage invalidMessage = new SendMessage();
            invalidMessage.setChatId(chatId);
            invalidMessage.setText(BotMessages.INVALID_EMPLOYEE_ID.getMessage());
            
            stateManager.clearState(chatId);
            return invalidMessage;
        }
    }
    
    public SendMessage createAccessDeniedMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(BotMessages.ACCESS_DENIED.getMessage());
        return message;
    }
    
    private SendMessage createErrorMessage(long chatId, String errorText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(errorText);
        return message;
    }
}