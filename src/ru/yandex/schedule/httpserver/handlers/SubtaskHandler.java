package ru.yandex.schedule.httpserver.handlers;

import ru.yandex.schedule.httpserver.exception.BadRequest;
import ru.yandex.schedule.manager.TaskManager;
import ru.yandex.schedule.tasks.Subtask;

import java.util.Objects;

public class SubtaskHandler extends BaseTaskHandler<Subtask> {
    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager, taskManager::addSubtask, taskManager::updateSubtask, taskManager::getSubtaskById,
                taskManager::getSubtasksList, taskManager::deleteSubtask, Subtask.class);
    }

    @Override
    protected void validate(Subtask subtask) throws BadRequest {
        if (Objects.isNull(subtask.getEpicId()) || Objects.isNull(subtask.getName())
                || Objects.isNull(subtask.getDescription()) || Objects.isNull(subtask.getStatus())
                || Objects.isNull(subtask.getDuration()) != Objects.isNull(subtask.getStartTime())) {
            throw new BadRequest("Bad request parameters for subtask");
        }
    }
}