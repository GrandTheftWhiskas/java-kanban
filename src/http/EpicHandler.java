package http;
import com.sun.net.httpserver.HttpExchange;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;


class EpicHandler extends TaskHandler {
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
            default:
                exchange.sendResponseHeaders(404, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    String response = "Введен неверный url";
                    os.write(response.getBytes());
                }
        }
    }
}
