import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.InMemoryTaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


public class HttpTaskServer {

    public void start() {
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
            httpServer.createContext("/tasks", new TaskHandler());
            httpServer.createContext("/subtasks", new SubtaskHandler());
            httpServer.createContext("/epics", new EpicHandler());
            httpServer.createContext("/history", new HistoryHandler());
            httpServer.createContext("/prioritized", new PrioritizedHandler());
            httpServer.start();
        } catch (IOException e) {
            System.out.println("Произошла ошибка");
        }
    }
}

class TaskHandler implements HttpHandler {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    Gson gson = new Gson();
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        switch (method) {
            case "GET":
                if (path.length > 2) {
                    Task task = taskManager.getTask(Integer.parseInt(path[2]));
                    if (task != null) {
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            String response = gson.toJson(task);
                            os.write(response.getBytes());
                        }
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            String response = "Задача не найдена";
                            os.write(response.getBytes());
                        }
                    }
                } else {
                    List<Task> tasks = taskManager.getAllTasks();
                    exchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        String response = gson.toJson(tasks);
                        os.write(response.getBytes());
                    }
                }
                break;
            case "POST":
                InputStream inputStream = exchange.getRequestBody();
                String request = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(request, Task.class);
                if (task.getId() == 0) {
                    Task returnTask = taskManager.createTask(task);
                    if (returnTask != null) {
                        exchange.sendResponseHeaders(201, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            String response = "Задача успешно cоздана";
                            os.write(response.getBytes());
                        }
                    } else {
                        exchange.sendResponseHeaders(406, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            String response = "Ошибка: Обнаружено пересечение";
                            os.write(response.getBytes());
                        }
                    }
                } else {
                    taskManager.updateTask(task);
                    exchange.sendResponseHeaders(201, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        String response = "Задача успешно обновлена";
                        os.write(response.getBytes());
                    }
                }
                break;
            case "DELETE":
                InputStream inputStream1 = exchange.getRequestBody();
                String request1 = new String(inputStream1.readAllBytes(), StandardCharsets.UTF_8);
                Task task1 = gson.fromJson(request1, Task.class);
                taskManager.deleteTask(task1.getId());
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    String response = "Задача успешно удалена";
                    os.write(response.getBytes());
                }
                break;
            default:
                exchange.sendResponseHeaders(404, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    String response = "Введен неверный url";
                    os.write(response.getBytes());
                }
        }
    }
}

class SubtaskHandler extends TaskHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        switch (method) {
            case "GET":
                if (path.length > 2) {
                    SubTask returnSubtask = taskManager.getSubTask(Integer.parseInt(path[2]));
                    if (returnSubtask != null) {
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            String response = gson.toJson(returnSubtask);
                            os.write(response.getBytes());
                        }
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            String response = "Подзадача не найдена";
                            os.write(response.getBytes());
                        }
                    }
                } else {
                    List<SubTask> subTasks = taskManager.getAllSubTasks();
                    exchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        String response = gson.toJson(subTasks);
                        os.write(response.getBytes());
                    }
                }
                break;
            case "POST":
                InputStream inputStream = exchange.getRequestBody();
                String request = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                SubTask subTask = gson.fromJson(request, SubTask.class);
                if (subTask.getId() == 0) {
                    SubTask returnSubtask = taskManager.createSubTask(subTask);
                    if (returnSubtask != null) {
                        exchange.sendResponseHeaders(201, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            String response = "Подзадача успешно добавлена";
                            os.write(response.getBytes());
                        }
                    } else {
                        exchange.sendResponseHeaders(406, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            String response = "Ошибка: Обнаружено пересечение";
                            os.write(response.getBytes());
                        }
                    }
                } else {
                    taskManager.updateSubTask(subTask);
                    exchange.sendResponseHeaders(201, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        String response = "Подзадача успешно обновлена";
                        os.write(response.getBytes());
                    }
                }
                break;
            case "DELETE":
                InputStream inputStream1 = exchange.getRequestBody();
                String request1 = new String(inputStream1.readAllBytes(), StandardCharsets.UTF_8);
                SubTask subTask1 = gson.fromJson(request1, SubTask.class);
                taskManager.deleteSubTask(subTask1.getId());
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    String response = "Подзадача успешно удалена";
                    os.write(response.getBytes());
                }
                break;
            default:
                exchange.sendResponseHeaders(404, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    String response = "Введен неверный url";
                    os.write(response.getBytes());
                }
        }
    }
}

class EpicHandler extends TaskHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        switch (method) {
            case "GET":
                if (path.length == 3) {
                    Epic returnEpic = taskManager.getEpic(Integer.parseInt(path[2]));
                    if (returnEpic != null) {
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            String response = gson.toJson(returnEpic);
                            os.write(response.getBytes());
                        }
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            String response = "Эпик не найден";
                            os.write(response.getBytes());
                        }
                    }
                } else if (path.length == 4) {
                    Epic returnEpic = taskManager.getEpic(Integer.parseInt(path[2]));
                    if (returnEpic != null) {
                        List<Integer> list = returnEpic.getAllSubTasks();
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            String response = gson.toJson(list);
                            os.write(response.getBytes());
                        }
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            String response = "Эпик не найден";
                            os.write(response.getBytes());
                        }
                    }
                } else {
                    List<Epic> epics = taskManager.getAllEpics();
                    exchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        String response = gson.toJson(epics);
                        os.write(response.getBytes());
                    }
                }
                break;
            case "POST":
                InputStream inputStream = exchange.getRequestBody();
                String request = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(request, Epic.class);
                taskManager.createEpic(epic);
                exchange.sendResponseHeaders(201, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    String response = "Эпик успешно создан";
                    os.write(response.getBytes());
                }
                break;
            case "DELETE":
                InputStream inputStream1 = exchange.getRequestBody();
                String request1 = new String(inputStream1.readAllBytes(), StandardCharsets.UTF_8);
                Epic epic1 = gson.fromJson(request1, Epic.class);
                taskManager.deleteEpic(epic1.getId());
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    String response = "Эпик успешно удален";
                    os.write(response.getBytes());
                }
                break;
            default:
                exchange.sendResponseHeaders(404, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    String response = "Введен неверный url";
                    os.write(response.getBytes());
                }
        }
    }
}

class HistoryHandler extends TaskHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        List<Task> history = taskManager.getHistory();
        exchange.sendResponseHeaders(200, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            String response = gson.toJson(history);
            os.write(response.getBytes());
        }
    }
}

class PrioritizedHandler extends TaskHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        exchange.sendResponseHeaders(200, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            String response = gson.toJson(prioritizedTasks);
            os.write(response.getBytes());
        }
    }
}
