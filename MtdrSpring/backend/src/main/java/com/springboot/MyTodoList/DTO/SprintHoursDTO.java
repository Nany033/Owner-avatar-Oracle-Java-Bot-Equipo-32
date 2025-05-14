package com.springboot.MyTodoList.DTO;

public class SprintHoursDTO {
    private int sprintId;
    int estimated_hours;
    private int totalHours;

    public SprintHoursDTO(int sprintId, int estimated_hours, int totalHours) {
        this.sprintId = sprintId;
        this.estimated_hours = estimated_hours;
        this.totalHours = totalHours;
    }

    public int getSprintId() {
        return sprintId;
    }

    public void setSprintId(int sprintId) {
        this.sprintId = sprintId;
    }

    public int getEstimated_hours() {
        return estimated_hours;
    }
    public void setEstimated_hours(int estimated_hours) {
        this.estimated_hours = estimated_hours;
    }
    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }
}
