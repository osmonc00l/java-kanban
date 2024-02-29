package ru.yandex.schedule.Manager.Tasks;

public class Subtask extends Task {
    private int epicId;

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "ru.yandex.schedule.Manager.Tasks.Subtask{" +
                "EpicId=" + epicId +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                '}';
    }



    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }
}