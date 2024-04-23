package managers;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import tasks.*;


public class InMemoryTaskManager implements TaskManager {
    protected HistoryManager history = Managers.getDefaultHistory();
    protected Map<Integer, Task> tasks;
    protected Map<Integer, Epic> epics;
    protected Map<Integer, SubTask> subTasks;

    protected int id = 1;
    private Set<Task> tree = new TreeSet<>((Task o1, Task o2) -> {
        Duration duration1 = Duration.between(o1.getStartTime(), LocalDateTime.now());
        Duration duration2 = Duration.between(o2.getStartTime(), LocalDateTime.now());
        return (int) duration1.toMinutes() - (int) duration2.toMinutes();
    });

    private int generateId() {
        return id++;
    }

    @Override
    public Task createTask(Task task) {
        if (checkTasks(task)) {
            task.setId(generateId());
            tasks.put(task.getId(), task);
            setPrioritizedTasks(task);
            return task;
        } else {
            System.out.println("Обнаружено пересечение. Задача не может быть добавлена");
            return null;
        }
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        if (checkTasks(subTask)) {
            subTask.setId(generateId());
            Epic epic = epics.get(subTask.getEpic());
            epic.setSubTask(subTask.getId());
            subTasks.put(subTask.getId(), subTask);
            setPrioritizedTasks(subTask);
            calculateTime(epic);
            return subTask;
        } else {
            System.out.println("Обнаружено пересечение. Подзадача не может быть добавлена");
            return null;
        }
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
            calculateTime(saved);
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
        calculateTime(saved);
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
            calculateTime(epic);
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

    private void setPrioritizedTasks(Task task) {
        tree.add(task);
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(tree);
    }

    private boolean checkTasks(Task task) {
        Set<Task> set = getPrioritizedTasks().stream().filter(task1 ->
                !task1.getEndTime().isBefore(task.getStartTime())).collect(Collectors.toSet());
        if (!set.isEmpty()) {
            return false;
        } else {
            set = getPrioritizedTasks().stream().filter(task1 ->
                    !task1.getStartTime().isAfter(task.getEndTime())).collect(Collectors.toSet());
            return set.isEmpty();
        }
    }

    public void calculateTime(Epic epic) {
        List<SubTask> sTasks = getAllSubTasks().stream().filter(subTask -> subTask.getEpic() == epic.getId())
                .sorted((SubTask subTask1, SubTask subTask2) -> {
                    Duration duration = Duration.between(subTask1.getStartTime(), LocalDateTime.now());
                    Duration duration1 = Duration.between(subTask2.getStartTime(), LocalDateTime.now());
                    return (int) duration.toMinutes() - (int) duration1.toMinutes();
                }).collect(Collectors.toList());
        epic.setStartTime(sTasks.get(0).getStartTime());
        epic.setEndTime(sTasks.get(sTasks.size() - 1).getEndTime());
        calculateStatus(epic);
    }

    private void calculateStatus(Epic epic) {
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