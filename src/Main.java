import ru.yandex.schedule.manager.TaskManager;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Status;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Task task1 = new Task("First task", "desc-1", Status.NEW);
        Task task2 = new Task("Second task", "desc-2", Status.NEW);
        Epic epic1 = new Epic("First epic", "desc-1");
        Epic epic2 = new Epic("Second epic", "desc-2");
        Subtask subtask1 = new Subtask("first subtask", "d", Status.DONE, 1);
        Subtask subtask2 = new Subtask("second subtask", "d", Status.NEW, 1);
        Subtask subtask3 = new Subtask("third subtask", "d", Status.NEW, 2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.addTask(task1);
        manager.addTask(task2);

        System.out.println(epic1);
        System.out.println(epic2);
        manager.printSubtasks();
        manager.printEpics();


    }
}
