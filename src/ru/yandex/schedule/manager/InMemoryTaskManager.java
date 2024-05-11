package ru.yandex.schedule.manager;

import ru.yandex.schedule.resources.Managers;
import ru.yandex.schedule.resources.Status;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private final TreeSet<Task> sortedTasks;
    protected int idSequence;
    protected final HistoryManager historyManager = Managers.getDefaultHistoryManager();

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        sortedTasks = new TreeSet<>((task1, task2) -> {
            if (task1.getEndTime().isBefore(task2.getStartTime())) {
                return -1;
            } else if (task1.getStartTime().isAfter(task2.getEndTime())) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    protected Integer putInTaskHashMap(Task task) {
        if (task != null && checkTaskForOverlapping(task)) {
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
            sortedTasks.remove(tasks.remove(id));
        } else {
            System.out.println("Задача отсутствует");
        }
    }

    protected Integer putInSubtaskHashMap(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null && checkTaskForOverlapping(subtask)) {
            subtasks.put(subtask.getId(), subtask);
            epic.setSubtaskIds(subtask.getId());
            updateStatusEpic(epic);
            updateEpicTime(subtask);
            return subtask.getId();
        } else {
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
            updateEpicTime(subtask);
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
        if (epics.containsKey(id)) {
            historyManager.remove(id);
            for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
                deleteSubtask(subtaskId);
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
        new ArrayList<>(tasks.keySet()).forEach(this::deleteTask);
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        new ArrayList<>(subtasks.keySet()).forEach(this::deleteSubtask);
        new ArrayList<>(subtasks.values()).forEach(this::updateEpicTime);
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        new ArrayList<>(epics.keySet()).forEach(this::getEpicById);
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
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksList() {
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

    @Override
    public TreeSet<Task> getSortedTasks() {
        return new TreeSet<>(sortedTasks);
    }

    public boolean checkTaskForOverlapping(Task task) {
        if (task.getStartTime() != null && task.getEndTime() != null) {
            int sizeBefore = sortedTasks.size();
            sortedTasks.add(task);
            return sortedTasks.size() != sizeBefore;
        } else {
            return false;
        }
    }
    private void updateEpicTime(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for (int id : epic.getSubtaskIds()) {
            subtasksOfEpic.add(subtasks.get(id));
        }
        subtasksOfEpic.stream().filter(sub -> sub.getStartTime() != null && sub.getDuration() != 0)
                .map(Task::getStartTime).min(LocalDateTime::compareTo).ifPresent(epic::setStartTime);
        subtasksOfEpic.stream().filter(sub -> sub.getStartTime() != null && sub.getDuration() != 0)
                .map(Task::getEndTime).max(LocalDateTime::compareTo).ifPresent(epic::setEndTime);
        Optional<Long> sumOfDuration = subtasksOfEpic.stream()
                .filter(sub -> sub.getStartTime() != null && sub.getDuration() != 0)
                .map(Task::getDuration).reduce(Long::sum);
        sumOfDuration.ifPresent(epic::setDuration);
    }
}

