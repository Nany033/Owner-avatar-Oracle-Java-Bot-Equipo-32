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
    
    @Column(name = "NAME")
    private String name;
    
    @Column(name = "ROL")
    private String rol;
    
    @Column(name = "EQUIPO_ID")
    private Integer equipoId;
    
    @Column(name = "CHAT_ID")
    private Long chatId;
    
    // Constructor vacío requerido por JPA
    public User() {
    }
    
    // Constructor con ID
    public User(String userId) {
        this.userId = userId;
    }
    
    // Constructor completo
    public User(String userId, String name, String rol, Integer equipoId, Long chatId) {
        this.userId = userId;
        this.name = name;
        this.rol = rol;
        this.equipoId = equipoId;
        this.chatId = chatId;
    }
    
    // Getters y setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public Integer getEquipoId() {
        return equipoId;
    }
    
    public void setEquipoId(Integer equipoId) {
        this.equipoId = equipoId;
    }
    
    public Long getChatId() {
        return chatId;
    }
    
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", rol='" + rol + '\'' +
                ", equipoId=" + equipoId +
                ", chatId=" + chatId +
                '}';
    }
}