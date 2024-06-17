/*package ru.yandex.schedule.httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.schedule.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

public class UserHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("GET")) {
            sendNotFound(httpExchange);
        }
        String path = httpExchange.getRequestURI().getPath();
        switch (path) {
            case "/history": {
                List<Task> history = manager.getHistory();
                String historyJson = gson.toJson(history);
                sendText(httpExchange, historyJson);
                break;
            }
            case "/prioritized": {
                TreeSet<Task> prioritizedTasks = manager.getSortedTasks();
                String prioritizedTasksJson = gson.toJson(prioritizedTasks);
                sendText(httpExchange, 200prioritizedTasksJson);
                break;
            }
            default: {
                sendNotFound(httpExchange);
            }
        }
    }
}*/
