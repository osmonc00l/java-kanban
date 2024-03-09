package ru.yandex.schedule.manager;

public class Managers<T extends TaskManager> {
    public static TaskManager getInMemoryTaskManager(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

}
