package com.springboot.MyTodoList.service.impl;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.service.ToDoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        } catch(Exception e) {
            return false;
        }
    }
    
    @Override
    public ToDoItem updateToDoItem(int id, ToDoItem td) {
        Optional<ToDoItem> toDoItemData = toDoItemRepository.findById(id);
        if(toDoItemData.isPresent()) {
            ToDoItem toDoItem = toDoItemData.get();
            toDoItem.setID(id);
            toDoItem.setCreation_ts(td.getCreation_ts());
            toDoItem.setDescription(td.getDescription());
            toDoItem.setDone(td.isDone());
            toDoItem.setDeadline(td.getDeadline());
            toDoItem.setEstimated_hours(td.getEstimated_hours());
            return toDoItemRepository.save(toDoItem);
        } else {
            return null;
        }
    }
    
    @Override
    public List<ToDoItem> saveAll(List<ToDoItem> toDoItems) {
        return toDoItemRepository.saveAll(toDoItems);
    }
}