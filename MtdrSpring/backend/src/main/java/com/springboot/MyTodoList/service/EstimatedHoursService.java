package com.springboot.MyTodoList.service;

import java.util.List;

import com.springboot.MyTodoList.model.ToDoItem;

public interface EstimatedHoursService {
    /**
     * Sets the estimated hours for a task.
     * @param id The task ID.
     * @param estimatedHours The estimated hours for the task.
     * @return The updated task.
     */
    ToDoItem setEstimatedHours(int id, int estimatedHours);

    /**
     * Checks if the estimated hours exceed the limit and returns a warning message.
     * @param estimatedHours The estimated hours.
     * @return A warning message if applicable.
     */
    boolean checkEstimatedHours(int estimatedHours);

    /**
     * Splits a task into multiple smaller tasks if it exceeds the maximum allowed time.
     * @param task The task to split.
     * @return A list of smaller tasks.
     */
    List<ToDoItem> splitTask(ToDoItem task);
}
