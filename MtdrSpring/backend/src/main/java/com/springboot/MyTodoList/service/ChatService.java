package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Chat;
import java.util.Optional;

public interface ChatService {
    /**
     * Obtiene un chat por su ID.
     * @param chatId ID del chat
     * @return Optional con el chat si se encuentra
     */
    Optional<Chat> getChatById(Long chatId);
    
    /**
     * Registra un nuevo chat o actualiza uno existente.
     * @param chatId ID del chat
     * @return El chat creado o actualizado
     */
    Chat registerChat(Long chatId);
    
    /**
     * Actualiza la marca de tiempo de última actividad de un chat.
     * @param chatId ID del chat
     * @return El chat actualizado, o null si no se encuentra
     */
    Chat updateLastActivity(Long chatId);
    
    /**
     * Verifica si existe un chat con el ID especificado.
     * @param chatId ID del chat a verificar
     * @return true si existe, false si no
     */
    boolean existsChat(Long chatId);
    
    /**
     * Guarda un chat.
     * @param chat El chat a guardar
     * @return El chat guardado
     */
    Chat saveChat(Chat chat);
}