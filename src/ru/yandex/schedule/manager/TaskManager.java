package ru.yandex.schedule.manager;

import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

public interface TaskManager {
    Optional<Task> addTask(Task task);

    boolean updateTask(Task task);

    Task deleteTask(int id);

    Optional<Subtask> addSubtask(Subtask subtask);

    boolean updateSubtask(Subtask updatedSubTask);

    Subtask deleteSubtask(int id);

    Optional<Epic> addEpic(Epic epic);

    boolean updateEpic(Epic updatedEpic);

    Epic deleteEpic(Integer id);

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Optional<Task> getTaskById(int id);

    Optional<Subtask> getSubtaskById(int id);

    Optional<Epic> getEpicById(int id);

    List<Subtask> getSubtasksOfEpic(int epicId);

    List<Task> getTasksList();

    List<Epic> getEpicsList();

    List<Subtask> getSubtasksList();

    void printEpics();

    void printSubtasks();

    void printTasks();

    List<Task> getHistory();

    TreeSet<Task> getSortedTasks();
}
