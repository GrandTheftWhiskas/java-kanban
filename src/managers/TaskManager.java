package managers;
import tasks.*;

import java.util.ArrayList;
import exceptions.*;

public interface TaskManager {
    Task createTask(Task task) throws ManagerSaveException;

    SubTask createSubTask(SubTask subTask) throws ManagerSaveException;

    Epic createEpic(Epic epic) throws ManagerSaveException;

    Task getTask(int id) throws ManagerSaveException;

    SubTask getSubTask(int id) throws ManagerSaveException;

    Epic getEpic(int id) throws ManagerSaveException;

    void updateTask(Task task) throws ManagerSaveException;

    void updateEpic(Epic epic) throws ManagerSaveException;

    void updateSubTask(SubTask subTask) throws ManagerSaveException;

    ArrayList<Task> getAllTasks();

    ArrayList<SubTask> getAllSubTasks();

    ArrayList<Epic> getAllEpics();

    void deleteTask(int id) throws ManagerSaveException;

    void deleteSubTask(int id) throws ManagerSaveException;

    void deleteEpic(int id) throws ManagerSaveException;

    void deleteAllTasks() throws ManagerSaveException;

    void deleteAllSubTasks() throws ManagerSaveException;

    void deleteAllEpics() throws ManagerSaveException;
}
