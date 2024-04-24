package http;
import com.sun.net.httpserver.HttpExchange;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

class PrioritizedHandler extends TaskHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Task task = null;
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        exchange.sendResponseHeaders(200, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            String response = gson.toJson(prioritizedTasks);
            os.write(response.getBytes());
        }
    }
}
