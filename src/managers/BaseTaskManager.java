package managers;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import java.util.*;

public class BaseTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks;
    protected Map<Integer, Epic> epics;
    protected Map<Integer, SubTask> subTasks;

    protected int id = 1;

    public BaseTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    private int generateId() {
        return id++;
    }


    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
            return task;
    }


    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(generateId());
        Epic epic = epics.get(subTask.getEpic());
        List<Integer> sTasks = epic.getAllSubTasks();
        if (sTasks.isEmpty()) {
            epic.setStartTime(subTask.getStartTime());
            epic.setEndTime(subTask.getStartTime().plus(subTask.getDuration()));
        } else {
            epic.setEndTime(epic.getEndTime().plus(subTask.getDuration()));
        }
        epic.setSubTask(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        calculateStatus(epic);
            return subTask;
    }


    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }


    public Task getTask(int id) {
        return tasks.get(id);

    }


    public SubTask getSubTask(int id) {
        return subTasks.get(id);

    }


    public Epic getEpic(int id) {
        return epics.get(id);
    }


    public void updateTask(Task task) {
        if (tasks.containsValue(task)) {
            tasks.put(task.getId(), task);
        }
    }


    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }


    public void updateSubTask(SubTask subTask) {
        int epicId = subTask.getEpic();
        Epic saved = epics.get(epicId);
        if (saved != null && subTasks.containsValue(subTask)) {
            subTasks.put(subTask.getId(), subTask);
            calculateStatus(saved);
        }
    }


    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }


    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }


    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }


    public void deleteTask(int id) {
        tasks.remove(id);
    }


    public void deleteSubTask(int id) {
        SubTask removeSubTask = subTasks.remove(id);
        int epicId = removeSubTask.getEpic();
        Epic saved = epics.get(epicId);
        saved.deleteSubTask(id);
        calculateStatus(saved);
    }


    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        List<Integer> removeSubTasks = epic.getAllSubTasks();
        for (int subTaskId : removeSubTasks) {
            subTasks.remove(subTaskId);
        }
    }


    public void deleteAllTasks() {
        tasks.clear();
    }


    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteAllSubTasks();
            calculateStatus(epic);
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    protected void calculateStatus(Epic epic) {
        SubTask subTask;
        int newTask = 0;
        int done = 0;
        int allSubTasks = 0;
        List<Integer> list = epic.getAllSubTasks();
        if (list.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        for (int id : list) {
            subTask = subTasks.get(id);
            if (subTask.getStatus() == Status.NEW) {
                newTask++;
                allSubTasks++;
            } else if (subTask.getStatus() == Status.DONE) {
                done++;
                allSubTasks++;
            } else {
                allSubTasks++;
            }
        }
        if (newTask == allSubTasks) {
            epic.setStatus(Status.NEW);
        } else if (done == allSubTasks) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager manager = (InMemoryTaskManager) o;
        return Objects.equals(tasks, manager.tasks) &&
                Objects.equals(subTasks, manager.subTasks) &&
                Objects.equals(epics, manager.epics) &&
                id == manager.id;
    }
}

