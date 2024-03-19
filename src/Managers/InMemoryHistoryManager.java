package Managers;
import Tasks.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    List<Task> returnList = new ArrayList<>();
    private HashMap<Integer, Node> historyMap = new HashMap<>();
    Node head;
    Node tail;


    @Override
    public void add(Task task) {
       if (historyMap.containsKey(task.getId())) {
           Node rmNode = historyMap.get(task.getId());
           removeNode(rmNode);
           returnList.remove(task);
       }
       addLast(task);
    }

    @Override
    public void remove(int id) {
        Node rmNode = historyMap.get(id);
        removeNode(rmNode);
        returnList.remove(id);
    }

    public void removeNode(Node node) {
        historyMap.remove(node);
    }

    @Override
    public List<Task> getHistory() {
        return returnList;
    }

    public void addLast(Task element) {
        final Node oldTail = tail;
        final Node newNode = new Node(tail, element, null);
        tail = newNode;
        if (oldTail != null) {
            oldTail.next = newNode;
        } else {
            head = newNode;
        }
        historyMap.put(element.getId(), newNode);
        returnList.add(element);
    }
}




