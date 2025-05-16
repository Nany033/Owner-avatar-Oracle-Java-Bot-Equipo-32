package com.springboot.MyTodoList.service.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.service.ToDoItemService;

/**
 * Implementación de la interfaz ToDoItemService que provee
 * la lógica de negocio para gestionar elementos de la lista de tareas.
 */
@Service
public class ToDoItemServiceImpl implements ToDoItemService {

    @Autowired
    private ToDoItemRepository toDoItemRepository;

    @Override
    public List<ToDoItem> findAll() {
        List<ToDoItem> todoItems = toDoItemRepository.findAll();
        return todoItems;
    }

    @Override
    public List<ToDoItem> findAllActiveItems() {

        return toDoItemRepository.findAllActiveItems();
    }

    @Override
    public List<ToDoItem> findAllCompletedItems() {

        return toDoItemRepository.findAllCompletedItems();
    }

    @Override
    public ResponseEntity<ToDoItem> getItemById(int id) {
        Optional<ToDoItem> todoData = toDoItemRepository.findById(id);
        if (todoData.isPresent()) {
            return new ResponseEntity<>(todoData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ToDoItem addToDoItem(ToDoItem toDoItem) {
        return toDoItemRepository.save(toDoItem);
    }

    @Override
    public boolean deleteToDoItem(int id) {
        try {
            toDoItemRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ToDoItem updateToDoItem(int id, ToDoItem td) {
        Optional<ToDoItem> toDoItemData = toDoItemRepository.findById(id);
        if (toDoItemData.isPresent()) {
            ToDoItem toDoItem = toDoItemData.get();
            toDoItem.setID(id);
            toDoItem.setCreation_ts(td.getCreation_ts());
            toDoItem.setDescription(td.getDescription());
            toDoItem.setDone(td.isDone());
            toDoItem.setDeadline(td.getDeadline());
            toDoItem.setEstimated_hours(td.getEstimated_hours());
            toDoItem.setCompletion_date(td.getCompletion_date());
            return toDoItemRepository.save(toDoItem);
        } else {
            return null;
        }
    }

    @Override
    public List<ToDoItem> saveAll(List<ToDoItem> toDoItems) {
        return toDoItemRepository.saveAll(toDoItems);
    }

    @Override
    public List<ToDoItem> findByUserId(int user_id) {
        return toDoItemRepository.findByUserId(user_id); // assuming a repository method exists
    }

    @Override
    public List<ToDoItem> findBySprintId(int sprint_id) {
        return toDoItemRepository.findBySprintId(sprint_id); // assuming a repository method exists
    }

    @Override
    public List<ToDoItem> findRelatedTasks(int taskId) {
        // Get the task to find its creation timestamp
        Optional<ToDoItem> taskOpt = toDoItemRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            return List.of();
        }

        ToDoItem task = taskOpt.get();
        OffsetDateTime timestamp = task.getCreation_ts();
        OffsetDateTime startTime = timestamp.minusSeconds(1);
        OffsetDateTime endTime = timestamp.plusSeconds(1);
        
        // Find all tasks created within 1 second of this task
        return toDoItemRepository.findTasksCreatedNearTime(startTime, endTime);
    }
}