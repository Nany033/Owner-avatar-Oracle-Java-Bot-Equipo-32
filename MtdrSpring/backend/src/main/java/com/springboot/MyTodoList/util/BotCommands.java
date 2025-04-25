package com.springboot.MyTodoList.util;

public enum BotCommands {
    START_COMMAND("/start"), 
    HIDE_COMMAND("/hide"), 
    TODO_LIST("/todolist"),
    ADD_ITEM("/additem"),
    UPCOMING_DEADLINES("/upcoming"),
    OVERDUE_TASKS("/overdue"),
    ASSIGN_ITEM("/assignitem");  

    private String command;

    BotCommands(String enumCommand) {
        this.command = enumCommand;
    }

    public String getCommand() {
        return command;
    }
}