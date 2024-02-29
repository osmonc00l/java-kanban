package ru.yandex.schedule.manager;

import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Status;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int idSequence;

    private int idNumber;
    public int addTask(Task task) {
        int newTaskId = generateIdSequence();
        task.setId(newTaskId);
        tasks.put(task.getId(), task);
        return task.getId();
    }
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача отсутствует");
        }
    }
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задача отсутствует");
        }
    }

    public int addSubtask(Subtask subtask) {
        int subtaskId = generateIdSequence();
        subtask.setId(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(subtask.getId(), subtask);
            epic.setSubtaskIds(subtask.getId());
            updateStatusEpic(epic);
            return subtaskId;
        } else {
            System.out.println("Эпик не найден");
            return -1;
        }
    }
    public void updateSubtask(Subtask updatedSubTask) {
        Epic epic = epics.get(updatedSubTask.getEpicId());
        if (!epics.containsKey(updatedSubTask.getEpicId())) {
            System.out.println("Эпик указаный в подзадаче отсутствует");
            return;
        }
        if (subtasks.containsKey(updatedSubTask.getId())) {
            subtasks.put(updatedSubTask.getId(), updatedSubTask);
            updateStatusEpic(epics.get(updatedSubTask.getEpicId()));
        } else {
            System.out.println("Подзадача не была обнаружена");
        }
    }
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.deleteSubtask(id);
            updateStatusEpic(epic);
            subtasks.remove(id);
        } else {
            System.out.println("Такой подзадачи не существует");
        }
    }
    public int addEpic(Epic epic) {
        int epicId = generateIdSequence();
        epic.setId(epicId);
        epics.put(epic.getId(), epic);
        return epic.getId();

    }
    public void updateEpic(Epic updatedEpic) {
        if (epics.containsKey(updatedEpic.getId())) {
            epics.get(updatedEpic.getId()).setName(updatedEpic.getName());
            epics.get(updatedEpic.getId()).setDescription(updatedEpic.getDescription());
        } else {
            System.out.println("Эпик не найден");
        }
    }
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        if (epics != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        } else {
            System.out.println("Такой эпик отсутствует");
        }
    }
    private int generateIdSequence() {
        idSequence++;
        return idSequence;
    }
    public void deleteAllTasks() {
        tasks.clear();
    }
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasksOfEpic();
            updateStatusEpic(epic);
        }
        subtasks.clear();
    }
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }
    public Task getTaskById(int id) {
        return tasks.get(id);
    }
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }
    public Epic getEpicById(int id) {
        return epics.get(id);
    }
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtasksOfEpic= new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            subtasksOfEpic.add(subtasks.get(subtaskId));
        }
        return subtasksOfEpic;
    }
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<Task>(tasks.values());
    }
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<Epic>(epics.values());
    }
    public ArrayList<Subtask> getAllsubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }
    void updateStatusEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtaskIds().size() == 0) {
                epic.setStatus(Status.NEW);
            } else {
                ArrayList<Subtask> newSubtasks = new ArrayList<>();
                int countDone = 0;
                int countNew = 0;

                for (Integer subtaskId : epic.getSubtaskIds()) {
                    Subtask subtask = subtasks.get(subtaskId);
                    if (subtask.getStatus() == Status.DONE) {
                        countDone++;
                    }
                    if (subtask.getStatus() == Status.NEW) {
                        countNew++;
                    }
                    if (subtask.getStatus() == Status.IN_PROGRESS) {
                        epic.setStatus(Status.IN_PROGRESS);
                        return;
                    }
                }

                if (countDone == epic.getSubtaskIds().size()) {
                    epic.setStatus(Status.DONE);
                } else if (countNew == epic.getSubtaskIds().size()) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        } else {
            System.out.println("Эпик не найден");
        }
    }
    public void printEpics() {
        if (epics.isEmpty()) {
            System.out.println("Список эпиков пуст");
            return;
        }
        for (Epic epic : epics.values()) {
            System.out.println(epic);
        }
    }
    public void printSubtasks() {
        if (subtasks.isEmpty()) {
            System.out.println("Список подзадач пуст");
            return;
        }
        for (Subtask subtask : subtasks.values()) {
            System.out.println(subtask);
        }
    }
    public void printTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
    }
}
