package com.springboot.MyTodoList.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
public class User {
    
    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "TELEGRAM_CHAT_ID")
    private Long telegramChatId;
    
    public User() {
    }
    
    public User(String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
}