package managers;

import org.junit.jupiter.api.Test;
import ru.yandex.schedule.manager.TaskManager;
import ru.yandex.schedule.resources.Managers;
import ru.yandex.schedule.resources.Status;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager>{
    @Override
    public TaskManager createTaskManager() {
        return Managers.getInMemoryTaskManager();
    }

    @Test
    void addTask() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.of(2023, 9, 2, 7, 0),
                Duration.ofMinutes(30).toMinutes());
        int taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.of(2023, 9, 2, 7, 0),
                Duration.ofMinutes(30).toMinutes());
        int taskId = taskManager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(taskId).getStatus());
    }

    @Test
    void deleteTask() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.of(2023, 9, 2, 7, 0),
                Duration.ofMinutes(30).toMinutes());
        int taskId = taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskById(taskId));
        taskManager.deleteTask(taskId);
        assertEquals(Collections.EMPTY_LIST, taskManager.getTasksList());
    }

    @Test
    void addSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача",
                "Описание подзадачи", Status.NEW, epicId,
                LocalDateTime.of(2023, 10, 6, 12, 0),
                Duration.ofMinutes(30).toMinutes());
        int subtaskId = taskManager.addSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNotNull(savedSubtask, "Сабтаски не найдены.");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasksList();

        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтасков.");
        assertEquals(savedSubtask, subtasks.get(0), "Сабтаски не совпадают.");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача",
                "Описание подзадачи", Status.NEW, epicId,
                LocalDateTime.of(2023, 10, 6, 12, 0),
                Duration.ofMinutes(30).toMinutes());
        int subtaskId = taskManager.addSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, taskManager.getSubtaskById(subtaskId).getStatus());
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epicId).getStatus());
    }

    @Test
    void deleteEpic() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epicId));
        taskManager.deleteEpic(epicId);
        assertEquals(Collections.EMPTY_LIST, taskManager.getEpicsList());
    }

    @Test
    void deleteSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача",
                "Описание подзадачи", Status.NEW, epicId,
                LocalDateTime.of(2023, 10, 6, 12, 0),
                Duration.ofMinutes(30).toMinutes());
        int subtaskId = taskManager.addSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtaskById(subtaskId));
        assertEquals(epic, taskManager.getEpicById(epicId));
        taskManager.deleteSubtask(subtaskId);
        assertEquals(Collections.EMPTY_LIST, taskManager.getSubtasksList());
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпики не найдены.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpicsList();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(savedEpic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void historyContainsOldVersions() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.of(2023, 9, 2, 7, 0),
                Duration.ofMinutes(30).toMinutes());
        int taskId = taskManager.addTask(task);
        assertEquals(task.getName(), taskManager.getTaskById(taskId).getName());
        task.setName("Новое имя задачи");
        task.setDescription("Новое описание задачи");
        assertEquals(task.getName(), taskManager.getTaskById(taskId).getName());
    }

    @Test
    void checkIfTasksAreSame() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.of(2023, 9, 2, 7, 0),
                Duration.ofMinutes(30).toMinutes());
        int taskId = taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskById(taskId));
        task.setName("Updated name");
        task.setDescription("Updated decp");
        assertEquals(task, taskManager.getTaskById(taskId));
    }

    @Test
    void checkIfTasksHasSameFields() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.of(2023, 9, 2, 7, 0),
                Duration.ofMinutes(30).toMinutes());
        int taskId = taskManager.addTask(task);
        assertEquals(task.getName(), taskManager.getTaskById(taskId).getName());
        assertEquals(task.getDescription(), taskManager.getTaskById(taskId).getDescription());
        assertEquals(task.getStatus(), taskManager.getTaskById(taskId).getStatus());
    }

    @Test
    public void deleteTaskFromHistory() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.of(2023, 9, 2, 7, 0),
                Duration.ofMinutes(30).toMinutes());
        int taskId = taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskById(taskId));
        List<Task> history = taskManager.getHistory();
        assertArrayEquals(history.toArray(), taskManager.getTasksList().toArray());
        assertEquals(history.size(), taskManager.getTasksList().size());
        taskManager.deleteAllTasks();
        history = taskManager.getTasksList();
        assertEquals(history.size(), taskManager.getTasksList().size());
    }

    @Test
    public void deleteEpicFromHistory() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epicId));
        List<Task> history = taskManager.getHistory();
        assertArrayEquals(history.toArray(), taskManager.getEpicsList().toArray());
        assertEquals(history.size(), taskManager.getEpicsList().size());
        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getEpicsList().size());
    }

    @Test
    public void deleteSubtasksFromHistory() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача",
                "Описание подзадачи", Status.NEW, epicId,
                LocalDateTime.of(2023, 10, 6, 12, 0),
                Duration.ofMinutes(30).toMinutes());
        int subtaskId = taskManager.addSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtaskById(subtaskId));
        List<Task> history = taskManager.getHistory();
        assertArrayEquals(history.toArray(), taskManager.getSubtasksList().toArray());
        assertEquals(history.size(), taskManager.getSubtasksList().size());
        taskManager.deleteAllSubtasks();
        history = taskManager.getHistory();
        assertEquals(history.size(), taskManager.getSubtasksList().size());
    }
}