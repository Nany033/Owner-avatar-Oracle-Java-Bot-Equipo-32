package com.springboot.MyTodoList.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.MyTodoList.model.Chat;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.repository.UsersRepository;
import com.springboot.MyTodoList.service.ChatService;
import com.springboot.MyTodoList.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ChatService chatService;

    @Override
    public boolean validateUser(String userId) {
        boolean exists = usersRepository.existsById(userId);
        logger.info("Validando usuario con ID {}: {}", userId, exists ? "VÁLIDO" : "INVÁLIDO");
        return exists;
    }

    @Override
    public User associateChatId(String userId, Long chatId) {
        logger.info("Asociando chatId {} con userId {}", chatId, userId);

        // Primero, registrar el chat
        Chat chat = chatService.registerChat(chatId);
        logger.info("Chat registrado: {}", chat);

        // Luego, asociar el chat con el usuario
        Optional<User> userOpt = usersRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setChatId(chatId);
            User savedUser = usersRepository.save(user);
            logger.info("Usuario actualizado correctamente: {}", savedUser);
            return savedUser;
        } else {
            logger.warn("No se encontró usuario con ID: {}", userId);
            return null;
        }
    }

    @Override
    public Optional<User> getUserById(String userId) {
        logger.info("Buscando usuario por ID: {}", userId);
        Optional<User> user = usersRepository.findById(userId);
        if (user.isPresent()) {
            logger.info("Usuario encontrado: {}", user.get());
        } else {
            logger.info("Usuario no encontrado con ID: {}", userId);
        }
        return user;
    }

    @Override
    public Optional<User> findByChatId(Long chatId) {
        logger.info("Buscando usuario por chatId: {}", chatId);

        // Asegurarnos de actualizar la última actividad del chat
        if (chatService.existsChat(chatId)) {
            chatService.updateLastActivity(chatId);
        }

        Optional<User> user = usersRepository.findByChatId(chatId);
        if (user.isPresent()) {
            logger.info("Usuario encontrado por chatId {}: {}", chatId, user.get());
        } else {
            logger.info("No se encontró usuario con chatId: {}", chatId);
        }
        return user;
    }

    @Override
    public User saveUser(User user) {
        logger.info("Guardando usuario: {}", user);

        // Si el usuario tiene un chatId, asegurarnos de que existe
        if (user.getChatId() != null && !chatService.existsChat(user.getChatId())) {
            logger.info("Creando registro de chat para el chatId: {}", user.getChatId());
            chatService.registerChat(user.getChatId());
        }

        return usersRepository.save(user);
    }

    @Override
    public void deleteUser(String userId) {
        logger.info("Eliminando usuario con ID: {}", userId);
        usersRepository.deleteById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Obteniendo todos los usuarios");
        return usersRepository.findAll();
    }
}