package com.springboot.MyTodoList.service.impl;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.service.DeadlineService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeadlineServiceImpl implements DeadlineService {

    @Autowired
    private ToDoItemRepository toDoItemRepository;
    
    @Override
    public ToDoItem setDeadline(int todoItemId, OffsetDateTime deadline) {
        Optional<ToDoItem> todoItemOpt = toDoItemRepository.findById(todoItemId);
        
        if (!todoItemOpt.isPresent()) {
            return null;
        }
        
        ToDoItem todoItem = todoItemOpt.get();
        todoItem.setDeadline(deadline);
        return toDoItemRepository.save(todoItem);
    }
    
    @Override
    public ToDoItem setDeadlineFromString(int todoItemId, String deadlineStr) {
        if (!isValidDeadlineFormat(deadlineStr)) {
            return null;
        }
        
        OffsetDateTime deadline = parseDeadline(deadlineStr);
        return setDeadline(todoItemId, deadline);
    }
    
    @Override
    public ToDoItem removeDeadline(int todoItemId) {
        return setDeadline(todoItemId, null);
    }
    
    @Override
    public List<ToDoItem> getUpcomingDeadlines(int daysAhead) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime future = now.plusDays(daysAhead);
        
        // Use the optimized repository method instead of findAll()
        return toDoItemRepository.findUpcomingDeadlines(now, future);
    }
    
    @Override
    public List<ToDoItem> getOverdueItems() {
        OffsetDateTime now = OffsetDateTime.now();
        
        // Use the existing repository method
        return toDoItemRepository.findOverdueItems(now);
    }
    
    @Override
    public boolean isValidDeadlineFormat(String deadlineStr) {
        try {
            if (deadlineStr == null || deadlineStr.equalsIgnoreCase("NINGUNA") 
                || deadlineStr.equalsIgnoreCase("NONE")) {
                return true;
            }
            
            LocalDate.parse(deadlineStr); 
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    @Override
    public OffsetDateTime parseDeadline(String deadlineStr) {
        if (deadlineStr == null || deadlineStr.equalsIgnoreCase("NINGUNA") 
            || deadlineStr.equalsIgnoreCase("NONE")) {
            return null;
        }
        
        try {
            LocalDate date = LocalDate.parse(deadlineStr);
            return date.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}