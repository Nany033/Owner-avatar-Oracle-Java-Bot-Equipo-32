package com.springboot.MyTodoList.controller.bot.handler;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.RealTimeService;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.controller.bot.state.ConversationStateManager;
import com.springboot.MyTodoList.util.BotMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TaskCompletionHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(TaskCompletionHandler.class);
    
    private final ToDoItemService toDoItemService;
    private final RealTimeService realTimeService;
    private final ConversationStateManager stateManager;
    
    public TaskCompletionHandler(ToDoItemService toDoItemService, 
                               RealTimeService realTimeService,
                               ConversationStateManager stateManager) {
        this.toDoItemService = toDoItemService;
        this.realTimeService = realTimeService;
        this.stateManager = stateManager;
    }
    
    @Override
    public boolean canHandle(String command) {
        return false;
    }
    
    @Override
    public SendMessage handle(Update update, long chatId) {
        return null;
    }
    
    public SendMessage handleDoneAction(int itemId, long chatId) {
        try {
            ToDoItem item = toDoItemService.getItemById(itemId).getBody();
            if (item == null) {
                return createErrorMessage(chatId, "Tarea no encontrada");
            }
            
            item.setDone(true);
            toDoItemService.updateToDoItem(itemId, item);
            
            // Guardar el ID de la tarea para el siguiente paso
            stateManager.setTempData(chatId, "itemId", String.valueOf(itemId));
            
            // Mover al estado de espera de tiempo real
            stateManager.setState(chatId, ConversationStateManager.STATE_WAITING_REAL_TIME);
            
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("¿Cuántas horas reales tomó completar esta tarea?");
            return message;
        } catch (Exception e) {
            logger.error("Error al marcar tarea como completada: {}", e.getMessage());
            return createErrorMessage(chatId, "Ocurrió un error al marcar la tarea como completada.");
        }
    }
    
    public SendMessage handleUndoAction(int itemId, long chatId) {
        try {
            ToDoItem item = toDoItemService.getItemById(itemId).getBody();
            if (item == null) {
                return createErrorMessage(chatId, "Tarea no encontrada");
            }
            
            item.setDone(false);
            toDoItemService.updateToDoItem(itemId, item);
            
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(BotMessages.ITEM_UNDONE.getMessage());
            return message;
        } catch (Exception e) {
            logger.error("Error al deshacer tarea: {}", e.getMessage());
            return createErrorMessage(chatId, "Ocurrió un error al deshacer la tarea.");
        }
    }
    
    public SendMessage handleDeleteAction(int itemId, long chatId) {
        try {
            toDoItemService.deleteToDoItem(itemId);
            
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(BotMessages.ITEM_DELETED.getMessage());
            return message;
        } catch (Exception e) {
            logger.error("Error al eliminar tarea: {}", e.getMessage());
            return createErrorMessage(chatId, "Ocurrió un error al eliminar la tarea.");
        }
    }
    
    private SendMessage createErrorMessage(long chatId, String errorText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(errorText);
        return message;
    }
}