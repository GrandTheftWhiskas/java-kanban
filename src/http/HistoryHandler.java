package http;
import com.sun.net.httpserver.HttpExchange;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

class HistoryHandler extends TaskHandler {
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
