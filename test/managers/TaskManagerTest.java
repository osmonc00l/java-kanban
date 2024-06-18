package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.schedule.manager.TaskManager;
import ru.yandex.schedule.resources.Status;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest <T extends TaskManager>{
    protected T taskManager;
    protected abstract T createTaskManager();
    @BeforeEach
    public void init() {
        taskManager = createTaskManager();
    }
    @Test
    public void getTasksList() {
        List<Task> taskList = taskManager.getTasksList();
        assertEquals(0, taskList.size());
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.of(2023, 9, 2, 7, 0),
                Duration.ofMinutes(30).toMinutes());
        taskManager.addTask(task);
        taskList = taskManager.getTasksList();
        assertEquals(1, taskList.size());
    }
    @Test
    void isTasksOverlap() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.of(2023, 9, 2, 7, 0),
                Duration.ofMinutes(30).toMinutes());
        taskManager.addTask(task);

        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask1 = new Subtask("Первая подзадача",
                "Описание первой подзадачи", Status.NEW, epicId,
                LocalDateTime.of(2023, 10, 6, 12, 0),
                Duration.ofMinutes(30).toMinutes());
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Вторая подзадача c другим временем",
                "Описание второй подзадачи", Status.NEW, epicId,
                LocalDateTime.of(2023, 10, 6, 12, 0),
                Duration.ofMinutes(30).toMinutes());
        taskManager.addSubtask(subtask2);
        assertEquals(2, taskManager.getSortedTasks().size());
        Subtask subtask3 = new Subtask("Третья подзадача",
                "Описание третьей подзадачи", Status.NEW, epicId,
                LocalDateTime.of(2023, 10, 6, 13, 0),
                Duration.ofMinutes(30).toMinutes());
        taskManager.addSubtask(subtask3);
        assertEquals(3, taskManager.getSortedTasks().size());
    }
}
