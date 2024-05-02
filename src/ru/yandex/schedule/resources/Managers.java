package ru.yandex.schedule.resources;

import ru.yandex.schedule.manager.HistoryManager;
import ru.yandex.schedule.manager.InMemoryHistoryManager;
import ru.yandex.schedule.manager.InMemoryTaskManager;
import ru.yandex.schedule.manager.TaskManager;

public class Managers<T extends TaskManager> {
    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

}
