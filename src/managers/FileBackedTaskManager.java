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

    public void save() {
        try {
            Writer writer = new FileWriter(file);
            writer.write("id,type,name,status,description,epic \n");
            for (Task task : super.getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (SubTask subTask : super.getAllSubTasks()) {
                writer.write(toString(subTask) + "\n");
            }
            for (Epic epic : super.getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            writer.write(" " + "\n");
            writer.write(historyToString(history));
            writer.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
               String line = reader.readLine();
               if (!line.isBlank()) {
                   fileBackedTaskManager.createTask(fileBackedTaskManager.fromString(line));
               } else {
                   break;
               }
               while (reader.ready()) {
                   String line1 = reader.readLine();
                   FileBackedTaskManager.historyFromString(line1);
               }
               reader.close();
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка");
        }
        return fileBackedTaskManager;
    }

    private String toString(Task task) {
        if (task.getType().equals(Type.EPIC)) {
            return String.format("%d,%s,%s,%s,%s %n", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription());
        } else if (task.getType().equals(Type.SUBTASK)) {
            SubTask subTask = subTasks.get(task.getId());
            return String.format("%d,%s,%s,%s,%s,%d %n", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), subTask.getEpic());
        } else {
            return String.format("%d,%s,%s,%s,%s %n", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription());
        }
    }

    private Task fromString(String value) {
        List<String> values = List.of((value.split(",")));
            if (values.get(1).equals("SUBTASK")) {
                SubTask subTask = new SubTask(null,null,null, null, 0);
                subTask.setId(Integer.parseInt(values.get(0)));
                subTask.setType(Type.SUBTASK);
                subTask.setName(values.get(2));
                subTask.setDescription(values.get(4));
                subTask.setEpic(Integer.parseInt(values.get(5)));
                if (values.get(3).equals("IN_PROGRESS")) {
                    subTask.setStatus(Status.IN_PROGRESS);
                } else if (values.get(3).equals("DONE")) {
                    subTask.setStatus(Status.DONE);
                } else {
                    subTask.setStatus(Status.NEW);
                }
                return subTask;
            } else if (values.get(1).equals("EPIC")) {
                Epic epic = new Epic(null, null, null, null);
                epic.setId(Integer.parseInt(values.get(0)));
                epic.setType(Type.EPIC);
                epic.setName(values.get(2));
                epic.setDescription(values.get(4));
                if (values.get(3).equals("IN_PROGRESS")) {
                    epic.setStatus(Status.IN_PROGRESS);
                } else if (values.get(3).equals("DONE")) {
                    epic.setStatus(Status.DONE);
                } else {
                    epic.setStatus(Status.NEW);
                }
                return epic;
            } else {
                Task task = new Task(null, null, null, null);
                task.setId(Integer.parseInt(values.get(0)));
                task.setType(Type.TASK);
                task.setName(values.get(2));
                task.setDescription(values.get(4));
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

    private static String historyToString(HistoryManager manager) {
        StringBuilder value = new StringBuilder("");
        for (Task task : manager.getHistory()) {
            if (task.getType().equals(Type.SUBTASK)) {
                SubTask subTask = subTasks.get(task.getId());
                value.append(task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus()
                        + task.getDescription() + "," + subTask.getEpic() + "\n");
            } else {
                value.append(task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus()
                        + task.getDescription() + "\n");
            }
        }
        return value.toString();
    }

    private static void historyFromString(String value) {
        HistoryManager manager = new InMemoryHistoryManager();
        String[] text = value.split(",");
        for (int i = 0; i < text.length; i++) {
            manager.add(tasks.get(Integer.parseInt(text[0])));
        }
    }
}
