package com.springboot.MyTodoList.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.MyTodoList.DTO.SprintHoursDTO;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.service.KPIsService;

@Service
public class KPIsServiceImpl implements KPIsService {

    @Autowired
    private ToDoItemRepository toDoItemRepository;

    @Override
    public List<SprintHoursDTO> getHoursPerSprint(int user_id) {
        List<ToDoItem> items = toDoItemRepository.findByUserId(user_id);

        // Map to store sprintId -> [totalEstimated, totalActual]
        Map<Integer, int[]> sprintDataMap = new HashMap<>();

        for (ToDoItem item : items) {
            Integer sprintId = item.getSprint_id();
            Integer estimated = item.getEstimated_hours(); // Add this back
            Integer actual = item.getReal_time();

            if (sprintId != null) {
                int[] data = sprintDataMap.getOrDefault(sprintId, new int[2]);

                if (estimated != null)
                    data[0] += estimated; // estimated_hours

                if (actual != null)
                    data[1] += actual; // real_time

                sprintDataMap.put(sprintId, data);
            }
        }

        return sprintDataMap.entrySet().stream()
                .map(e -> new SprintHoursDTO(
                        e.getKey(), // sprintId
                        e.getValue()[0], // estimatedHours
                        e.getValue()[1])) // actualHours
                .sorted(Comparator.comparingInt(SprintHoursDTO::getSprintId))
                .collect(Collectors.toList());
    }

    @Override
    public List<SprintHoursDTO> getSprintHoursForAllUsers() {
        List<ToDoItem> items = toDoItemRepository.findAll();

        // Map of sprintId -> [estimated_hours_sum, real_time_sum]
        Map<Integer, int[]> sprintHoursMap = new HashMap<>();

        for (ToDoItem item : items) {
            Integer sprintId = item.getSprint_id();
            Integer realTime = item.getReal_time();
            int estimatedHours = item.getEstimated_hours(); // Already an `int`

            if (sprintId != null) {
                sprintHoursMap.putIfAbsent(sprintId, new int[2]);

                sprintHoursMap.get(sprintId)[0] += estimatedHours;

                if (realTime != null) {
                    sprintHoursMap.get(sprintId)[1] += realTime;
                }
            }
        }

        return sprintHoursMap.entrySet().stream()
                .map(e -> new SprintHoursDTO(
                        e.getKey(), // sprintId
                        e.getValue()[0], // estimated_hours
                        e.getValue()[1] // totalHours (real_time)
                ))
                .sorted(Comparator.comparingInt(SprintHoursDTO::getSprintId))
                .collect(Collectors.toList());
    }

}
