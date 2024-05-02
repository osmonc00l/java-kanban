package ru.yandex.schedule.manager;

import ru.yandex.schedule.resources.ManagerSaveException;
import ru.yandex.schedule.resources.Status;
import ru.yandex.schedule.resources.TaskTypes;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File filePath;

    public FileBackedTaskManager(File filePath) {
        this.filePath = filePath;
    }

    private static final String HEAD = "id,type,name,status,description,epic";

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask updatedSubTask) {
        super.updateSubtask(updatedSubTask);
        save();
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask subtask = super.deleteSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    public String taskToString(Task task) {
        return task.getId() + "," + TaskTypes.TASK + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription();
    }

    public String epicToString(Epic epic) {
        return epic.getId() + "," + TaskTypes.EPIC + "," + epic.getName() + "," + epic.getStatus() + ","
                + epic.getDescription();
    }

    public String subtaskToString(Subtask subtask) {
        return subtask.getId() + "," + TaskTypes.SUBTASK + "," + subtask.getName() + "," + subtask.getStatus() + ","
                + subtask.getDescription() + "," + subtask.getEpicId();
    }

    static String historyToString(HistoryManager historyManager) {
        List<String> history = new ArrayList<>();

        for (Task task : historyManager.getHistory()) {
            history.add(String.valueOf(task.getId()));
        }
        return String.join(",", history);
    }

    public void fromString(String line) {
        String[] lines = line.split(",");
        TaskTypes type = TaskTypes.valueOf(lines[1]);
        idSequence = Math.max(idSequence, Integer.parseInt(lines[0]) + 1);
        switch (type) {
            case TASK:
                putInTaskHashMap(new Task(lines[2], lines[4], Integer.parseInt(lines[0]), Status.valueOf(lines[3])));
                break;
            case EPIC:
                putInEpicHashMap(new Epic(lines[2], lines[4], Integer.parseInt(lines[0])));
                break;
            case SUBTASK:
                putInSubtaskHashMap(new Subtask(lines[2], lines[4], Integer.parseInt(lines[0]), Status.valueOf(lines[3]),
                        Integer.parseInt(lines[5])));
                break;
        }
    }

    @Override
    public String toString() {
        List<String> elements = new ArrayList<>();
        String head = "id,type,name,status,description,epic";
        elements.add(head);
        for (Task task : getAllTasks()) {
            elements.add(taskToString(task));
        }
        for (Epic epic : getAllEpics()) {
            elements.add(epicToString(epic));
        }
        for (Subtask subtask : getAllSubtasks()) {
            elements.add(subtaskToString(subtask));
        }
        return String.format("%s%n%n%s", String.join("\n", elements), historyToString(historyManager));
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter(filePath.toString(), StandardCharsets.UTF_8))) {
            bufferedWriter.write(toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл");
        }
    }

    public static FileBackedTaskManager loadFromFile(Path dataFile) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(dataFile.toFile());
        LinkedList<String> readLinesFromFile = new LinkedList<>();
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(dataFile.toString(), StandardCharsets.UTF_8))) {
            if (bufferedReader.ready()) {
                bufferedReader.readLine();
            }
            while (bufferedReader.ready()) {
                readLinesFromFile.add(bufferedReader.readLine());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        String historyLine = readLinesFromFile.pollLast();

        for (String line : readLinesFromFile) {
            if (!line.isEmpty()) {
                fileBackedTaskManager.fromString(line);
            }
        }

        if (Objects.nonNull(historyLine)) {
            for (Integer element : historyFromString(historyLine)) {
                fileBackedTaskManager.getTaskById(element);
                fileBackedTaskManager.getEpicById(element);
                fileBackedTaskManager.getSubtaskById(element);
            }
        }
        return fileBackedTaskManager;
    }


    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        for (String element : value.split(",")) {
            history.add(Integer.parseInt(element));
        }
        return history;
    }
}
