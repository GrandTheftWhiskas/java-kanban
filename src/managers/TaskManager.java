package managers;
import tasks.*;

import java.util.ArrayList;
import exceptions.*;

public interface TaskManager {
    Task createTask(Task task);

    SubTask createSubTask(SubTask subTask);

    Epic createEpic(Epic epic);

    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    ArrayList<Task> getAllTasks();

    ArrayList<SubTask> getAllSubTasks();

    ArrayList<Epic> getAllEpics();

    void deleteTask(int id);

    void deleteSubTask(int id);

    void deleteEpic(int id);

    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics();
}
