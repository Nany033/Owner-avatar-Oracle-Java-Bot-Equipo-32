package com.springboot.MyTodoList.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.DTO.SprintHoursDTO;
import com.springboot.MyTodoList.service.KPIsService;

@RestController
@RequestMapping("/KPIs")
public class KPIsController {

    @Autowired
    private KPIsService kpisService;

    @GetMapping("/hours-per-sprint/{userId}")
    public ResponseEntity<List<SprintHoursDTO>> getHoursPerSprint(@PathVariable
    int userId) {
    List<SprintHoursDTO> data = kpisService.getHoursPerSprint(userId);
    return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/hours-per-sprint")
    public List<SprintHoursDTO> getSprintHours() {
        return kpisService.getSprintHoursForAllUsers();
    }

}
