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
import java.util.Optional;

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
        Optional<Task> optionalTask = taskManager.addTask(task);
        optionalTask.ifPresent(value -> assertEquals(value, task));

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
        Optional<Task> optionalTask = taskManager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        optionalTask.ifPresent(value -> assertEquals(Status.IN_PROGRESS, value.getStatus()));

    }

    @Test
    void deleteTask() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.of(2023, 9, 2, 7, 0),
                Duration.ofMinutes(30).toMinutes());
        Optional<Task> optionalTask = taskManager.addTask(task);
        Task savedTask = taskManager.getTaskById(task.getId()).get();
        optionalTask.ifPresent(value -> assertEquals(value, task));
        taskManager.deleteTask(savedTask.getId());
        assertEquals(Collections.EMPTY_LIST, taskManager.getTasksList());
    }

    @Test
    void addSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача",
                "Описание подзадачи", Status.NEW, epic.getId(),
                LocalDateTime.of(2023, 10, 6, 12, 0),
                Duration.ofMinutes(30).toMinutes());
        Optional<Subtask> optionalSubtask = taskManager.addSubtask(subtask);
        optionalSubtask.ifPresent(value -> assertEquals(value, subtask));
        final List<Subtask> subtasks = taskManager.getSubtasksList();

        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтасков.");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача",
                "Описание подзадачи", Status.NEW, epic.getId(),
                LocalDateTime.of(2023, 10, 6, 12, 0),
                Duration.ofMinutes(30).toMinutes());
        Optional<Subtask> optionalSubtask = taskManager.addSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        if (optionalSubtask.isPresent()) {
            Subtask savedSubtask = optionalSubtask.get();
            assertEquals(Status.IN_PROGRESS, savedSubtask.getStatus());
            assertEquals(Status.IN_PROGRESS, savedSubtask.getStatus());
        }

    }

    @Test
    void deleteEpic() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.addEpic(epic);
        Epic savedEpic = taskManager.getEpicById(epic.getId()).get();
        assertEquals(epic, savedEpic);
        taskManager.deleteEpic(epic.getId());
        assertEquals(Collections.EMPTY_LIST, taskManager.getEpicsList());
    }

    @Test
    void deleteSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        Optional<Epic> optionalEpic = taskManager.addEpic(epic);
        Epic savedEpic = optionalEpic.get();
        Subtask subtask = new Subtask("Подзадача",
                "Описание подзадачи", Status.NEW, epic.getId(),
                LocalDateTime.of(2023, 10, 6, 12, 0),
                Duration.ofMinutes(30).toMinutes());
        Optional<Subtask> optionalSubtask = taskManager.addSubtask(subtask);
        if (optionalSubtask.isPresent()) {
            Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId()).get();
            assertEquals(subtask, savedSubtask);
            assertEquals(epic, savedEpic);
            taskManager.deleteSubtask(savedSubtask.getId());
            assertEquals(Collections.EMPTY_LIST, taskManager.getSubtasksList());
        }

    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        Optional<Epic> optionalEpic = taskManager.addEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epic.getId()).get();

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
        Optional<Task> optionalTask = taskManager.addTask(task);
        Task savedTask = optionalTask.get();
        assertEquals(task.getName(), savedTask.getName());
        task.setName("Новое имя задачи");
        task.setDescription("Новое описание задачи");
        assertEquals(task.getName(),savedTask.getName());
    }

    @Test
    void checkIfTasksAreSame() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.of(2023, 9, 2, 7, 0),
                Duration.ofMinutes(30).toMinutes());
        taskManager.addTask(task);
        Optional<Task> optionalTask = taskManager.getTaskById(task.getId());
        if(optionalTask.isPresent()) {
            Task savedTask = optionalTask.get();
            assertEquals(task, savedTask);
            task.setName("Updated name");
            task.setDescription("Updated decp");
            assertEquals(task, savedTask);
            task.setName("Updated name");
            task.setDescription("Updated decp");
            assertEquals(task, savedTask);
        }
    }

    @Test
    void checkIfTasksHasSameFields() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.of(2023, 9, 2, 7, 0),
                Duration.ofMinutes(30).toMinutes());
        Optional<Task> optionalTask = taskManager.addTask(task);;
        if(optionalTask.isPresent()) {
            Task savedTask = optionalTask.get();
            assertEquals(task.getName(), savedTask.getName());
            assertEquals(task.getDescription(), savedTask.getDescription());
            assertEquals(task.getStatus(), savedTask.getStatus());
        }
    }

    @Test
    public void deleteTaskFromHistory() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW,
                LocalDateTime.of(2023, 9, 2, 7, 0),
                Duration.ofMinutes(30).toMinutes());
        taskManager.addTask(task);
        Optional<Task> optionalTask = taskManager.getTaskById(task.getId());
        if (optionalTask.isPresent()) {
            Task savedTask = optionalTask.get();
            assertEquals(task, savedTask);
            List<Task> history = taskManager.getHistory();
            assertArrayEquals(history.toArray(), taskManager.getTasksList().toArray());
            assertEquals(history.size(), taskManager.getTasksList().size());
            taskManager.deleteAllTasks();
            history = taskManager.getTasksList();
            assertEquals(history.size(), taskManager.getTasksList().size());
        }

    }

    @Test
    public void deleteEpicFromHistory() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.addEpic(epic);
        Optional<Epic> optionalEpic = taskManager.getEpicById(epic.getId());
        if (optionalEpic.isPresent()) {
            Epic savedEpic = optionalEpic.get();
                    assertEquals(epic, savedEpic);
            List<Task> history = taskManager.getHistory();
            assertArrayEquals(history.toArray(), taskManager.getEpicsList().toArray());
            assertEquals(history.size(), taskManager.getEpicsList().size());
            taskManager.deleteAllEpics();
            assertEquals(0, taskManager.getEpicsList().size());
        }
    }

    @Test
    public void deleteSubtasksFromHistory() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        Optional<Epic> optionalEpic = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача",
                "Описание подзадачи", Status.NEW, epic.getId(),
                LocalDateTime.of(2023, 10, 6, 12, 0),
                Duration.ofMinutes(30).toMinutes());
        taskManager.addSubtask(subtask);
        Optional<Subtask> optionalSubtask = taskManager.getSubtaskById(subtask.getId());
        if (optionalSubtask.isPresent()) {
            Subtask savedSubtask = optionalSubtask.get();
            assertEquals(subtask, savedSubtask);
            List<Task> history = taskManager.getHistory();
            assertArrayEquals(history.toArray(), taskManager.getSubtasksList().toArray());
            assertEquals(history.size(), taskManager.getSubtasksList().size());
            taskManager.deleteAllSubtasks();
            history = taskManager.getHistory();
            assertEquals(history.size(), taskManager.getSubtasksList().size());
        }

    }
}