package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.schedule.resources.Managers;
import ru.yandex.schedule.manager.HistoryManager;
import ru.yandex.schedule.resources.Status;
import ru.yandex.schedule.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class InMemoryHistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    public void initEach() {
        historyManager = Managers.getDefaultHistoryManager();
    }

    @Test
    void add() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
    }

}