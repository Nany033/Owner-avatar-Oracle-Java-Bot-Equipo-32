package com.springboot.MyTodoList.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.MyTodoList.model.Chat;
import com.springboot.MyTodoList.repository.ChatRepository;
import com.springboot.MyTodoList.service.ChatService;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);

    @Autowired
    private ChatRepository chatRepository;

    @Override
    public Optional<Chat> getChatById(Long chatId) {
        logger.info("Buscando chat por ID: {}", chatId);
        return chatRepository.findById(chatId);
    }

    @Override
    public Chat registerChat(Long chatId) {
        logger.info("Registrando chat con ID: {}", chatId);
        
        Optional<Chat> existingChat = chatRepository.findById(chatId);
        if (existingChat.isPresent()) {
            Chat chat = existingChat.get();
            logger.info("Chat existente encontrado: {}", chat);
            return chat;
        } else {
            // Crear nuevo chat con adminId = 0L
            Chat newChat = new Chat(chatId);
            Chat savedChat = chatRepository.save(newChat);
            logger.info("Nuevo chat registrado: {}", savedChat);
            return savedChat;
        }
    }

    @Override
    public Chat updateLastActivity(Long chatId) {
        
        logger.info("Actualizando actividad para chat ID: {}", chatId);
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isPresent()) {
            return chatOpt.get();
        }
        return null;
    }

    @Override
    public boolean existsChat(Long chatId) {
        boolean exists = chatRepository.existsById(chatId);
        logger.info("Verificando existencia de chat ID {}: {}", chatId, exists ? "EXISTE" : "NO EXISTE");
        return exists;
    }

    @Override
    public Chat saveChat(Chat chat) {
        logger.info("Guardando chat: {}", chat);
        
        
        if (chat.getCreatedAt() == null) {
            chat.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        
        try {
            return chatRepository.save(chat);
        } catch (Exception e) {
            logger.error("Error al guardar chat: " + e.getMessage(), e);
            
            return chat;
        }
    }
}