package ru.yandex.schedule.tasks;

import ru.yandex.schedule.resources.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public ArrayList<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void setSubtaskIds(int id) {
        subtaskIds.add(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                ", startTime=" + super.getStartTime() +
                ", duration=" + super.getDuration() +
                '}';
    }

    public void deleteSubtask(Integer id) {
        subtaskIds.remove(id);
    }

    public void deleteAllSubtasksOfEpic() {
        subtaskIds.clear();
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW, null, null);
    }

    public Epic(String name, String description, int id) {
        super(name, description, id, Status.NEW, null, null);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
