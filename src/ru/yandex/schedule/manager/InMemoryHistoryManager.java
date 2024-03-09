package ru.yandex.schedule.manager;

import ru.yandex.schedule.tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        checkIfFull();
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }

    private void checkIfFull() {
        if (history.size() == 10) {
            history.remove(0);
        }
    }
}
