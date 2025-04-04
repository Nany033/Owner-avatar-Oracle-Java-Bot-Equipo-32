package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, String> {
    
    /**
     * Busca un usuario por su ID de chat de Telegram
     * @param chatId El ID del chat de Telegram
     * @return Optional con el usuario si se encuentra
     */
    Optional<User> findByChatId(Long chatId);
    
    /**
     * Verifica si existe un usuario con el ID de chat especificado
     * @param chatId El ID del chat a verificar
     * @return true si existe, false si no
     */
    boolean existsByChatId(Long chatId);
}