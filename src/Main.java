import tasks.*;
import managers.*;

public class Main {
    public static void main(String[] args){
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        HistoryManager manager = Managers.getDefaultHistory();
        Task task = taskManager.createTask(new Task("Новая задача", Status.NEW, "Описание"));
        System.out.println("Создана задача: " + task);
        Task task1 = taskManager.createTask(new Task("Новая задача1", Status.NEW, "Описание задачи1"));

        Task taskM = taskManager.getTask(task.getId());
        System.out.println("Получена задача: " + taskM);

        taskManager.getTask(task1.getId());

        taskM = taskManager.getTask(1);
        System.out.println("Получена задача: " + taskM);

        System.out.println("Cписок: ");
        taskManager.getHistory();

        for (Task print : manager.getHistory()) {
            System.out.println(print);
        }
    }
}