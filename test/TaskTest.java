

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import tasks.*;
import managers.*;

class TaskTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    @Test
    public void taskEqualsTask() {
        Task task = taskManager.createTask(new Task("Вторая задача", Status.NEW, "Подробное описание"));
        Task secondTask = taskManager.getTask(task.getId());
        Assertions.assertEquals(task, secondTask, "Объекты не равны");
    }

    @Test
    public void epicEqualsEpic() {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        Epic secondEpic = taskManager.getEpic(epic.getId());
        Assertions.assertEquals(epic, secondEpic, "Эпики не равны");
    }

    @Test
    public void subtaskEqualsSubtask() {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager.createSubTask(new SubTask("Новая подзадача", Status.NEW, "Описание подзадачи", epic.getId()));
        SubTask secondSubTask = taskManager.getSubTask(subTask.getId());
        Assertions.assertEquals(subTask, secondSubTask, "Подзадачи не равны");
    }

    @Test
    public void epicAddEpic() {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        epic.setSubTask(epic.getId());
        for (int id : epic.getAllSubTasks()) {
            Assertions.assertEquals(id, epic.getId(), "ID совпадают");
        }
    }

    @Test
    public void subtaskIsEpic() {
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        taskManager.createSubTask(new SubTask("Новая подзадача", Status.NEW, "Описание подзадачи", epic.getId()));
        List<Integer> list = epic.getAllSubTasks();
        Assertions.assertNotEquals(list.get(0), epic.getId());
    }

    @Test
    public void managersEquals() {

        Task task = taskManager.createTask(new Task("Новая задача", Status.NEW, "Описание задачи"));
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
    public void giveTask() {
        TaskManager manager = Managers.getDefault();
        Task task = manager.createTask(new Task("Новая задача", Status.NEW, "Описание задачи"));
        Epic epic = manager.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        Task task1 = manager.getTask(task.getId());
        Epic epic1 = manager.getEpic(epic.getId());
        Assertions.assertNotNull(task1);
        Assertions.assertNotNull(epic1);
        Assertions.assertEquals(task, task1);
        Assertions.assertEquals(epic, epic1);
    }

    @Test
    public void checkOldVersionTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = taskManager.createTask(new Task("Новая задача", Status.NEW, "Описание задачи"));
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
        Task task = new Task("Новая задача", Status.NEW, "Описание задачи");
        manager.add(task);
        for (Task print : manager.getHistory()) {
            Assertions.assertNotNull(print);
        }
    }

    @Test
    public void removeTest() {
        HistoryManager manager = Managers.getDefaultHistory();
        Task task = new Task("Новая задача", Status.NEW, "Описание задачи");
        manager.add(task);
        manager.remove(task.getId());
        for (Task print : manager.getHistory()) {
            Assertions.assertNull(print);
        }
    }

    @Test
    public void checkIsEmpty() {
        HistoryManager manager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Новая задача", Status.NEW, "Описание задачи");
        taskManager.createTask(task);
        taskManager.getTask(task.getId());
        taskManager.deleteAllTasks();
        List<Task> list = manager.getHistory();
        Assertions.assertTrue(list.isEmpty());
    }
}