package ru.yandex.schedule.manager;

import ru.yandex.schedule.tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private ArrayList<Task> history = new ArrayList<>();
    @Override
    public void add(Task task) {
        checkIfFull();
        history.add(task);
    }

    @Override
    public void remove(Task task) {
        if(!history.isEmpty()) {
            history.remove(task);
        } else {
            System.out.println("История пуста");
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
    private void checkIfFull() {
        if (history.size() == 10) {
            history.remove(0);
        }
    }
}
