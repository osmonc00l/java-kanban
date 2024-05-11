package ru.yandex.schedule.tasks;

import ru.yandex.schedule.resources.Status;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "EpicId=" + epicId +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", startTime=" + super.getStartTime() +
                ", duration=" + super.getDuration() +
                '}';
    }



    public Subtask(String name, String description, Status status, int epicId, LocalDateTime startTime, Long duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status, null, null);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int id, Status status, int epicId, LocalDateTime startTime,
                   Long duration) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
    }
}
