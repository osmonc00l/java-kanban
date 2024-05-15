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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File filePath;

    public FileBackedTaskManager(File filePath) {
        this.filePath = filePath;
    }

    private static final String HEAD = "id,type,name,status,description,epic";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

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
                + task.getDescription() + "," + task.getStartTime().format(formatter) + "," + task.getDuration();
    }

    public String epicToString(Epic epic) {
        String startTime = epic.getStartTime() != null ? epic.getStartTime().format(formatter) : "null";
        String duration = epic.getDuration() != 0 ? String.valueOf(epic.getDuration()) : "null";
        return epic.getId() + "," + TaskTypes.EPIC + "," + epic.getName() + "," + epic.getStatus() + ","
                + epic.getDescription() + "," + startTime + "," + duration;
    }

    public String subtaskToString(Subtask subtask) {
        String startTime = subtask.getStartTime() != null ? subtask.getStartTime().format(formatter) : "null";
        String duration = subtask.getDuration() != 0 ? String.valueOf(subtask.getDuration()) : "null";
        return subtask.getId() + "," + TaskTypes.SUBTASK + "," + subtask.getName() + "," + subtask.getStatus() + ","
                + subtask.getDescription() + "," + subtask.getEpicId() + ","
                + startTime + "," + duration;
    }

    static String historyToString(HistoryManager historyManager) {
        return historyManager.getHistory().stream().map(Task::getId).map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public void fromString(String line) {
        String[] lines = line.split(",");
        TaskTypes type = TaskTypes.valueOf(lines[1]);
        idSequence = Math.max(idSequence, Integer.parseInt(lines[0]) + 1);
        switch (type) {
            case TASK:
                putInTaskHashMap(new Task(lines[2], lines[4], Integer.parseInt(lines[0]), Status.valueOf(lines[3]),
                        LocalDateTime.parse(lines[5], formatter), Long.parseLong(lines[6])));
                break;
            case EPIC:
                putInEpicHashMap(new Epic(lines[2], lines[4], Integer.parseInt(lines[0])));
                break;
            case SUBTASK:
                putInSubtaskHashMap(new Subtask(lines[2], lines[4], Integer.parseInt(lines[0]), Status.valueOf(lines[3]),
                        Integer.parseInt(lines[5]), LocalDateTime.parse(lines[6], formatter), Long.parseLong(lines[7])));
                break;
        }
    }

    @Override
    public String toString() {
        List<String> elements = new ArrayList<>();
        elements.add(HEAD);
        for (Task task : getTasksList()) {
            elements.add(taskToString(task));
        }
        for (Epic epic : getEpicsList()) {
            elements.add(epicToString(epic));
        }
        for (Subtask subtask : getSubtasksList()) {
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
         try {
             FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(dataFile.toFile());
             LinkedList<String> readLinesFromFile = new LinkedList<>();
             BufferedReader bufferedReader = new BufferedReader(
                     new FileReader(dataFile.toString(), StandardCharsets.UTF_8));
             if (bufferedReader.ready()) {
                 bufferedReader.readLine();
             }
             while (bufferedReader.ready()) {
                 readLinesFromFile.add(bufferedReader.readLine());
             }


             String historyLine = readLinesFromFile.pollLast();

             for (String line : readLinesFromFile) {
                 if (!line.isEmpty()) {
                     fileBackedTaskManager.fromString(line);
                 }
             }

             if (Objects.nonNull(historyLine)) {
                 historyFromString(historyLine).stream()
                         .peek(fileBackedTaskManager::getTaskById)
                         .peek(fileBackedTaskManager::getEpicById)
                         .forEach(fileBackedTaskManager::getSubtaskById);
             }
             return fileBackedTaskManager;
         } catch (IOException e) {
             throw new RuntimeException(e.getMessage());
         }
    }

    static List<Integer> historyFromString(String value) {
        return Arrays.stream(value.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    public static void main(String[] args) {
    }
}
