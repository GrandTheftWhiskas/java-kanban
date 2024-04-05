package managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import tasks.*;
import exceptions.*;


public class InMemoryTaskManager implements TaskManager {
    protected HistoryManager history = Managers.getDefaultHistory();
    protected Map<Integer, Task> tasks;
    protected Map<Integer, Epic> epics;
    protected Map<Integer, SubTask> subTasks;

    protected int id = 1;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    private int generateId() {
        return id++;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(generateId());
        Epic epic = epics.get(subTask.getEpic());
        epic.setSubTask(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        calculateStatus(epic);
        return subTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        history.add(task);
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        history.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        history.add(epic);
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsValue(task)) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int epicId = subTask.getEpic();
        Epic saved = epics.get(epicId);
        if (saved != null && subTasks.containsValue(subTask)) {
            subTasks.put(subTask.getId(), subTask);
            calculateStatus(saved);
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        history.remove(id);

    }

    @Override
    public void deleteSubTask(int id) {
        SubTask removeSubTask = subTasks.remove(id);
        history.remove(id);
        int epicId = removeSubTask.getEpic();
        Epic saved = epics.get(epicId);
        saved.deleteSubTask(id);
        calculateStatus(saved);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        history.remove(id);
        List<Integer> removeSubTasks = epic.getAllSubTasks();
        for (int subTaskId : removeSubTasks) {
            subTasks.remove(subTaskId);
        }
    }

    @Override
    public void deleteAllTasks() {
        List<Integer> list = new ArrayList<>(tasks.keySet());
        history.clearHistory(list);
        tasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        List<Integer> list = new ArrayList<>(subTasks.keySet());
        history.clearHistory(list);
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteAllSubTasks();
            calculateStatus(epic);
        }
    }

    @Override
    public void deleteAllEpics() {
        List<Integer> list1 = new ArrayList<>(subTasks.keySet());
        List<Integer> list2 = new ArrayList<>(epics.keySet());
        history.clearHistory(list1);
        history.clearHistory(list2);
        epics.clear();
        subTasks.clear();
    }

    public List<Task> getHistory() {
        return history.getHistory();
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

