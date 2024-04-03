package managers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import tasks.*;
import exceptions.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private String file = "save.txt";

    public FileBackedTaskManager(String file) {
        this.file = file;
    }

    @Override
    public Task createTask(Task task) throws ManagerSaveException {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) throws ManagerSaveException {
        super.createSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public Epic createEpic(Epic epic) throws ManagerSaveException {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) throws ManagerSaveException {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Task getTask(int id) throws ManagerSaveException {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTask(int id) throws ManagerSaveException {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpic(int id) throws ManagerSaveException {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public void deleteTask(int id) throws ManagerSaveException {
        super.deleteTask(id);
        save();

    }

    @Override
    public void deleteSubTask(int id) throws ManagerSaveException {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) throws ManagerSaveException {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllTasks() throws ManagerSaveException {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() throws ManagerSaveException {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpics() throws ManagerSaveException {
        super.deleteAllEpics();
        save();
    }

    public void save() throws ManagerSaveException {
        try {
            Path path = Files.createFile(Paths.get("IdeaProjects/java-kanban", file));
            Writer writer = new FileWriter(path.toFile().getName(), true);
            writer.write(historyToString(history));
            for (Task task : super.getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (SubTask subTask : super.getAllSubTasks()) {
                writer.write(toString(subTask) + "\n");
            }
            for (Epic epic : super.getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка");
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) throws ManagerSaveException {
        return new FileBackedTaskManager(path.toFile().getName());
    }

    private String toString(Task task) {
        if (epics.get(task.getId()) != null) {
            return String.format("%d,%s,%s,%s,%s %n", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription());
        } else if (subTasks.get(task.getId()) != null) {
            SubTask subTask = subTasks.get(task.getId());
            return String.format("%d,%s,%s,%s,%s,%d %n", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), subTask.getEpic());
        } else {
            return String.format("%d,%s,%s,%s,%s %n", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription());
        }
    }

    private Task fromString(String value) {
        Task task = new Task(null, null, null, null);
        List<String> values = List.of((value.split(",")));
        for (int i = 0; i < values.size(); i++) {
            switch (i) {
                case 0:
                    task.setId(Integer.parseInt(values.get(i)));
                    break;
                case 2:
                    task.setName(values.get(i));
                    break;
                case 3:
                    if (values.get(i).equals("NEW")) {
                        task.setStatus(Status.NEW);
                    } else if (values.get(i).equals("IN_PROGRESS")) {
                        task.setStatus(Status.IN_PROGRESS);
                    } else {
                        task.setStatus(Status.DONE);
                    }
                    break;
                case 4:
                    task.setDescription(values.get(i));
                    break;
                case 5:
                    SubTask subTask = new SubTask(task.getName(), task.getStatus(), task.getType(), task.getDescription(),
                            Integer.parseInt(values.get(i)));
                    return subTask;

            }
        }
        return task;
    }

    private static String historyToString(HistoryManager manager) {
        String value = "";
        for (Task task : manager.getHistory()) {
            value += task.getId() + ",";
        }
        return value;
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> tasksId = new ArrayList<>();
        String[] text = value.split(",");
        for (int i = 0; i < text.length; i++) {
            tasksId.add(Integer.parseInt(text[i]));
        }
        return tasksId;
    }
}
