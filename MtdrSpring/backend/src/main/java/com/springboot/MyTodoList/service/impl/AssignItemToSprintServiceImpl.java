package com.springboot.MyTodoList.service.impl;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.service.AssignItemToSprintService;
import com.springboot.MyTodoList.repository.SprintsRepository;
import com.springboot.MyTodoList.model.Sprints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AssignItemToSprintServiceImpl implements AssignItemToSprintService {
    @Autowired
    private ToDoItemRepository toDoItemRepository;

    @Autowired
    private SprintsRepository sprintRepository; 

    @Override
    public ToDoItem assignItemToSprint(int todoItemId, int sprint_id) {
        
        Optional<ToDoItem> todoItemOptional = toDoItemRepository.findById(todoItemId);
        if (!todoItemOptional.isPresent()) {
            return null; 
        }
        
        ToDoItem todoItem = todoItemOptional.get();
        
        
        if (todoItem.getDeadline() != null) {
            Optional<Sprints> sprintOptional = sprintRepository.findById(sprint_id);
            
            if (sprintOptional.isPresent() && todoItem.getDeadline().isAfter(sprintOptional.get().getTime_end())) {
                return null; 
            }
        }
        
        todoItem.setSprint_id(sprint_id);
        return toDoItemRepository.save(todoItem);
    }

    @Override
    public boolean isItemInSprint(int todoItemId, int sprint_id) {
        
        Optional<ToDoItem> todoItemOptional = toDoItemRepository.findById(todoItemId);
        return todoItemOptional.map(item -> 
            item.getSprint_id() != null && item.getSprint_id() == sprint_id
        ).orElse(false);
    }

    @Override
    public boolean removeItemFromSprint(int todoItemId, int sprint_id) {
        
        Optional<ToDoItem> todoItemOptional = toDoItemRepository.findById(todoItemId);
        if (todoItemOptional.isPresent()) {
            ToDoItem todoItem = todoItemOptional.get();
            if (todoItem.getSprint_id() != null && todoItem.getSprint_id() == sprint_id) {
                todoItem.setSprint_id(null);
                toDoItemRepository.save(todoItem);
                return true;
            }
        }
        return false;
    }

    @Override
    public Sprints findAppropriateSprint(ToDoItem todoItem) {
        if (todoItem.getDeadline() == null) {
            return null; 
        }

        OffsetDateTime taskDeadline = todoItem.getDeadline();
        List<Sprints> allSprints = getAllSprints(); 
        
        
        for (Sprints sprint : allSprints) {
            if (!taskDeadline.isBefore(sprint.getTime_start()) && 
                !taskDeadline.isAfter(sprint.getTime_end())) {
                return sprint;
            }
        }
        
        return null; 
    }

    @Cacheable("sprints") 
    @Override
    public List<Sprints> getAllSprints() {
        return sprintRepository.findAll();
    }
    
    @Override
    public Map<Integer, Integer> assignItemsToSprints(List<ToDoItem> todoItems) {
        Map<Integer, Integer> results = new HashMap<>();
        List<Sprints> allSprints = getAllSprints(); 
        
        
        List<ToDoItem> itemsToUpdate = new ArrayList<>();
        
        for (ToDoItem item : todoItems) {
            Sprints appropriateSprint = findAppropriateSprintFromCache(item, allSprints);
            
            if (appropriateSprint != null) {
                item.setSprint_id(appropriateSprint.getSprint_id());
                itemsToUpdate.add(item);
                results.put(item.getID(), appropriateSprint.getSprint_id());
            } else {
                results.put(item.getID(), null);
            }
        }
        
        
        if (!itemsToUpdate.isEmpty()) {
            toDoItemRepository.saveAll(itemsToUpdate);
        }
        
        return results;
    }
    
    
    private Sprints findAppropriateSprintFromCache(ToDoItem todoItem, List<Sprints> allSprints) {
        if (todoItem.getDeadline() == null) {
            return null;
        }
        
        OffsetDateTime taskDeadline = todoItem.getDeadline();
        
        for (Sprints sprint : allSprints) {
            if (!taskDeadline.isBefore(sprint.getTime_start()) && 
                !taskDeadline.isAfter(sprint.getTime_end())) {
                return sprint;
            }
        }
        
        return null;
    }
}