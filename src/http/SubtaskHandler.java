package http;
import com.sun.net.httpserver.HttpExchange;
import tasks.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

class SubtaskHandler extends TaskHandler {
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
            default:
                exchange.sendResponseHeaders(404, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    String response = "Введен неверный url";
                    os.write(response.getBytes());
                }
        }
    }
}
