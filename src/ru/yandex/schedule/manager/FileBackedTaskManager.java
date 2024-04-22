package ru.yandex.schedule.manager;

public class FileBackedTaskManager extends InMemoryTaskManager{
    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }
}
