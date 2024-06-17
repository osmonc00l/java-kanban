package ru.yandex.schedule.httpserver;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.schedule.httpserver.handlers.*;
import ru.yandex.schedule.manager.FileBackedTaskManager;
import ru.yandex.schedule.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class HttpTaskServer{
    private final HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer(Supplier<TaskManager> supplier) throws IOException {
        taskManager = supplier.get();
        httpServer = HttpServer.create(new InetSocketAddress(80), 0);
        registerEndpoints();
    }

    private void registerEndpoints() {
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop(int delay) {
        httpServer.stop(delay);
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(
                () -> FileBackedTaskManager.loadFromFile(Paths.get("data.csv")));
        httpTaskServer.start();
    }
}
