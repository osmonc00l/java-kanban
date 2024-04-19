package ru.yandex.schedule.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.schedule.manager.Managers;
import ru.yandex.schedule.manager.TaskManager;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Status;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void initEach() {
        taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistoryManager());
    }

    @Test
    void addTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    void deleteTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        final int taskId = taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskById(taskId));
        taskManager.deleteTask(taskId);
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllTasks());
    }

    @Test
    void addSubtask() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubask", "Test addNewSubtask description", Status.NEW, epic.getId());
        final int subtaskId = taskManager.addSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Сабтаски не найдены.");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтасков.");
        assertEquals(savedSubtask, subtasks.get(0), "Сабтаски не совпадают.");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", Status.NEW,
                + epic.getId());
        taskManager.addSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, taskManager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void deleteEpic() {
        Epic epic = new Epic("Epic", "Epic description");
        final int epicId = taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epicId));
        taskManager.deleteEpic(epicId);
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllEpics());
    }

    @Test
    void deleteSubtask() {
        Epic epic = new Epic("Epic", "Epic description");
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description", Status.NEW, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtaskById(subtaskId));
        assertEquals(epic, taskManager.getEpicById(epicId));
        taskManager.deleteSubtask(subtaskId);
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllSubtasks());
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int taskId = taskManager.addEpic(epic);

        final Epic savedEpic = taskManager.getEpicById(taskId);

        assertNotNull(savedEpic, "Эпики не найдены.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(savedEpic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void historyContainsOldVersions() {
        Task task1 = new Task("task", "desc", Status.NEW);
        taskManager.addTask(task1);
        assertEquals(task1.getName(), taskManager.getTaskById(1).getName());
        task1.setName("Updated name");
        task1.setDescription("Updated decp");
        assertEquals(task1.getName(), taskManager.getTaskById(1).getName());
    }

    @Test
    void checkIfTasksAreSame() {
        Task task1 = new Task("task", "desc", Status.NEW);
        taskManager.addTask(task1);
        assertEquals(task1, taskManager.getTaskById(1));
        task1.setName("Updated name");
        task1.setDescription("Updated decp");
        assertEquals(task1, taskManager.getTaskById(1));
    }

    @Test
    void checkIfTasksHasSameFields() {
        Task task1 = new Task("task", "desc", Status.NEW);
        taskManager.addTask(task1);
        assertEquals(task1.getName(), taskManager.getTaskById(1).getName());
        assertEquals(task1.getDescription(), taskManager.getTaskById(1).getDescription());
        assertEquals(task1.getStatus(), taskManager.getTaskById(1).getStatus());
    }

    @Test
    public void deleteTaskFromHistory() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        final int taskId = taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskById(taskId));
        List<Task> history = taskManager.getHistory();
        assertArrayEquals(history.toArray(), taskManager.getAllTasks().toArray());
        assertEquals(history.size(), taskManager.getAllTasks().size());
        taskManager.deleteAllTasks();
        history = taskManager.getAllTasks();
        assertEquals(history.size(), taskManager.getAllTasks().size());
    }

    @Test
    public void deleteEpicFromHistory() {
        Epic epic = new Epic("Epic", "Epic description");
        final int epicId = taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epicId));
        List<Task> history = taskManager.getHistory();
        assertArrayEquals(history.toArray(), taskManager.getAllEpics().toArray());
        assertEquals(history.size(), taskManager.getAllEpics().size());
        taskManager.deleteAllEpics();
        history = taskManager.getHistory();
        assertEquals(history.size(), taskManager.getAllEpics().size());
    }

    @Test
    public void deleteSubtasksFromHistory() {
        Epic epic = new Epic("Epic", "Epic description");
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask description", Status.NEW, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtaskById(subtaskId));
        List<Task> history = taskManager.getHistory();
        assertArrayEquals(history.toArray(), taskManager.getAllSubtasks().toArray());
        assertEquals(history.size(), taskManager.getAllSubtasks().size());
        taskManager.deleteAllSubtasks();
        history = taskManager.getHistory();
        assertEquals(history.size(), taskManager.getAllSubtasks().size());
    }
}