package ru.yandex.schedule.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.schedule.manager.InMemoryHistoryManager;
import ru.yandex.schedule.manager.Managers;
import ru.yandex.schedule.manager.TaskManager;
import ru.yandex.schedule.tasks.Status;
import ru.yandex.schedule.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;
    @BeforeEach
    public void initEach(){
        historyManager = new InMemoryHistoryManager();
    }
    @Test
    void add() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void remove() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        historyManager.remove(task);
        assertNull(history, "История пустая");
    }


}