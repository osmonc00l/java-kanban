package ru.yandex.schedule.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.schedule.manager.Managers;
import ru.yandex.schedule.manager.TaskManager;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Status;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager taskManager;
    @BeforeEach
    public void initEach(){
        taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
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

    }

    @Test
    void deleteTask() {
    }

    @Test
    void addSubtask() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubask", "Test addNewSubtask description", Status.NEW, 1);
        final int subtaskId = taskManager.addSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Сабтаски не найдены.");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllsubtasks();

        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтасков.");
        assertEquals(savedSubtask, subtasks.get(0), "Сабтаски не совпадают.");
    }

    @Test
    void updateSubtask() {
    }

    @Test
    void deleteSubtask() {
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
    void historyContainsOldVersions(){
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
    void canNotAddSubtaskToSubtasks() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "subtask1 description", Status.NEW, 1);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2 description", Status.NEW, 2);
        taskManager.addSubtask(subtask1);
        ArrayList<Subtask> history = taskManager.getAllsubtasks();
        assertEquals(2, history.size());
    }
}