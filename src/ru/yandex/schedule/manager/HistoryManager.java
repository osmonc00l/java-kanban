package ru.yandex.schedule.manager;

import ru.yandex.schedule.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);

    List<Task> getHistory();
}
