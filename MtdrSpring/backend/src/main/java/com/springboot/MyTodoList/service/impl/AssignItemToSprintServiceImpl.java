package com.springboot.MyTodoList.service.impl;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.service.AssignItemToSprintService;
import com.springboot.MyTodoList.repository.SprintsRepository;
import com.springboot.MyTodoList.model.Sprints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssignItemToSprintServiceImpl implements AssignItemToSprintService {
    @Autowired
    private ToDoItemRepository toDoItemRepository;

    @Autowired
    private SprintsRepository sprintRepository; 

    @Override
    public ToDoItem assignItemToSprint(int todoItemId, int sprint_id) {
        // Check if the item exists
        Optional<ToDoItem> todoItemOptional = toDoItemRepository.findById(todoItemId);
        if (todoItemOptional.isPresent()) {
            ToDoItem todoItem = todoItemOptional.get();
           if(todoItem.getDeadline() != null) {
            //Check if sprint exists 
               Optional<Sprints> sprintOptional = sprintRepository.findById(sprint_id);
               // Check if the sprint deadline is after the item deadline
                if (sprintOptional.isPresent() && todoItem.getDeadline().isAfter(sprintOptional.get().getTime_end())) {
                    return null; // Sprint deadline is before item deadline
                }
            }
            todoItem.setSprint_id(sprint_id); 
            return toDoItemRepository.save(todoItem);
        }
        return null; // Item not found
    }

    @Override
    public boolean isItemInSprint(int todoItemId, int sprint_id) {
        Optional<ToDoItem> todoItemOptional = toDoItemRepository.findById(todoItemId);
        if (todoItemOptional.isPresent()) {
            ToDoItem todoItem = todoItemOptional.get();
            return todoItem.getSprint_id() != null && todoItem.getSprint_id() == sprint_id;
        }
        return false;
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
            return null; // No deadline, can't determine appropriate sprint
        }

        OffsetDateTime taskDeadline = todoItem.getDeadline();
        List<Sprints> allSprints = sprintRepository.findAll();
        
        // Find the sprint that contains the task's deadline
        for (Sprints sprint : allSprints) {
            if (!taskDeadline.isBefore(sprint.getTime_start()) && 
                !taskDeadline.isAfter(sprint.getTime_end())) {
                return sprint;
            }
        }
        
        return null; // No suitable sprint found
    }

    @Override
    public List<Sprints> getAllSprints() {
        return sprintRepository.findAll();
    }
}
