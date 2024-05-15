package ru.yandex.schedule.manager;

import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    int addTask(Task task);

    void updateTask(Task task);

    void deleteTask(int id);

    int addSubtask(Subtask subtask);

    void updateSubtask(Subtask updatedSubTask);

    Subtask deleteSubtask(int id);

    int addEpic(Epic epic);

    void updateEpic(Epic updatedEpic);

    void deleteEpic(int id);

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

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
