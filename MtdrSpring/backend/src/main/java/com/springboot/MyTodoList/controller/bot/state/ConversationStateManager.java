package com.springboot.MyTodoList.controller.bot.state;

import com.springboot.MyTodoList.model.ToDoItem;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class ConversationStateManager {
    
    public static final String STATE_WAITING_EMPLOYEE_ID = "WAITING_EMPLOYEE_ID";
    public static final String STATE_WAITING_DESCRIPTION = "WAITING_DESCRIPTION";
    public static final String STATE_WAITING_DEADLINE = "WAITING_DEADLINE";
    public static final String STATE_WAITING_ESTIMATED_HOURS = "WAITING_ESTIMATED_HOURS";
    public static final String STATE_WAITING_SPRINT = "WAITING_SPRINT";
    public static final String STATE_WAITING_REAL_TIME = "WAITING_REAL_TIME";
    public static final String STATE_WAITING_DEVELOPER_ID = "WAITING_DEVELOPER_ID";  // Nuevo estado para asignación
    
    // Mapa para seguir el estado de conversación de cada usuario
    private final Map<Long, String> userStates = new HashMap<>();
    
    // Mapa para almacenar datos de la tarea en construcción
    private final Map<Long, TaskData> taskDataMap = new HashMap<>();
    
    // Mapa para almacenar datos temporales durante la conversación
    private final Map<Long, Map<String, String>> tempDataMap = new HashMap<>();
    
    public void setState(Long chatId, String state) {
        userStates.put(chatId, state);
    }
    
    public String getState(Long chatId) {
        return userStates.get(chatId);
    }
    
    public void clearState(Long chatId) {
        userStates.remove(chatId);
        taskDataMap.remove(chatId);
        tempDataMap.remove(chatId);
    }
    
    public TaskData getTaskData(Long chatId) {
        return taskDataMap.computeIfAbsent(chatId, k -> new TaskData());
    }
    
    public void clearTaskData(Long chatId) {
        taskDataMap.remove(chatId);
    }
    
    public void setTempData(Long chatId, String key, String value) {
        tempDataMap.computeIfAbsent(chatId, k -> new HashMap<>()).put(key, value);
    }
    
    public String getTempData(Long chatId, String key) {
        Map<String, String> tempData = tempDataMap.get(chatId);
        return tempData != null ? tempData.get(key) : null;
    }
    
    public void clearTempData(Long chatId) {
        tempDataMap.remove(chatId);
    }
    
    public static class TaskData {
        private String description;
        private String deadline;
        private Integer estimatedHours;
        private Integer realTime;
        private Integer sprintId;
        private Integer itemId;
        private String userId;  
        
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getDeadline() {
            return deadline;
        }
        
        public void setDeadline(String deadline) {
            this.deadline = deadline;
        }
        
        public Integer getEstimatedHours() {
            return estimatedHours;
        }
        
        public void setEstimatedHours(Integer estimatedHours) {
            this.estimatedHours = estimatedHours;
        }
        
        public Integer getRealTime() {
            return realTime;
        }
        
        public void setRealTime(Integer realTime) {
            this.realTime = realTime;
        }
        
        public Integer getSprintId() {
            return sprintId;
        }
        
        public void setSprintId(Integer sprintId) {
            this.sprintId = sprintId;
        }
        
        public Integer getItemId() {
            return itemId;
        }
        
        public void setItemId(Integer itemId) {
            this.itemId = itemId;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public boolean isComplete() {
            return description != null && 
                   deadline != null && 
                   estimatedHours != null;
        }
    }
}