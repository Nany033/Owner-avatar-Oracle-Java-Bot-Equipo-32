package com.springboot.MyTodoList.util;

public enum BotMessages {
    
    HELLO_MYTODO_BOT("Hello! I'm MyTodoList Bot!\nType a new todo item below and press the send button, or select an option below:\n /todolist\n /additem\n /upcoming\n /overdue"),
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
    ESTIMATED_HOURS_SET("Estimated hours updated successfully!");

    private String message;

    BotMessages(String enumMessage) {
        this.message = enumMessage;
    }

    public String getMessage() {
        return message;
    }
}