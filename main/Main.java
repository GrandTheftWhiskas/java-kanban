import tasks.*;
import managers.*;
import exceptions.*;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = taskManager.createTask(new Task("Новая задача", Status.NEW, "Описание",
                LocalDateTime.now(), Duration.ofMinutes(90)));
        System.out.println("Создана задача: " + task);

        Task taskM = taskManager.getTask(task.getId());
        System.out.println("Получена задача: " + taskM);

        taskM = taskManager.getTask(1);
        System.out.println("Получена задача: " + taskM);

        System.out.println("Cписок: ");
        System.out.println(taskManager.getHistory());
    }
}