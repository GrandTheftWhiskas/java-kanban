import managers.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerTest<T extends TaskManager> {
    TaskManager taskManager = new BaseTaskManager();
    InMemoryTaskManager taskManager1 = new InMemoryTaskManager();
    @Test
    public void createGetAndDeleteTasksTest() {
        Task task = taskManager.createTask(new Task("Новая задача", Status.NEW,
                "Описание задачи", LocalDateTime.now(), Duration.ofMinutes(15)));
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));

        Task task1 = taskManager.getTask(task.getId());
        SubTask subTask1 = taskManager.getSubTask(subTask.getId());
        Epic epic1 = taskManager.getEpic(epic.getId());

        taskManager.deleteTask(task.getId());
        taskManager.deleteSubTask(subTask.getId());
        taskManager.deleteEpic(epic.getId());

        Task task2 = taskManager.getTask(task.getId());
        SubTask subTask2 = taskManager.getSubTask(subTask.getId());
        Epic epic2 = taskManager.getEpic(epic.getId());
        Assertions.assertNotNull(task);
        Assertions.assertNotNull(epic);
        Assertions.assertEquals(epic.getStatus(), Status.NEW);
        Assertions.assertNotNull(subTask);
        Assertions.assertNotNull(subTask.getEpic());
        Assertions.assertNotNull(task1);
        Assertions.assertNotNull(subTask1);
        Assertions.assertNotNull(epic1);
        Assertions.assertNull(task2);
        Assertions.assertNull(subTask2);
        Assertions.assertNull(epic2);
    }

    @Test
    public void getAllTasksAndDeleteAllTasksTest() {
        Task task = taskManager.createTask(new Task("Новая задача", Status.NEW,
                "Описание задачи", LocalDateTime.now(), Duration.ofMinutes(15)));
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));

        List<Task> tasks = taskManager.getAllTasks();
        List<SubTask> subTasks = taskManager.getAllSubTasks();
        List<Epic> epics = taskManager.getAllEpics();

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();

        List<Task> tasks2 = taskManager.getAllTasks();
        List<SubTask> subTasks2 = taskManager.getAllSubTasks();
        List<Epic> epics2 = taskManager.getAllEpics();

        Assertions.assertNotNull(tasks);
        Assertions.assertNotNull(subTasks);
        Assertions.assertNotNull(epics);
        Assertions.assertTrue(tasks2.isEmpty());
        Assertions.assertTrue(subTasks2.isEmpty());
        Assertions.assertTrue(epics2.isEmpty());
    }

    @Test
    public void timeTasksTest() {
        Task task = taskManager1.createTask(new Task("Новая задача", Status.NEW,
                "Описание задачи", LocalDateTime.now(), Duration.ofMinutes(15)));
        Task task1 = taskManager1.createTask(new Task("Новая задача1", Status.NEW,
                "Описание задачи1", LocalDateTime.now().plusHours(3), Duration.ofMinutes(25)));
        Task task2 = taskManager1.createTask(new Task("Новая задача2", Status.NEW,
                "Описание задачи2", LocalDateTime.now().plusHours(4), Duration.ofMinutes(35)));
        Assertions.assertTrue(task.getEndTime().isBefore(task1.getStartTime()));
        Assertions.assertTrue(task1.getEndTime().isBefore(task2.getStartTime()));
    }

    @Test
    public void historyTest() {

        Task task = taskManager1.createTask(new Task("Новая задача", Status.NEW,
                "Описание задачи", LocalDateTime.now(), Duration.ofMinutes(15)));
        Task task1 = taskManager1.createTask(new Task("Новая задача1", Status.NEW,
                "Описание задачи1", LocalDateTime.now().plusHours(3), Duration.ofMinutes(25)));
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));

        taskManager1.getTask(task.getId());
        taskManager1.getTask(task1.getId());
        taskManager1.getEpic(epic.getId());
        taskManager1.getSubTask(subTask.getId());

        List<Task> history = taskManager1.getHistory();
        taskManager1.deleteTask(task1.getId());
        List<Task> history1 = taskManager1.getHistory();
        Assertions.assertFalse(history.isEmpty());
        Assertions.assertTrue(history.size() == 4);
        Assertions.assertTrue(history1.size() == 3);
    }

    
}
