package com.springboot.MyTodoList.service.impl;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.repository.UsersRepository;
import com.springboot.MyTodoList.service.TaskAssignmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskAssignmentServiceImpl implements TaskAssignmentService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskAssignmentServiceImpl.class);
    
    @Autowired
    private ToDoItemRepository toDoItemRepository;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Override
    public ToDoItem assignTaskToUser(int todoItemId, String userId) {
        logger.info("Asignando tarea {} al usuario {}", todoItemId, userId);
        
        // Verificar que el usuario existe
        Optional<User> userOpt = usersRepository.findById(userId);
        if (!userOpt.isPresent()) {
            logger.error("Usuario {} no encontrado", userId);
            return null;
        }
        
        // Verificar que la tarea existe
        Optional<ToDoItem> taskOpt = toDoItemRepository.findById(todoItemId);
        if (!taskOpt.isPresent()) {
            logger.error("Tarea {} no encontrada", todoItemId);
            return null;
        }
        
        // Asignar la tarea
        ToDoItem task = taskOpt.get();
        task.setUser_id(Integer.valueOf(userId));
        return toDoItemRepository.save(task);
    }
    
    @Override
    public List<User> getAllDevelopers() {
        logger.info("Obteniendo todos los developers");
        return usersRepository.findAll().stream()
                .filter(user -> "developer".equalsIgnoreCase(user.getRol()))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isManager(String userId) {
        logger.info("Verificando si el usuario {} es manager", userId);
        Optional<User> userOpt = usersRepository.findById(userId);
        return userOpt.map(user -> "manager".equalsIgnoreCase(user.getRol())).orElse(false);
    }
    
    @Override
    public List<ToDoItem> getTasksAssignedToUser(String userId) {
        logger.info("Obteniendo tareas asignadas al usuario {}", userId);
        return toDoItemRepository.findAll().stream()
                .filter(task -> userId.equals(task.getUser_id()))
                .collect(Collectors.toList());
    }
}