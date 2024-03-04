package ru.yandex.schedule.manager;

import ru.yandex.schedule.tasks.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);
    void remove(Task task);
    ArrayList<Task> getHistory();
}
