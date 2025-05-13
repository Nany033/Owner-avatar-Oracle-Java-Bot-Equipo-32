package com.springboot.MyTodoList.model;

import java.time.OffsetDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "TODOITEM")
public class ToDoItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
    
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Column(name = "CREATION_TS")
    private OffsetDateTime creation_ts;
    
    @Column(name = "DONE")
    private boolean done;
    
    @Column(name = "DEADLINE")
    private OffsetDateTime deadline;
    
    // Fixed naming to be consistent
    @Column(name = "ESTIMATED_HOURS")
    private int estimated_hours;

    @Column(name = "USER_ID")
    private Integer user_id;
    
    @Column(name = "SPRINT_ID")
    private Integer sprint_id;
    
    @Column(name = "REAL_TIME")
    private Integer real_time;
    
    @Column(name = "COMPLETION_DATE")
    private OffsetDateTime completion_date;

    public ToDoItem() {
    }

    public ToDoItem(int ID, String description, OffsetDateTime creation_ts, boolean done, OffsetDateTime deadline, int estimated_hours, Integer user_id, int sprint_id, OffsetDateTime completion_date) {
        this.ID = ID;
        this.description = description;
        this.creation_ts = creation_ts;
        this.done = done;
        this.deadline = deadline;
        this.estimated_hours = estimated_hours;
        this.user_id = user_id;
        this.sprint_id = sprint_id;
        this.completion_date = completion_date;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreation_ts() {
        return creation_ts;
    }

    public void setCreation_ts(OffsetDateTime creation_ts) {
        this.creation_ts = creation_ts;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public OffsetDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(OffsetDateTime deadline) {
        this.deadline = deadline;
    }

    public int getEstimated_hours() {
        return estimated_hours;
    }

    public void setEstimated_hours(int estimated_hours) {
        this.estimated_hours = estimated_hours;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getReal_time() {
        return real_time;
    }

    public void setReal_time(Integer real_time) {
        this.real_time = real_time;
    }

    public Integer getSprint_id() {
        return sprint_id;
    }

    public void setSprint_id(Integer sprint_id) {
        this.sprint_id = sprint_id;
    }

    public OffsetDateTime getCompletion_date() {
        return completion_date;
    }

    public void setCompletion_date(OffsetDateTime completion_date) {
        this.completion_date = completion_date;
    }
    
    @Override
    public String toString() {
        return "ToDoItem{" +
                "ID=" + ID +
                ", description='" + description + '\'' +
                ", creation_ts=" + creation_ts +
                ", deadline=" + deadline +
                ", done=" + done +
                ", estimated_hours=" + estimated_hours +
                ", user_id=" + user_id +
                ", real_time=" + real_time +
                ", sprint_id=" + sprint_id +
                ", user_id='" + user_id + '\'' +
                ", completion_date=" + completion_date +
                '}';
    }
}