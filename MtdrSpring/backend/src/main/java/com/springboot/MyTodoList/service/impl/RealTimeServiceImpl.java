package com.springboot.MyTodoList.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.service.RealTimeService;

@Service
public class RealTimeServiceImpl implements RealTimeService {

    @Autowired
    private ToDoItemRepository toDoItemRepository;

    @Override
    public ToDoItem setRealTime(int itemId, int realTime) {
        if (!isValidRealTime(realTime)) {
            throw new IllegalArgumentException("Invalid real time value: " + realTime);
        }

        ToDoItem item = toDoItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + itemId));
        
        item.setReal_time(realTime);
        return toDoItemRepository.save(item);
    }

    @Override
    public boolean isValidRealTime(int realTime) {
        // Validate that real time is between 1 and 100 hours
        return realTime > 0 && realTime <= 100;
    }
} 