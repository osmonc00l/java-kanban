package managers;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.schedule.httpserver.HttpTaskServer;
import ru.yandex.schedule.httpserver.handlers.BaseHttpHandler;
import ru.yandex.schedule.httpserver.tokens.TaskListTypeToken;
import ru.yandex.schedule.httpserver.tokens.TaskTreeSetTypeToken;
import ru.yandex.schedule.manager.InMemoryTaskManager;
import ru.yandex.schedule.manager.TaskManager;
import ru.yandex.schedule.resources.Status;
import ru.yandex.schedule.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

    public class HttpTaskServerTest {
        Supplier<TaskManager> taskManagerSupplier = InMemoryTaskManager::new;
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManagerSupplier);
        TaskManager taskManager = httpTaskServer.getTaskManager();
        Gson gson = BaseHttpHandler.getGson();

        public HttpTaskServerTest() throws IOException {
        }

        @BeforeEach
        public void setUp() {
            taskManager.deleteAllTasks();
            taskManager.deleteAllSubtasks();
            taskManager.deleteAllEpics();
            httpTaskServer.start();
        }

        @AfterEach
        public void shutDown() {
            httpTaskServer.stop(0);
        }

        @Test
        public void testCreateTask() throws IOException, InterruptedException {
            Task task = new Task("task", "description", Status.NEW, null, null);
            String taskJson = gson.toJson(task, Task.class);

            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create("http://localhost:8080/tasks/");
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson)).uri(uri).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, response.statusCode());

            List<Task> tasksFromManager = taskManager.getTasksList();

            assertNotNull(tasksFromManager, "task");
            assertEquals(1, tasksFromManager.size());
            assertEquals("task", tasksFromManager.get(0).getName());
            assertEquals(201, response.statusCode());
        }

        @Test
        public void testGetTaskById() throws IOException, InterruptedException {
            Task firstTask = new Task("First task", "First tasks description", Status.NEW);
            Task secondTask = new Task("Second Task", "Second tasks description", Status.NEW);
            Task thirdTask = new Task("Third task", "Third tasks description", Status.NEW);


            taskManager.addTask(firstTask);
            taskManager.addTask(secondTask);
            taskManager.addTask(thirdTask);

            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create("http://localhost:8080/tasks/" + secondTask.getId().toString());
            HttpRequest request = HttpRequest.newBuilder()
                    .GET().uri(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Task secondEpicFromJson = gson.fromJson(response.body(), Task.class);

            assertEquals(secondEpicFromJson, secondTask);
            assertEquals(200, response.statusCode());
        }
        @Test
        public void testGetListOfTasks() throws IOException, InterruptedException {
            Task firstTask = new Task("First task", "First tasks description", Status.NEW);
            Task secondTask = new Task("Second Task", "Second tasks description", Status.NEW);
            Task thirdTask = new Task("Third task", "Third tasks description", Status.NEW);


            taskManager.addTask(firstTask);
            taskManager.addTask(secondTask);
            taskManager.addTask(thirdTask);

            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create("http://localhost:8080/tasks/");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET().uri(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            List<Task> tasksFromJson = gson.fromJson(response.body(), new TaskListTypeToken().getType());
            List<Task> tasksFromManager = taskManager.getTasksList();

            assertEquals(tasksFromJson, tasksFromManager);
            assertEquals(200, response.statusCode());
        }

        @Test
        public void testHistoryFromHttpServer() throws IOException, InterruptedException {
            Task firstTask = new Task("First task", "Description first task",
                    Status.NEW, LocalDateTime.now(), 10L);
            Task secondTask = new Task("Second task", "Description second task",
                    Status.NEW, LocalDateTime.now().plusHours(1), 10L);
            Task thirdTask = new Task("Third task", "Description third task",
                    Status.NEW, LocalDateTime.now().plusHours(5), 10L);

            taskManager.addTask(firstTask);
            taskManager.addTask(secondTask);
            taskManager.addTask(thirdTask);

            taskManager.getTaskById(thirdTask.getId());
            taskManager.getTaskById(secondTask.getId());
            taskManager.getTaskById(firstTask.getId());

            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create("http://localhost:8080/history/");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET().uri(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
            System.out.println(response.body());

            List<Task> taskListFromJson = gson.fromJson(response.body(), new TaskListTypeToken().getType());
            List<Task> taskListFromManager = taskManager.getHistory();
            assertEquals(taskListFromManager, taskListFromJson);
        }

        @Test
        public void testPrioritizedFromHttpServer() throws IOException, InterruptedException {
            Task firstTask = new Task("First task", "Description first task",
                    Status.NEW, LocalDateTime.now(), 10L);
            Task secondTask = new Task("Second task", "Description second task",
                    Status.NEW, LocalDateTime.now().plusHours(1), 10L);
            Task thirdTask = new Task("Third task", "Description third task",
                    Status.NEW, LocalDateTime.now().plusHours(2), 10L);

            taskManager.addTask(firstTask);
            taskManager.addTask(secondTask);
            taskManager.addTask(thirdTask);
            System.out.println(taskManager.getSortedTasks());
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create("http://localhost:8080/prioritized/");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET().uri(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
            System.out.println();
            TreeSet<Task> prioritizedFromManager = taskManager.getSortedTasks();
            TreeSet<Task> prioritizedFromJson = gson.fromJson(response.body(), new TaskTreeSetTypeToken().getType());

            assertEquals(prioritizedFromManager, prioritizedFromJson);
        }
    }