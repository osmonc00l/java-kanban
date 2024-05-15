package managers;

import org.junit.jupiter.api.Test;
import ru.yandex.schedule.manager.FileBackedTaskManager;
import ru.yandex.schedule.resources.Status;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{
    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            File temporaryFile = File.createTempFile("data", "csv");
            return FileBackedTaskManager.loadFromFile(temporaryFile.toPath());
        } catch (IOException e) {
            e.getStackTrace();
        }
        return null;
    }
    @Test
    void shouldBePositiveIfEmptyFileSaveAndLoadSuccess (){
        try {
            File temporaryFile = File.createTempFile("data", "csv");
            FileBackedTaskManager fb = FileBackedTaskManager.loadFromFile(temporaryFile.toPath());
            assertNotNull(fb);
            FileBackedTaskManager fb1 = FileBackedTaskManager.loadFromFile(temporaryFile.toPath());
            assertNotNull(fb1);
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    @Test
    void shouldBePositiveIfTasksSaveIsCorrect() {
        try {
            File file = File.createTempFile("data", "csv");
            Task task = new Task("Задача", "Описание задачи", Status.NEW,
                    LocalDateTime.of(2023, 9, 2, 7, 0),
                    Duration.ofMinutes(30).toMinutes());
            Epic epic = new Epic("Эпик", "Описание эпика");

            FileBackedTaskManager fb = new FileBackedTaskManager(file);
            assertNotNull(fb);

            fb.addTask(task);
            fb.getTaskById(task.getId());
            fb.addEpic(epic);
            fb.getEpicById(epic.getId());
            Subtask subtask = new Subtask("Подзадача",
                    "Описание подзадачи", Status.NEW, epic.getId(),
                    LocalDateTime.of(2023, 10, 6, 12, 0),
                    Duration.ofMinutes(30).toMinutes());
            fb.addSubtask(subtask);
            fb.getSubtaskById(subtask.getId());
            String expectedData = """
                    id,type,name,status,description,epic
                    1,TASK,Задача,NEW,Описание задачи,02.09.2023 07:00,30
                    2,EPIC,Эпик,NEW,Описание эпика,06.10.2023 12:00,30
                    3,SUBTASK,Подзадача,NEW,Описание подзадачи,2,06.10.2023 12:00,30

                    1,2,3""";

            String lines = fb.toString();
            assertEquals(expectedData, lines);
            FileBackedTaskManager fb1 = FileBackedTaskManager.loadFromFile(file.toPath());
            assertEquals(fb.toString(), fb1.toString());
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    @Test
    void shouldBePositiveIfTasksAreEqual() {
        try {
            File file = File.createTempFile("data", "csv");
            Epic epic = new Epic("Эпик", "Описание эпика");

            FileBackedTaskManager fb = new FileBackedTaskManager(file);
            assertNotNull(fb);
            Task task = new Task("Задача", "Описание задачи", Status.NEW,
                    LocalDateTime.of(2023, 9, 2, 7, 0),
                    Duration.ofMinutes(30).toMinutes());
            fb.addTask(task);
            fb.addEpic(epic);
            fb.getEpicById(epic.getId());
            Subtask subtask = new Subtask("Подзадача",
                    "Описание подзадачи", Status.NEW, epic.getId(),
                    LocalDateTime.of(2023, 10, 6, 12, 0),
                    Duration.ofMinutes(30).toMinutes());
            fb.addSubtask(subtask);
            fb.getEpicById(epic.getId());
            fb.getTaskById(task.getId());
            fb.getSubtaskById(subtask.getId());
            FileBackedTaskManager fb1 = FileBackedTaskManager.loadFromFile(file.toPath());
            assertEquals(fb.getTasksList(), fb1.getTasksList());
            assertEquals(fb.getEpicsList(), fb1.getEpicsList());
            assertEquals(fb.getSubtasksList(), fb1.getSubtasksList());
            assertEquals(fb.toString(), fb1.toString());

        } catch (IOException e) {
            e.getStackTrace();
        }
    }
}
