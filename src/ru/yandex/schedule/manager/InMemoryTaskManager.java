package ru.yandex.schedule.manager;

import ru.yandex.schedule.resources.Managers;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.resources.Status;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected int idSequence;
    protected final HistoryManager historyManager = Managers.getDefaultHistoryManager();

    protected Integer putInTaskHashMap(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
            return task.getId();
        } else {
            return null;
        }
    }

    @Override
    public int addTask(Task task) {
        task.setId(generateIdSequence());
        return putInTaskHashMap(task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача отсутствует");
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            tasks.remove(id);
        } else {
            System.out.println("Задача отсутствует");
        }
    }

    protected Integer putInSubtaskHashMap(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(subtask.getId(), subtask);
            epic.setSubtaskIds(subtask.getId());
            updateStatusEpic(epic);
            return subtask.getId();
        } else {
            System.out.println("Эпик не найден");
            return -1;
        }
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int subtaskId = generateIdSequence();
        subtask.setId(subtaskId);
        return putInSubtaskHashMap(subtask);
    }

    @Override
    public void updateSubtask(Subtask updatedSubTask) {
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

    @Override
    public Subtask deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            epics.get(subtask.getEpicId()).deleteSubtask(subtask.getId());
            historyManager.remove(id);
            return subtask;
        } else {
            System.out.println("Такой подзадачи не существует");
            return null;
        }
    }

    protected Integer putInEpicHashMap(Epic epic) {
        if (epic != null) {
            epics.put(epic.getId(), epic);
            return epic.getId();
        } else {
            return null;
        }
    }

    @Override
    public int addEpic(Epic epic) {
        int epicId = generateIdSequence();
        epic.setId(epicId);
        return putInEpicHashMap(epic);

    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        if (epics.containsKey(updatedEpic.getId())) {
            epics.get(updatedEpic.getId()).setName(updatedEpic.getName());
            epics.get(updatedEpic.getId()).setDescription(updatedEpic.getDescription());
        } else {
            System.out.println("Эпик не найден");
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        if (epics.containsKey(id)) {
            historyManager.remove(id);
            for (Integer subtaskId : epic.getSubtaskIds()) {
                deleteSubtask(subtaskId);
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        } else {
            System.out.println("Такой эпик отсутствует");
        }
    }

    private int generateIdSequence() {
        return ++idSequence;
    }

    @Override
    public void deleteAllTasks() {
        for (int taskId : tasks.keySet()) {
            deleteTask(taskId);
        }
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        for (int subtaskId : subtasks.keySet()) {
            deleteSubtask(subtaskId);
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        for (int epicId: epics.keySet()) {
            deleteEpic(epicId);
        }
        epics.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            return null;
        }

    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            return null;
        }

    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else {
            return null;
        }

    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            subtasksOfEpic.add(subtasks.get(subtaskId));
        }
        return subtasksOfEpic;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    private void updateStatusEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtaskIds().isEmpty()) {
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

    @Override
    public void printEpics() {
        if (epics.isEmpty()) {
            System.out.println("Список эпиков пуст");
            return;
        }
        for (Epic epic : epics.values()) {
            System.out.println(epic);
        }
    }

    @Override
    public void printSubtasks() {
        if (subtasks.isEmpty()) {
            System.out.println("Список подзадач пуст");
            return;
        }
        for (Subtask subtask : subtasks.values()) {
            System.out.println(subtask);
        }
    }

    @Override
    public void printTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
            return;
        }
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}

