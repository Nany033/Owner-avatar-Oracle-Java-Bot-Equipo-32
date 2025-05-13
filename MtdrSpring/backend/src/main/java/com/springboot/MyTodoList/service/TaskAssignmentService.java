package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.User;
import java.util.List;

public interface TaskAssignmentService {
    /**
     * Assigns a task to a specific user
     * @param todoItemId The ID of the task to assign
     * @param userId The ID of the user to assign the task to
     * @return The updated task with the assignment
     */
    ToDoItem assignTaskToUser(int todoItemId, String userId);
    
    /**
     * Gets all developers (users with ROL = 'developer')
     * @return List of users who are developers
     */
    List<User> getAllDevelopers();
    
    /**
     * Checks if a user is a manager
     * @param userId The ID of the user to check
     * @return true if the user is a manager, false otherwise
     */
    boolean isManager(String userId);
    
    /**
     * Gets tasks assigned to a specific user
     * @param userId The ID of the user
     * @return List of tasks assigned to the user
     */
    List<ToDoItem> getTasksAssignedToUser(String userId);
}