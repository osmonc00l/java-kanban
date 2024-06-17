package ru.yandex.schedule.httpserver.handlers;

import ru.yandex.schedule.httpserver.exception.BadRequest;
import ru.yandex.schedule.manager.TaskManager;
import ru.yandex.schedule.tasks.Task;

import java.util.Objects;

public class TaskHandler extends BaseTaskHandler<Task>{
    public TaskHandler(TaskManager taskManager) {
        super(taskManager, taskManager::addTask, taskManager::updateTask,
                taskManager::getTaskById, taskManager::getTasksList, taskManager::deleteTask, Task.class);
    }

    @Override
    protected void validate(Task task) throws BadRequest {
        if (Objects.isNull(task.getName()) || Objects.isNull(task.getDescription())
                || Objects.isNull(task.getStatus())) {
            throw new BadRequest("Bad request parameters for task");
        }
    }
}
