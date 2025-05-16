package com.springboot.MyTodoList.util;

public enum BotMessages {
    
    HELLO_MYTODO_BOT("Hello! I'm MyTodoList Bot!\n Select an option from menu"),
    BOT_REGISTERED_STARTED("Bot registered and started succesfully!"),
    ITEM_DONE("Item done! Select /todolist to return to the list of todo items, or /start to go to the main screen."), 
    ITEM_UNDONE("Item undone! Select /todolist to return to the list of todo items, or /start to go to the main screen."), 
    ITEM_DELETED("Item deleted! Select /todolist to return to the list of todo items, or /start to go to the main screen."),
    TYPE_NEW_TODO_ITEM("Enter the name of the New Todo Item and press the send button."),
    NEW_ITEM_ADDED("New item added! Select /todolist to return to the list of todo items, or /start to go to the main screen."),
    BYE("Bye! Select /start to resume!"),
    REQUEST_DEADLINE("Please enter a deadline for this task (format: YYYY-MM-DD), or type 'NONE' if there's no deadline:"),
    INVALID_DATE_FORMAT("Invalid date format. Please use YYYY-MM-DD format or type 'NONE':"),
    NO_UPCOMING_DEADLINES("You don't have any upcoming deadlines."),
    NO_OVERDUE_TASKS("Good job! You don't have any overdue tasks."),
    UPCOMING_DEADLINES_TITLE("Upcoming deadlines:"),
    OVERDUE_TASKS_TITLE("Overdue tasks:"),
    COMMAND_NOT_RECOGNIZED("Command not recognized. Here are the available commands:"),

    // Estimated Hours Messages
    REQUEST_ESTIMATED_HOURS("Please enter the estimated hours required for this task:"),
    INVALID_ESTIMATED_HOURS("Invalid input. Please enter a number for the estimated hours."),
    EXCEEDED_ESTIMATED_HOURS("⚠ Warning: Task exceeds 4 hours and will be split."),
    TASK_SPLIT_SUCCESS("Task exceeded the allowed hours and has been split into multiple smaller tasks."),
    ESTIMATED_HOURS_SET("Estimated hours updated successfully!"),

    // Employee validation messages
    ASK_EMPLOYEE_ID("Por favor, introduce tu número de empleado para continuar:"),
    INVALID_EMPLOYEE_ID("Lo siento, por el momento no puedes acceder a este servicio."),
    WELCOME_EMPLOYEE("¡Bienvenido/a! Tu número de empleado ha sido validado correctamente."),
    WELCOME_BACK("¡Bienvenido/a de nuevo! Has ingresado anteriormente con el número de empleado: "),
    ACCESS_DENIED("Necesitas validar tu número de empleado para acceder a esta funcionalidad. Usa /start para comenzar."),
    
    // Task Assignment Messages
    TYPE_DEVELOPER_ID_TO_ASSIGN("Por favor, introduce el ID del desarrollador al que deseas asignar esta tarea:"),
    INVALID_DEVELOPER_ID("No se encontró un desarrollador con ese ID. Intenta de nuevo:"),
    TASK_ASSIGNED_SUCCESS("Tarea asignada exitosamente al desarrollador con ID: "),
    NOT_AUTHORIZED_ASSIGN_TASKS("No tienes autorización para asignar tareas. Solo los managers pueden hacerlo."),
    
    // Developer Tasks Messages
    SELECT_DEVELOPER_VIEW_TASKS("Por favor, introduce el ID del desarrollador para ver sus tareas:"),
    NO_TASKS_FOR_DEVELOPER("El desarrollador no tiene tareas asignadas."),
    DEVELOPER_TASKS_TITLE("Tareas asignadas al desarrollador "),
    NOT_AUTHORIZED_VIEW_DEV_TASKS("No tienes autorización para ver tareas de desarrolladores. Solo los managers pueden hacerlo.");

    private String message;

    BotMessages(String enumMessage) {
        this.message = enumMessage;
    }

    public String getMessage() {
        return message;
    }
}