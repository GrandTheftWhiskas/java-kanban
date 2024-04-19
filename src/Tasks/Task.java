package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private int id;
    private String name;

    private Type type = Type.TASK;
    private LocalDateTime startTime;
    private Duration duration;
    private Status status;
    private String description;

    public Task(String name, Status status, String description) {
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public Task(String name, Status status, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return "Tasks.Task{" +
                "id =" + id + '\'' +
                ", name =" + name + '\'' +
                ", status =" + status + '\'' +
                ", description =" + description + '\'' +
                '}';

    }
}
