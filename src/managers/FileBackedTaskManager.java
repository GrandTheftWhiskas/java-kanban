package managers;
import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import tasks.*;
import exceptions.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();

    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    private void save() {
        try {
            Writer writer = new FileWriter(file);
            writer.write("id,type,name,status,description,startTime,duration,epic \n");
            for (Task task : super.getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (SubTask subTask : super.getAllSubTasks()) {
                writer.write(toString(subTask) + "\n");
            }
            for (Epic epic : super.getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            writer.write("  " + "\n");
            writer.write(historyToString(history));
            writer.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            final String csv = Files.readString(file.toPath());
            final String[] lines = csv.split("\n");
            int counterId = 0;
            int iteration = 0;
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (!line.isBlank()) {
                    Task task = fileBackedTaskManager.fromString(line);
                    if (task.getType() == Type.TASK) {
                        fileBackedTaskManager.tasks.put(task.getId(), task);
                    } else if (task.getType() == Type.EPIC) {
                        fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                    } else {
                        fileBackedTaskManager.subTasks.put(task.getId(), (SubTask) task);
                    }
                    if (counterId < task.getId()) {
                        counterId = task.getId();
                    }
                } else {
                    iteration = i + 1;
                    break;
                }
            }

            while (iteration != lines.length) {
                String line = lines[iteration];
                ArrayList<Integer> values = fileBackedTaskManager.historyFromString(line);
                for (int id : values) {
                    if (fileBackedTaskManager.tasks.get(id) != null) {
                        fileBackedTaskManager.history.add(fileBackedTaskManager.tasks.get(id));
                    } else if (fileBackedTaskManager.epics.get(id) != null) {
                        fileBackedTaskManager.history.add(fileBackedTaskManager.epics.get(id));
                    } else {
                        fileBackedTaskManager.history.add(fileBackedTaskManager.subTasks.get(id));
                    }
                }
                iteration++;
            }

            fileBackedTaskManager.id = counterId;
            for (Map.Entry<Integer, SubTask> e : fileBackedTaskManager.subTasks.entrySet()) {
                final SubTask subTask = e.getValue();
                final Epic epic = fileBackedTaskManager.epics.get(subTask.getEpic());
                epic.setSubTask(subTask.getId());
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка");
        }
        return fileBackedTaskManager;
    }

    private String toString(Task task) {
        if (task.getType().equals(Type.EPIC)) {
            return String.format("%d,%s,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration().toMinutes());
        } else if (task.getType().equals(Type.SUBTASK)) {
            SubTask subTask = subTasks.get(task.getId());
            return String.format("%d,%s,%s,%s,%s,%s,%s,%d", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(),
                    subTask.getStartTime(), subTask.getDuration().toMinutes(), subTask.getEpic());
        } else {
            return String.format("%d,%s,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration().toMinutes());
        }
    }

    private Task fromString(String value) {
        List<String> values = List.of(value.split(","));
        if (values.get(1).equals("SUBTASK")) {
            SubTask subTask = new SubTask(null,null,null, null, null, 0);
            subTask.setId(Integer.parseInt(values.get(0)));
            subTask.setName(values.get(2));
            subTask.setDescription(values.get(4));
            subTask.setStartTime(LocalDateTime.parse(values.get(5)));
            subTask.setDuration(Duration.ofMinutes(Integer.parseInt(values.get(6))));
            subTask.setEpic(Integer.parseInt(values.get(7)));
            if (values.get(3).equals("IN_PROGRESS")) {
                subTask.setStatus(Status.IN_PROGRESS);
            } else if (values.get(3).equals("DONE")) {
                subTask.setStatus(Status.DONE);
            } else {
                subTask.setStatus(Status.NEW);
            }
            return subTask;
        } else if (values.get(1).equals("EPIC")) {
            Epic epic = new Epic(null, null, null);
            epic.setId(Integer.parseInt(values.get(0)));
            epic.setName(values.get(2));
            epic.setDescription(values.get(4));
            epic.setStartTime(LocalDateTime.parse(values.get(5)));
            epic.setDuration(Duration.ofMinutes(Integer.parseInt(values.get(6))));
            if (values.get(3).equals("IN_PROGRESS")) {
                epic.setStatus(Status.IN_PROGRESS);
            } else if (values.get(3).equals("DONE")) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.NEW);
            }
            return epic;
        } else {
            Task task = new Task(null, null, null, null, null);
            task.setId(Integer.parseInt(values.get(0)));
            task.setName(values.get(2));
            task.setDescription(values.get(4));
            task.setStartTime(LocalDateTime.parse(values.get(5)));
            task.setDuration(Duration.ofMinutes(Integer.parseInt(values.get(6))));
            if (values.get(3).equals("IN_PROGRESS")) {
                task.setStatus(Status.IN_PROGRESS);
            } else if (values.get(3).equals("DONE")) {
                task.setStatus(Status.DONE);
            } else {
                task.setStatus(Status.NEW);
            }
            return task;
        }
    }

    private String historyToString(HistoryManager manager) {
        final List<Task> history = manager.getHistory();
        if (history.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(history.get(0).getId());
        for (int i = 1; i < history.size(); i++) {
            Task task = history.get(i);
            sb.append(",");
            sb.append(task.getId());
        }
        return sb.toString();
    }

    private ArrayList<Integer> historyFromString(String value) {
        final String[] values = value.split(",");
        final ArrayList<Integer> ids = new ArrayList<>(values.length);
        for (String id : values) {
            ids.add(Integer.parseInt(id));
        }
        return ids;
    }
}
