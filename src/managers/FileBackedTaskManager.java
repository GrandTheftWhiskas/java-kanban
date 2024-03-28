package managers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import tasks.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save(task);
        return task;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save(subTask);
        return subTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save(epic);
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save(task);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save(subTask);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save(epic);
    }


    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
    public void save(Task task) {
        try {
            try (Writer fileWriter = new FileWriter("save.txt")) {
               Path path = Files.createFile(Paths.get("C:/Users/admin/IdeaProjects/java-kanban","save.txt"));
               fileWriter.write(toString(task));
            } catch (IOException e) {
                throw new ManagerSaveException("Произошла ошибка");
            }
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }
    private String toString(Task task) {
        if (epics.get(task.getId()) != null) {
            return String.format("%d,%s,%s,%s,%s %n", task.getId(), Type.EPIC, task.getName(),
                    task.getStatus(), task.getDescription());
        } else if (subTasks.get(task.getId()) != null) {
            SubTask subTask = subTasks.get(task.getId());
            return String.format("%d,%s,%s,%s,%s,%d %n", task.getId(), Type.SUBTASK, task.getName(),
                    task.getStatus(), task.getDescription(), subTask.getEpic());
        } else if (tasks.get(task.getId()) != null) {
            return String.format("%d,%s,%s,%s,%s %n", task.getId(), Type.TASK, task.getName(),
                    task.getStatus(), task.getDescription());
        } else {
            return "Объект не найден";
        }
    }

    private Task fromString(String value) {
        Task task = new Task(null, null, null);
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
                    SubTask subTask = new SubTask(task.getName(), task.getStatus(), task.getDescription(),
                            Integer.parseInt(values.get(i)));
                    return subTask;

            }
        }
        return task;
    }

    private static String historyToString(HistoryManager manager) {
       String value = "";
       List<Task> history = manager.getHistory();
       for (Task task : history) {
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
