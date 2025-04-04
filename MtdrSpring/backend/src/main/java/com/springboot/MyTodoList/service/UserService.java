package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.User;
import java.util.Optional;

public interface UserService {
    /**
     * Verifica si un ID de usuario existe en la base de datos.
     * @param userId El ID del usuario a verificar
     * @return true si el usuario existe, false en caso contrario
     */
    boolean validateUser(String userId);
    
    /**
     * Asocia un ID de chat de Telegram con un usuario.
     * @param userId El ID del usuario
     * @param chatId El ID del chat de Telegram
     * @return El usuario actualizado, o null si no se encuentra
     */
    User associateChatId(String userId, Long chatId);
    
    /**
     * Obtiene un usuario por su ID.
     * @param userId El ID del usuario
     * @return Optional con el usuario si se encuentra
     */
    Optional<User> getUserById(String userId);
    
    /**
     * Busca un usuario por su ID de chat de Telegram.
     * @param chatId El ID del chat de Telegram
     * @return Optional con el usuario si se encuentra
     */
    Optional<User> findByChatId(Long chatId);
    
    /**
     * Guarda un nuevo usuario o actualiza uno existente.
     * @param user El usuario a guardar
     * @return El usuario guardado
     */
    User saveUser(User user);
    
    /**
     * Elimina un usuario por su ID.
     * @param userId El ID del usuario a eliminar
     */
    void deleteUser(String userId);
}