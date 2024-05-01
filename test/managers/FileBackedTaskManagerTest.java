package managers;

import org.junit.jupiter.api.Test;
import ru.yandex.schedule.manager.FileBackedTaskManager;
import ru.yandex.schedule.resources.Status;
import ru.yandex.schedule.resources.TaskTypes;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    @Test
    void shouldBePositiveIfEmptyFileSaveAndLoadSuccess () throws IOException {
        try {
            File file = File.createTempFile("temp","csv", new File( "/resources") );
            File file1 = File.createTempFile("temp1","csv", new File( "/resources") );
            FileBackedTaskManager fb = new FileBackedTaskManager(file.toPath());
            assertNotNull(fb);
            FileBackedTaskManager fb1 = FileBackedTaskManager.loadFromFile(file1);
            assertNotNull(fb1);
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    @Test
    void shouldBePositiveIfTasksSaveIsCorrect() throws IOException {
        try {
            File file = File.createTempFile("temp","csv", new File( "/resources") );
            Task task = new Task("task", "description");
            Epic epic = new Epic("epic","epicDescription");
            Subtask subtask = new Subtask("subtask", "subtaskDescription",
                    Status.NEW, epic.getId());
            FileBackedTaskManager fb = new FileBackedTaskManager(file.toPath());
            assertNotNull(fb);

            fb.addTask(task);
            fb.getTaskById(task.getId());
            fb.addEpic(epic);
            fb.getEpicById(epic.getId());
            fb.addSubtask(subtask);
            fb.getSubtaskById(subtask.getId());

            String refer = String.valueOf(task.getId()) + TaskTypes.TASK + task.getName()
                    + task.getStatus() + task.getDescription();
            refer = refer + String.valueOf(epic.getId()) + TaskTypes.EPIC + epic.getName()
                    + epic.getStatus() + epic.getDescription();
            refer = refer + String.valueOf(subtask.getId()) + TaskTypes.SUBTASK + subtask.getName()
                    + subtask.getStatus() + subtask.getDescription()
                    + String.valueOf(subtask.getEpicId());
            refer = refer + String.valueOf(task.getId()) + String.valueOf(epic.getId())
                    + String.valueOf(subtask.getId());
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String test = "";
            while (bf.ready()) {
                test = bf.readLine();
            }
            bf.close();
            assertEquals(refer,test);

            fb.deleteEpic(epic.getId());
            refer = String.valueOf(task.getId()) + TaskTypes.TASK + task.getName()
                    + task.getStatus() + task.getDescription() + String.valueOf(task.getId());

            bf = new BufferedReader(new FileReader(file));
            test = "";
            while (bf.ready()) {
                test = bf.readLine();
            }
            bf.close();
            assertEquals(refer,test);

            task.setStatus(Status.DONE);
            fb.updateTask(task);
            refer = String.valueOf(task.getId()) + TaskTypes.TASK + task.getName()
                    + Status.DONE + task.getDescription() + String.valueOf(task.getId());

            bf = new BufferedReader(new FileReader(file));
            test = "";
            while (bf.ready()) {
                test = bf.readLine();
            }
            bf.close();
            assertEquals(refer,test);
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    @Test
    void Test3() throws IOException {
        try {
            File file = File.createTempFile("temp","csv", new File( "/resources") );
            Task task = new Task("task1", "desc1");
            Epic epic = new Epic("epic1","epicDesk");
            Subtask subtask = new Subtask("subtask1", "subtaskDesc",
                    Status.NEW, epic.getId());
            FileBackedTaskManager fb = new FileBackedTaskManager(file.toPath());
            assertNotNull(fb);

            fb.addTask(task);
            fb.getTaskById(task.getId());
            fb.addEpic(epic);
            fb.getEpicById(epic.getId());
            fb.addSubtask(subtask);
            fb.getSubtaskById(subtask.getId());

            FileBackedTaskManager fb1 = FileBackedTaskManager.loadFromFile(file);
            assertEquals(fb.getAllTasks(), fb1.getAllTasks());
            assertEquals(fb.getAllEpics(), fb1.getAllEpics());
            assertEquals(fb.getAllSubtasks(), fb1.getAllSubtasks());

        } catch (IOException e) {
            e.getStackTrace();
        }
    }
}
