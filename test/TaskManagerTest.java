import managers.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskManagerTest<T extends TaskManager> {
    TaskManager taskManager1 = Managers.getDefault();

    @Test
    public void createTaskTest() {
        Task task = taskManager1.createTask(new Task("Новая задача", Status.NEW,
                "Описание задачи", LocalDateTime.now(), Duration.ofMinutes(15)));

        Assertions.assertNotNull(task);
    }

    @Test
    public void createSubtaskTest() {
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));

        Assertions.assertNotNull(subTask);
    }

    @Test
    public void createEpicTest() {
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));

        Assertions.assertNotNull(epic);
    }

    @Test
    public void getTaskTest() {
        Task task = taskManager1.createTask(new Task("Новая задача", Status.NEW,
                "Описание задачи", LocalDateTime.now(), Duration.ofMinutes(15)));

        Task task1 = taskManager1.getTask(task.getId());
        Assertions.assertNotNull(task1);
    }

    @Test
    public void getSubtaskTest() {
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));

        SubTask subTask1 = taskManager1.getSubTask(subTask.getId());
        Assertions.assertNotNull(subTask1);
    }

    @Test
    public void getEpicTest() {
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));

        Epic epic1 = taskManager1.getEpic(epic.getId());
        Assertions.assertNotNull(epic1);
    }

    @Test
    public void getAllTasksTest() {
        Task task = taskManager1.createTask(new Task("Новая задача", Status.NEW,
                "Описание задачи", LocalDateTime.now(), Duration.ofMinutes(15)));
        List<Task> list = taskManager1.getAllTasks();
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void getAllSubtasksTest() {
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));
        List<SubTask> list = taskManager1.getAllSubTasks();
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void getAllEpicsTest() {
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));
        List<Epic> list = taskManager1.getAllEpics();
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    public void deleteTaskTest() {
        Task task = taskManager1.createTask(new Task("Новая задача", Status.NEW,
                "Описание задачи", LocalDateTime.now(), Duration.ofMinutes(15)));
        taskManager1.deleteTask(task.getId());
        Task task1 = taskManager1.getTask(task.getId());
        Assertions.assertNull(task1);
    }

    @Test
    public void deleteSubtaskTest() {
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));
        taskManager1.deleteSubTask(subTask.getId());
        SubTask subTask1 = taskManager1.getSubTask(subTask.getId());
        Assertions.assertNull(subTask1);
    }

    @Test
    public void deleteEpicTest() {
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));
        taskManager1.deleteEpic(epic.getId());
        Epic epic1 = taskManager1.getEpic(epic.getId());
        Assertions.assertNull(epic1);
    }

    @Test
    public void deleteAllTasksTest() {
        Task task = taskManager1.createTask(new Task("Новая задача", Status.NEW,
                "Описание задачи", LocalDateTime.now(), Duration.ofMinutes(15)));
        taskManager1.deleteAllTasks();
        List<Task> list = taskManager1.getAllTasks();
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    public void deleteAllSubtasksTest() {
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.NEW,
            "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));
        taskManager1.deleteAllSubTasks();
        List<SubTask> list = taskManager1.getAllSubTasks();
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    public void deleteAllEpicsTest() {
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));
        taskManager1.deleteAllEpics();
        List<Epic> list = taskManager1.getAllEpics();
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    public void getEpicIdTest() {
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        Assertions.assertNotNull(epic.getId());
    }

    @Test
    public void getSubtaskFromEpicTest() {
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));
        SubTask subTask1 = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.NEW,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));
        Assertions.assertFalse(epic.getAllSubTasks().isEmpty());
    }

    @Test
    public void getEpicStatusTest() {
        Epic epic = taskManager1.createEpic(new Epic("Новый эпик", Status.NEW, "Описание эпика"));
        SubTask subTask = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.IN_PROGRESS,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));
        SubTask subTask1 = taskManager1.createSubTask(new SubTask("Новая подзадача", Status.IN_PROGRESS,
                "Описание подзадачи", LocalDateTime.now(), Duration.ofMinutes(30), epic.getId()));
        Assertions.assertEquals(epic.getStatus(), Status.IN_PROGRESS);
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

    }

    @Test
    public void timeTasksTest2() {
        Task task = taskManager1.createTask(new Task("Новая задача", Status.NEW,
                "Описание задачи", LocalDateTime.now(), Duration.ofMinutes(15)));
        Task task1 = taskManager1.createTask(new Task("Новая задача1", Status.NEW,
                "Описание задачи1", LocalDateTime.now().plusHours(3), Duration.ofMinutes(25)));
        Task task2 = taskManager1.createTask(new Task("Новая задача2", Status.NEW,
                "Описание задачи2", LocalDateTime.now().plusHours(4), Duration.ofMinutes(35)));
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

        taskManager1.deleteTask(task1.getId());
        List<Task> history = taskManager1.getHistory();
        Assertions.assertTrue(history.size() == 3);
    }
}
