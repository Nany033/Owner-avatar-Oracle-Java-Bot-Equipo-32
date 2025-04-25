package com.springboot.MyTodoList.controller.bot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {
    boolean canHandle(String command);
    SendMessage handle(Update update, long chatId);
}