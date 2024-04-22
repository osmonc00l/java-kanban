package ru.yandex.schedule.manager;

import ru.yandex.schedule.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        linkLast(task);
    }


    @Override
    public void remove(int id) {
        removeNode(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        if (head != null) {
            Node iter = head;
            while (iter != null) {
                taskArrayList.add(iter.task);
                iter = iter.next;
            }
        }
        return taskArrayList;
    }

    private void removeNode(Integer id) {
        Node element = history.remove(id);
        if (element == null) {
            return;
        }
        Node next = element.next;
        Node previous = element.previous;

        if (previous == null) {
            head = next;
        } else {
            previous.next = next;
            tail = previous;
        }

        if (next == null) {
            tail = previous;
        } else {
            next.previous = previous;
            head.next = null;
        }

    }

    private void linkLast(Task task) {
        Node element = new Node(task);
        if (history.containsKey(task.getId())) {
            removeNode(task.getId());
        }
        history.put(task.getId(), element);
        if (head == null) {
            head = tail = element;
        } else {
            tail.next = element;
            element.previous = tail;
            tail = element;
        }
    }

    private static class Node {
        final Task task;
        private Node next;
        private Node previous;

        private Node(Task task) {
            this.task = task;
        }
    }
}
