package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.ToDoItem;

public interface RealTimeService {
    
    /**
     * Sets the real time for a task
     * @param itemId The ID of the task
     * @param realTime The actual time taken to complete the task
     * @return The updated ToDoItem
     */
    ToDoItem setRealTime(int itemId, int realTime);
    
    /**
     * Validates if the real time is within acceptable limits
     * @param realTime The time to validate
     * @return true if the time is valid, false otherwise
     */
    boolean isValidRealTime(int realTime);
} 