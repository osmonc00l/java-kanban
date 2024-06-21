package ru.yandex.schedule.httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.schedule.httpserver.tokens.TaskTreeSetTypeToken;
import ru.yandex.schedule.manager.TaskManager;
import ru.yandex.schedule.tasks.Task;

import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeSet;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void deleteMethodHandler(HttpExchange httpExchange, LinkedList<String> paths) throws IOException {
        sendNotFound(httpExchange);
    }

    @Override
    protected void postMethodHandler(HttpExchange httpExchange, LinkedList<String> paths) throws IOException {
        sendNotFound(httpExchange);
    }

    @Override
    protected void getMethodHandler(HttpExchange httpExchange, LinkedList<String> paths) throws IOException {
        TreeSet<Task> prioritized = getTaskManager().getSortedTasks();
        String prioritizedJson = getGson().toJson(prioritized, new TaskTreeSetTypeToken().getType());

        sendText(httpExchange, 200, prioritizedJson);
    }
}
