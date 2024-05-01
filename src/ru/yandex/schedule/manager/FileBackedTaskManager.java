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
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path filePath;

    public FileBackedTaskManager(Path filePath) {
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

    public String toString(Task task) {
        return task.getId() + "," + TaskTypes.TASK + "," + task.getName() + ","
                + task.getDescription() + "," + task.getStatus();
    }

    public String toString(Epic epic) {
        return epic.getId() + "," + TaskTypes.EPIC + "," + epic.getName() + ","
                + epic.getDescription() + "," + epic.getStatus();
    }

    public String toString(Subtask subtask) {
        return subtask.getId() + "," + TaskTypes.SUBTASK + "," + subtask.getName() + ","
                + subtask.getDescription() + "," + subtask.getStatus() + "," + subtask.getEpicId();
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

        switch (type) {
            case TASK:
                putInTaskHashMap(new Task(lines[2], lines[4], Integer.parseInt(lines[0]), Status.valueOf(lines[3])));
                break;
            case EPIC:
                putInEpicHashMap(new Epic(lines[2], lines[4], Integer.parseInt(lines[0])));
                break;
            case SUBTASK:
                putInSubTaskHashMap(new Subtask(lines[2], lines[4], Integer.parseInt(lines[0]), Status.valueOf(lines[3]),
                        Integer.parseInt(lines[5])));
                break;
        }
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter(filePath.toString(), StandardCharsets.UTF_8))) {
            bufferedWriter.write(toString());
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file.toPath());
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (br.ready()) {
                String line = br.readLine();
                if (!line.equals(HEAD)) {
                    fileManager.fromString(line);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        return fileManager;
    }
}
