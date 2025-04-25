package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.Sprints;
import java.util.List;
import java.util.Map;

public interface AssignItemToSprintService {
    /**
     * Assigns a task to a sprint.
     * @param todoItemId The task ID.
     * @param sprint_id The sprint ID.
     * @return The updated task if successful, null otherwise.
     */
    ToDoItem assignItemToSprint(int todoItemId, int sprint_id);

    /**
     * Checks if a task is in a specific sprint.
     * @param todoItemId The task ID.
     * @param sprint_id The sprint ID.
     * @return true if the task is in the sprint, false otherwise.
     */
    boolean isItemInSprint(int todoItemId, int sprint_id);

    /**
     * Removes a task from a sprint.
     * @param todoItemId The task ID.
     * @param sprint_id The sprint ID.
     * @return true if successful, false otherwise.
     */
    boolean removeItemFromSprint(int todoItemId, int sprint_id);

    /**
     * Finds the appropriate sprint for a task based on its deadline.
     * @param todoItem The task to find a sprint for.
     * @return The sprint that best fits the task's deadline, or null if no suitable sprint is found.
     */
    Sprints findAppropriateSprint(ToDoItem todoItem);

    /**
     * Gets all active sprints.
     * @return A list of all active sprints.
     */
    List<Sprints> getAllSprints();
    
    /**
     * Asigna múltiples tareas a sprints apropiados en una operación batch.
     * @param todoItems Lista de tareas a asignar
     * @return Mapa con resultados: tarea ID -> sprint ID asignado (o null si no se asignó)
     */
    Map<Integer, Integer> assignItemsToSprints(List<ToDoItem> todoItems);
}