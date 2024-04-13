package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subTasks = new ArrayList<>();
    private Type type = Type.EPIC;
    private String name;
    private Status status;
    private String description;
    private LocalDateTime startTime;
    private Duration duration;
    private LocalDateTime endTime;

    public Epic(String name, Status status, String description) {
         super(name, status, description);
    }

    public void setSubTask(int subTask) {
           subTasks.add(subTask);
    }

    public void deleteSubTask(int id) {
        Integer newId = Integer.valueOf(id);
        subTasks.remove(newId);
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
    }

    public List<Integer> getAllSubTasks() {
        return subTasks;
    }

    @Override
    public Type getType() {
        return type;
    }

    public void setDuration(Duration duration) {
         this.duration = duration;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        setDuration(Duration.between(startTime, endTime));
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
