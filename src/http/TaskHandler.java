package http;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.InMemoryTaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
