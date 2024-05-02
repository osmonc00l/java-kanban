package managers;

import org.junit.jupiter.api.Test;
import ru.yandex.schedule.manager.FileBackedTaskManager;
import ru.yandex.schedule.resources.Status;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest {
    @Test
    void shouldBePositiveIfEmptyFileSaveAndLoadSuccess () throws IOException {
        try {
            File temporaryFile = File.createTempFile("data", "csv");
            File temporaryFile2 = File.createTempFile("temp1","csv", new File( "/resources") );
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
            Task task = new Task("task", "description");
            Epic epic = new Epic("epic","epicDescription");

            FileBackedTaskManager fb = new FileBackedTaskManager(file);
            assertNotNull(fb);

            fb.addTask(task);
            fb.getTaskById(task.getId());
            fb.addEpic(epic);
            fb.getEpicById(epic.getId());
            Subtask subtask = new Subtask("subtask", "subtaskDescription",
                    Status.NEW, epic.getId());
            fb.addSubtask(subtask);
            fb.getSubtaskById(subtask.getId());

            String expectedData = "id,type,name,status,description,epic\n" +
                    "1,TASK,task,NEW,description\n" +
                    "2,EPIC,epic,NEW,epicDescription\n" +
                    "3,SUBTASK,subtask,NEW,subtaskDescription,2" + "\n\n" + "1,2,3";

            String lines = fb.toString();
            assertEquals(expectedData, lines);
            FileBackedTaskManager fb1 = FileBackedTaskManager.loadFromFile(file.toPath());
            assertEquals(fb.toString(), fb1.toString());

        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    @Test
    void Test3() throws IOException {
        try {
            File file = File.createTempFile("data", "csv");
            Task task = new Task("task1", "desc1");
            Epic epic = new Epic("epic1","epicDesk");

            FileBackedTaskManager fb = new FileBackedTaskManager(file);
            assertNotNull(fb);

            fb.addTask(task);
            fb.getTaskById(task.getId());
            fb.addEpic(epic);
            fb.getEpicById(epic.getId());
            Subtask subtask = new Subtask("subtask1", "subtaskDesc",
                    Status.NEW, epic.getId());
            fb.addSubtask(subtask);
            fb.getSubtaskById(subtask.getId());
            fb.getEpicById(epic.getId());
            FileBackedTaskManager fb1 = FileBackedTaskManager.loadFromFile(file.toPath());
            assertEquals(fb.getAllTasks(), fb1.getAllTasks());
            assertEquals(fb.getAllEpics(), fb1.getAllEpics());
            assertEquals(fb.getAllSubtasks(), fb1.getAllSubtasks());

        } catch (IOException e) {
            e.getStackTrace();
        }
    }
}
