package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.ToDoItem;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.List;

public interface DeadlineService {
    ToDoItem setDeadline(int todoItemId, OffsetDateTime deadline);
    
    ToDoItem setDeadlineFromString(int todoItemId, String deadlineStr);
    
    ToDoItem removeDeadline(int todoItemId);
    
    List<ToDoItem> getUpcomingDeadlines(int daysAhead);
    
    List<ToDoItem> getOverdueItems();
    
    boolean isValidDeadlineFormat(String deadlineStr);
    
    OffsetDateTime parseDeadline(String deadlineStr);
}