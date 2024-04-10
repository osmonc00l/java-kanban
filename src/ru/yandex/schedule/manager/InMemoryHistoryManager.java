package ru.yandex.schedule.manager;

import ru.yandex.schedule.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;
    private int size;
    @Override
    public void add(Task task) {
        linkLast(task);
    }

    private void updateSize() {
        size = history.size();
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }


    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        if (size != 0) {
            Node iter = head;
            while (iter != null) {
                taskArrayList.add(iter.getValue());
                iter = iter.getNext();
            }
        }
        return taskArrayList;
    }

    private void removeNode(Integer id) {
        Node element = history.get(id);
        if (size == 1) {
            head = tail = null;
        } else {
            Node next = element.getNext();
            Node previous = element.getPrevious();
            removeCurrentFromNext(next, previous);
            removeCurrentFromPrevious(next, previous);
        }
        history.remove(id);
        updateSize();
    }

    private void removeCurrentFromNext(Node next, Node previous) {
        if (next == null) {
            previous.setNext(null);
            tail = previous;
        } else {
            if (previous == null) {
                next.setPrevious(null);
                head = next;
            } else {
                next.setPrevious(previous);
            }
        }
    }

    private void linkLast(Task task) {
        Node element = new Node(task);
        if (history.containsKey(task.getId())) {
            removeNode(task.getId());
        }
        history.put(task.getId(), element);
        if (size == 0) {
            head = tail = element;
        } else {
            tail.setNext(element);
            element.setPrevious(tail);
            tail = element;
        }
        updateSize();
    }

    private void removeCurrentFromPrevious(Node next, Node previous) {
        if (previous == null) {
            next.setPrevious(null);
            head = next;
        } else {
            if (next == null) {
                previous.setNext(null);
                tail = previous;
            } else {
                previous.setNext(next);
            }
        }
    }

    private static class Node {
        private Task task;
        private Node next;
        private Node previous;

        private Node(Task task) {
            setValue(task);
        }

        private Task getValue() {
            return task;
        }

        private void setValue(Task task) {
            this.task = task;
        }

        private Node getNext() {
            return next;
        }

        private void setNext(Node next) {
            this.next = next;
        }

        private Node getPrevious() {
            return previous;
        }

        private void setPrevious(Node previous) {
            this.previous = previous;
        }
    }
}
