package managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import tasks.*;
import exceptions.*;


public class InMemoryTaskManager extends BaseTaskManager {
    protected HistoryManager history = Managers.getDefaultHistory();

    protected int id = 1;
    private int sort = 0;

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
        super.createTask(task);
        sort = 0;
        getPrioritizedTasks();
        if (checkTasks(task)) {
            return task;
        } else {
            System.out.println("Обнаружено пересечение. Задача не может быть добавлена");
            return null;
        }
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        sort = 0;
        getPrioritizedTasks();
        if (checkTasks(subTask)) {
            return subTask;
        } else {
            System.out.println("Обнаружено пересечение. Подзадача не может быть добавлена");
            return null;
        }
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        return epic;
    }

    @Override
    public Task getTask(int id) {
       Task task = super.getTask(id);
        history.add(task);
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        history.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        history.add(epic);
        return epic;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        history.remove(id);

    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        history.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        history.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        List<Integer> list = new ArrayList<>(tasks.keySet());
        history.clearHistory(list);
        super.deleteAllTasks();
    }

    @Override
    public void deleteAllSubTasks() {
        List<Integer> list = new ArrayList<>(subTasks.keySet());
        history.clearHistory(list);
        super.deleteAllSubTasks();
    }

    @Override
    public void deleteAllEpics() {
        List<Integer> list1 = new ArrayList<>(subTasks.keySet());
        List<Integer> list2 = new ArrayList<>(epics.keySet());
        history.clearHistory(list1);
        history.clearHistory(list2);
        super.deleteAllEpics();
    }

    public List<Task> getHistory() {
        return history.getHistory();
    }

    public Set<Task> getPrioritizedTasks() {
        Comparator<Task> comparator = new Comparator<>() {
            @Override
            public int compare(Task o1, Task o2) {
                Duration duration1 = Duration.between(o1.getStartTime(), LocalDateTime.now());
                Duration duration2 = Duration.between(o2.getStartTime(), LocalDateTime.now());
                return (int) duration1.toMinutes() - (int) duration2.toMinutes();
            }
        };
        Set<Task> tree = new TreeSet<>(comparator);
        if (sort == 0) {
            for (Task task : tasks.values()) {
                if (task.getStartTime() == null) {
                    continue;
                }
                tree.add(task);
            }
            for (SubTask subTask : subTasks.values()) {
                if (subTask.getStartTime() == null) {
                    continue;
                }
                tree.add(subTask);
            }
            sort = 1;
            return tree;
        } else {
            return tree;
        }
    }

    public boolean checkTasks(Task task) {
        Set<Task> set = getPrioritizedTasks().stream().filter(task1 ->
                !task1.getEndTime().isBefore(task.getStartTime())).collect(Collectors.toSet());
        return set.isEmpty();
    }
}

