package ru.yandex.schedule.tasks;

import ru.yandex.schedule.resources.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description, int id, Status status, LocalDateTime startTime, Long duration) {
        setName(name);
        setDescription(description);
        setId(id);
        setStatus(status);
        setStartTime(startTime);
        setDuration(duration);
    }

    public Task(String name, String description, Status status) {
        this(name, description, 0, status, null, null);
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Long duration) {
        this(name, description, 0, status, startTime, duration);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }


    public void setDuration(Long duration) {
        if (duration != null) {
            this.duration = Duration.ofMinutes(duration);
        }
    }

    public long getDuration() {
        if (duration != null) {
            return duration.toMinutes();
        } else {
            return 0;
        }

    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }
}
