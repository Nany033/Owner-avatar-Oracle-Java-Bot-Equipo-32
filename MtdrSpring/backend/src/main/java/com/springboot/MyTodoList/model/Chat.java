package com.springboot.MyTodoList.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "CHAT")
public class Chat {
    
    @Id
    @Column(name = "CHAT_ID")
    private Long chatId;
    
    @Column(name = "ADMIN_ID", nullable = false)
    private Long adminId = 0L;  // Valor predeterminado para evitar NULL
    
    @Column(name = "CREATED_AT")
    private Timestamp createdAt;
    
    // Constructor vacío
    public Chat() {
        this.adminId = 0L;
    }
    
    // Constructor con ID
    public Chat(Long chatId) {
        this.chatId = chatId;
        this.adminId = 0L;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    // Getters y setters
    public Long getChatId() {
        return chatId;
    }
    
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
    
    public Long getAdminId() {
        return adminId;
    }
    
    public void setAdminId(Long adminId) {
        this.adminId = adminId != null ? adminId : 0L;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Chat{" +
                "chatId=" + chatId +
                ", adminId=" + adminId +
                ", createdAt=" + createdAt +
                '}';
    }
}