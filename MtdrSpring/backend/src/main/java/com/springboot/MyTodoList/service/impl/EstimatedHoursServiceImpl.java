package com.springboot.MyTodoList.service.impl;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.service.EstimatedHoursService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.springboot.MyTodoList.util.BotMessages;

@Service
public class EstimatedHoursServiceImpl implements EstimatedHoursService {

    private static final int MAX_HOURS = 4;

    @Autowired
    private ToDoItemRepository toDoItemRepository;

    @Override
    public ToDoItem setEstimatedHours(int id, int estimatedHours) {
        Optional<ToDoItem> optionalTask = toDoItemRepository.findById(id);
        if (optionalTask.isPresent()) {
            ToDoItem task = optionalTask.get();
            // Fixed method name to match the entity field name
            task.setEstimated_hours(estimatedHours);
            return toDoItemRepository.save(task);
        }
        return null;
    }

    @Override
    public boolean checkEstimatedHours(int estimatedHours) {
        return estimatedHours <= MAX_HOURS && estimatedHours > 0;
    }

    @Override
    public List<ToDoItem> splitTask(ToDoItem task) {
        List<ToDoItem> tasks = new ArrayList<>();
        int remainingHours = task.getEstimated_hours();
        int part = 1;

        while (remainingHours > 0) {
            ToDoItem subTask = new ToDoItem();
            subTask.setDescription(task.getDescription() + " (Part " + part + ")");
            subTask.setCreation_ts(task.getCreation_ts());
            subTask.setDone(false);
            subTask.setDeadline(task.getDeadline());

            int hoursForThisTask = Math.min(remainingHours, MAX_HOURS);
            subTask.setEstimated_hours(hoursForThisTask);
            tasks.add(subTask); // Don't save to DB here, just add to list

            remainingHours -= hoursForThisTask;
            part++;
        }
        return tasks;
    }
}