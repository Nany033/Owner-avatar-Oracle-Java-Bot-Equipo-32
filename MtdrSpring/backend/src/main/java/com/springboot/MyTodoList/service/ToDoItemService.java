package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.ToDoItem;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ToDoItemService {
    /**
     * Encuentra todos los elementos de la lista de tareas.
     * @return Una lista de todos los elementos ToDoItem
     */
    List<ToDoItem> findAll();
    
    /**
     * Encuentra todos los elementos activos (no completados) de la lista de tareas.
     * @return Una lista de elementos ToDoItem activos
     */
    List<ToDoItem> findAllActiveItems();
    
    /**
     * Encuentra todos los elementos completados de la lista de tareas.
     * @return Una lista de elementos ToDoItem completados
     */
    List<ToDoItem> findAllCompletedItems();
    
    /**
     * Busca un elemento de la lista de tareas por su identificador.
     * @param id El identificador del elemento a buscar
     * @return ResponseEntity con el elemento si se encuentra, o un estado NOT_FOUND
     */
    ResponseEntity<ToDoItem> getItemById(int id);
    
    /**
     * Agrega un nuevo elemento a la lista de tareas.
     * @param toDoItem El elemento a agregar
     * @return El elemento guardado con su ID generado
     */
    ToDoItem addToDoItem(ToDoItem toDoItem);
    
    /**
     * Elimina un elemento de la lista de tareas.
     * @param id El identificador del elemento a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    boolean deleteToDoItem(int id);
    
    /**
     * Actualiza un elemento existente de la lista de tareas.
     * @param id El identificador del elemento a actualizar
     * @param td El objeto con los nuevos datos
     * @return El elemento actualizado, o null si no se encuentra
     */
    ToDoItem updateToDoItem(int id, ToDoItem td);
    
    /**
     * Guarda múltiples elementos de la lista de tareas en una operación batch.
     * @param toDoItems Lista de elementos a guardar
     * @return Lista de elementos guardados con sus IDs generados
     */
    List<ToDoItem> saveAll(List<ToDoItem> toDoItems);

    public List<ToDoItem> findByUserId(int userId);

    public List<ToDoItem> findBySprintId(int sprintId);

    /**
     * Finds all tasks that were created as part of the same task split operation.
     * @param taskId The ID of one of the tasks in the split group
     * @return List of related tasks, or empty list if none found
     */
    public List<ToDoItem> findRelatedTasks(int taskId);
}