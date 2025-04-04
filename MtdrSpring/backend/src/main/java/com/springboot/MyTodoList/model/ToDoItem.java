package com.springboot.MyTodoList.model;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/*
    Representation of the TODOITEM table that exists already
    in the autonomous database.
 */
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

    // Fields to be developed later
    // @Column(name = "CREATOR_ID")
    // private int creator_id;
    
    // @Column(name = "TASK_DATE")
    // private OffsetDateTime task_date;
    
    @Column(name = "SPRINT_ID")
    private Integer sprint_id;
    
    @Column(name = "REAL_TIME")
    private Integer real_time;

    public ToDoItem() {
    }

    public ToDoItem(int ID, String description, OffsetDateTime creation_ts, boolean done, OffsetDateTime deadline, int estimated_hours, int sprint_id) {
        this.ID = ID;
        this.description = description;
        this.creation_ts = creation_ts;
        this.done = done;
        this.deadline = deadline;
        this.estimated_hours = estimated_hours;
        this.sprint_id = sprint_id;

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

    // Fixed method names to match the field name (lowercase 'h')
    public int getEstimated_hours() {
        return estimated_hours;
    }

    public void setEstimated_hours(int estimated_hours) {
        this.estimated_hours = estimated_hours;
    }

    public Integer getReal_time() {
        return real_time;
    }

    public void setReal_time(Integer real_time) {
        this.real_time = real_time;
    }

    // Methods for future columns are commented out
    /*
    public int getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    public OffsetDateTime getTask_date() {
        return task_date;
    }

    public void setTask_date(OffsetDateTime task_date) {
        this.task_date = task_date;
    }



    public Double getReal_time() {
        return real_time;
    }

    public void setReal_time(Double real_time) {
        this.real_time = real_time;
    }
    */

    @Override
    public String toString() {
        return "ToDoItem{" +
                "ID=" + ID +
                ", description='" + description + '\'' +
                ", creation_ts=" + creation_ts +
                ", deadline=" + deadline +
                ", done=" + done +
                ", estimated_hours=" + estimated_hours +
                ", real_time=" + real_time +
                ", sprint_id=" + sprint_id +
                // Future columns commented
                // ", creator_id=" + creator_id +
                // ", task_date=" + task_date +
                '}';
    }
}