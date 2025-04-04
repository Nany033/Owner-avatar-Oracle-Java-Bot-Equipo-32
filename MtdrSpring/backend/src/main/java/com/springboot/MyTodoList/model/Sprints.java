package com.springboot.MyTodoList.model;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SPRINTS")
public class Sprints {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "SPRINT_ID")
        private int sprint_id;

        @Column(name = "TIME_START")
        private OffsetDateTime time_start;

        @Column(name = "TIME_END")
        private OffsetDateTime time_end;

    public Sprints() {
    }

    public Sprints(int sprint_id, OffsetDateTime time_start, OffsetDateTime time_end) {
        this.sprint_id = sprint_id;
        this.time_start = time_start;
        this.time_end = time_end;
    }

    public int getSprint_id() {
        return sprint_id;
    }

    public int findById(int sprint_id) {
        return this.sprint_id;
    }

    public void setSprint_id(int sprint_id) {
        this.sprint_id = sprint_id;
    }

    public OffsetDateTime getTime_start() {
        return time_start;
    }

    public void setTime_start(OffsetDateTime time_start) {
        this.time_start = time_start;
    }

    public OffsetDateTime getTime_end() {
        return time_end;
    }

    public void setTime_end(OffsetDateTime time_end) {
        this.time_end = time_end;
    }

    @Override
    public String toString() {
        return "Sprints [sprint_id=" + sprint_id + ", time_start=" + time_start + ", time_end=" + time_end + "]";
    }
}
