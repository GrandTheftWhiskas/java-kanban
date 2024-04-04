

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import tasks.*;
import managers.*;
import exceptions.*;

import javax.imageio.IIOException;

class TaskTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void taskEqualsTask() throws ManagerSaveException {
        Task task = taskManager.createTask(new Task("Вторая задача", Status.NEW, Type.TASK, "Подробное описание"));
        Task secondTask = taskManager.getTask(task.getId());
        Assertions.assertEquals(task, secondTask, "Объекты не равны");
    }

    @Test
    public void epicEqualsEpic() throws ManagerSaveException {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", Status.NEW, Type.TASK, "Описание эпика"));
        Epic secondEpic = taskManager.getEpic(epic.getId());
        Assertions.assertEquals(epic, secondEpic, "Эпики не равны");
    }

    @Test
    public void subtaskEqualsSubtask() throws ManagerSaveException {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", Status.NEW, Type.TASK, "Описание эпика"));
        SubTask subTask = taskManager.createSubTask(new SubTask("Новая подзадача", Status.NEW, Type.TASK, "Описание подзадачи", epic.getId()));
        SubTask secondSubTask = taskManager.getSubTask(subTask.getId());
        Assertions.assertEquals(subTask, secondSubTask, "Подзадачи не равны");
    }

    @Test
    public void epicAddEpic() throws ManagerSaveException {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", Status.NEW, Type.TASK, "Описание эпика"));
        epic.setSubTask(epic.getId());
        for (int id : epic.getAllSubTasks()) {
            Assertions.assertEquals(id, epic.getId(), "ID совпадают");
        }
    }

    @Test
    public void subtaskIsEpic() throws ManagerSaveException {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", Status.NEW, Type.TASK, "Описание эпика"));
        taskManager.createSubTask(new SubTask("Новая подзадача", Status.NEW, Type.TASK, "Описание подзадачи", epic.getId()));
        List<Integer> list = epic.getAllSubTasks();
        Assertions.assertNotEquals(list.get(0), epic.getId());
    }

    @Test
    public void managersEquals() throws ManagerSaveException {

        Task task = taskManager.createTask(new Task("Новая задача", Status.NEW, Type.TASK, "Описание задачи"));
        HistoryManager manager1 = new InMemoryHistoryManager();
        manager1.add(task);
        List<Task> list1 = manager1.getHistory();
        HistoryManager manager2 = Managers.getDefaultHistory();
        manager2.add(task);
        List<Task> list2 = manager2.getHistory();
        Assertions.assertNotNull(list1, "Cписок пустой");
        Assertions.assertNotNull(list2, "Cписок пустой");
        Assertions.assertEquals(list1, list2, "Cписки не равны");
    }

    @Test
    public void giveTask() throws ManagerSaveException {
        TaskManager manager = Managers.getDefault();
        Task task = manager.createTask(new Task("Новая задача", Status.NEW, Type.TASK, "Описание задачи"));
        Epic epic = manager.createEpic(new Epic("Новый эпик", Status.NEW, Type.TASK, "Описание эпика"));
        Task task1 = manager.getTask(task.getId());
        Epic epic1 = manager.getEpic(epic.getId());
        Assertions.assertNotNull(task1);
        Assertions.assertNotNull(epic1);
        Assertions.assertEquals(task, task1);
        Assertions.assertEquals(epic, epic1);
    }

    @Test
    public void checkOldVersionTask() throws ManagerSaveException {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = taskManager.createTask(new Task("Новая задача", Status.NEW, Type.TASK, "Описание задачи"));
        Task task1 = taskManager.getTask(task.getId());
        historyManager.add(task);
        List<Task> list = historyManager.getHistory();
        task = list.get(0);
        Assertions.assertEquals(task, task1);
    }
    // 6-ой спринт

    @Test
    public void addTest() {
        HistoryManager manager = Managers.getDefaultHistory();
        Task task = new Task("Новая задача", Status.NEW, Type.TASK, "Описание задачи");
        manager.add(task);
        for (Task print : manager.getHistory()) {
            Assertions.assertNotNull(print);
        }
    }

    @Test
    public void removeTest() {
        HistoryManager manager = Managers.getDefaultHistory();
        Task task = new Task("Новая задача", Status.NEW, Type.TASK, "Описание задачи");
        manager.add(task);
        manager.remove(task.getId());
        for (Task print : manager.getHistory()) {
            Assertions.assertNull(print);
        }
    }

    @Test
    public void checkIsEmpty() throws ManagerSaveException {
        HistoryManager manager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Новая задача", Status.NEW, Type.TASK, "Описание задачи");
        taskManager.createTask(task);
        taskManager.getTask(task.getId());
        taskManager.deleteAllTasks();
        List<Task> list = manager.getHistory();
        Assertions.assertTrue(list.isEmpty());
    }
    // 7 спринт

    @Test
    public void saveAndDownloadEmptyFile() {
        try {
            File file = File.createTempFile("vrem", null, new File("java-kanban/vrem.txt"));
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            fileBackedTaskManager.save();
            FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
            Assertions.assertNull(fileBackedTaskManager1);
        } catch (IOException e) {
            System.out.println("Ошибка");
        }
    }

    @Test
    public void saveAndDownloadNotEmptyFile() {
        try {
            File file = File.createTempFile("vrem", null, new File("java-kanban/vrem.txt"));
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            fileBackedTaskManager.createTask(
                    new Task("Новая задача", Status.NEW, Type.TASK, "Описание задачи"));
            fileBackedTaskManager.createTask(
                    new Task("Вторая задача", Status.NEW, Type.TASK, "Описание задачи"));
            fileBackedTaskManager.createTask(
                    new Task("Еще одна задача", Status.NEW, Type.TASK, "Описание задачи"));
            FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
            Assertions.assertNotNull(fileBackedTaskManager1);
        } catch (IOException e) {
            System.out.println("Ошибка");
        }
    }
}