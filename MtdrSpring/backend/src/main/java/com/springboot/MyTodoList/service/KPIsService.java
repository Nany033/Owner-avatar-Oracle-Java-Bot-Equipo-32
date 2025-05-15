package com.springboot.MyTodoList.service;

import java.util.List;

import com.springboot.MyTodoList.DTO.SprintHoursDTO;

public interface KPIsService {
    List<SprintHoursDTO> getSprintHoursForAllUsers();

    List<SprintHoursDTO> getHoursPerSprint(int user_id);


}
