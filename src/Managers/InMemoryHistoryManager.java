package Managers;
import Tasks.*;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int SIZE = 10;
    private LinkedList<Task> history = new LinkedList<>();
    @Override
    public void add(Task task) {
        if (history.size() == SIZE) {
            history.remove(0);
        }
        history.add(history.size(), task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> returnList = new ArrayList<>(history);
        return returnList;
    }
}
