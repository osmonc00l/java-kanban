package ru.yandex.schedule.httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.schedule.httpserver.exception.BadRequest;
import ru.yandex.schedule.manager.TaskManager;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Subtask;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EpicHandler extends BaseTaskHandler<Epic> {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager, taskManager::addEpic, taskManager::updateEpic,
                taskManager::getEpicById, taskManager::getEpicsList, taskManager::deleteEpic, Epic.class);
    }

    @Override
    protected void getMethodHandler(HttpExchange httpExchange, LinkedList<String> paths) throws IOException {
        super.getMethodHandler(httpExchange, paths);
        if (paths.size() == 4 && paths.pollLast().equals("subtasks")) {
            Integer epicId = Integer.parseInt(paths.get(2));

            getEpicSubtasks(httpExchange, epicId);
        }
    }

    public void getEpicSubtasks(HttpExchange httpExchange, Integer epicId) throws IOException {
        Optional<Epic> epic = getTaskManager().getEpicById(epicId);
        if (epic.isPresent()) {
            List<Subtask> subtaskList = getTaskManager().getSubtasksOfEpic(epic.get().getId());
            String subtasksJson = getGson().toJson(subtaskList);

            sendText(httpExchange, 200, subtasksJson);
        } else {
            sendNotFound(httpExchange);
        }
    }

    @Override
    protected void validate(Epic epic) throws BadRequest {
        if ((Objects.isNull(epic.getName()) || Objects.isNull(epic.getDescription()))
                || Objects.nonNull(epic.getStatus()) || Objects.nonNull(epic.getDuration())
                || Objects.nonNull(epic.getStartTime())) {
            throw new BadRequest("Bad request parameters for epic");
        }
    }
}