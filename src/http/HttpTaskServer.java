package http;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    public static void main(String[] args) {
        start();
    }
    public static void start() {
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
            System.out.println(e.getStackTrace());
        }
    }
}
