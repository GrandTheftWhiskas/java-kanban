package managers;
import tasks.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private HashMap<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;


    @Override
    public void add(Task task) {
       if (historyMap.containsKey(task.getId())) {
           Node rmNode = historyMap.get(task.getId());
           removeNode(rmNode);
       }
       linkLast(task);
    }

    @Override
    public void remove(int id) {
       removeNode(historyMap.remove(id));
    }

    private void removeNode(Node node) {
        Node next = node.next;
        Node prev = node.prev;

        if (prev != null) {
            prev.next = next;
        } else {
            head = next;
        }
        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void clearHistory(List<Integer> tasks) {
        for (int id : tasks) {
            Node node = historyMap.remove(id);
            removeNode(node);
        }
    }

    private List<Task> getTasks() {
        List<Task> list = new LinkedList<>();
        Node node = head;
        while (node != null) {
            list.addLast(node.getTask());
            node = node.next;
        }
        return list;
    }

    private void linkLast(Task element) {
        final Node oldTail = tail;
        final Node newNode = new Node(tail, element, null);
        tail = newNode;
        if (oldTail != null) {
            oldTail.next = newNode;
        } else {
            head = newNode;
        }
        historyMap.put(element.getId(), newNode);
    }

    private static class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

        public Task getTask() {
            return data;
        }
    }
}