package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subTasks = new ArrayList<>();
    private Type type = Type.EPIC;

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
}
