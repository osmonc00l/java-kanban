package ru.yandex.schedule.httpserver.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.Subtask;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.resources.Status;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TaskAdapter extends TypeAdapter<Task> {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, Task task) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("id");
        jsonWriter.value(task.getId());
        if (task instanceof Subtask) {
            jsonWriter.name("epicID");
            jsonWriter.value(((Subtask) task).getEpicId());
        }
        jsonWriter.name("name");
        jsonWriter.value(task.getName());
        jsonWriter.name("description");
        jsonWriter.value(task.getDescription());
        jsonWriter.name("status");
        if (Objects.nonNull(task.getStatus())) {
            jsonWriter.value(task.getStatus().toString());
        } else {
            jsonWriter.nullValue();
        }
        jsonWriter.name("startTime");
        if (Objects.nonNull(task.getStartTime())) {
            jsonWriter.value(task.getStartTime().format(dateTimeFormatter));
        } else {
            jsonWriter.nullValue();
        }
        jsonWriter.name("duration");
        jsonWriter.value(task.getDuration());
        jsonWriter.name("type");
        if (task instanceof Epic) {
            jsonWriter.value("epic");
        } else if (task instanceof Subtask) {
            jsonWriter.value("subtask");
        } else {
            jsonWriter.value("task");
        }
        jsonWriter.endObject();
    }

    @Override
    public Task read(JsonReader jsonReader) throws IOException {
        Integer id = null;
        Integer epicID = null;
        String name = null;
        String description = null;
        Status status = null;
        LocalDateTime startTime = null;
        Long duration = null;
        String type = "";
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String fieldName = jsonReader.nextName();
            String value = jsonReader.nextString();
            switch (fieldName) {
                case "epicID":
                    epicID = Integer.valueOf(value);
                    break;
                case "id":
                    id = Integer.valueOf(value);
                    break;
                case "name":
                    name = value;
                    break;
                case "description":
                    description = value;
                    break;
                case "status":
                    status = Status.valueOf(value);
                    break;
                case "startTime":
                    startTime = LocalDateTime.parse(value, dateTimeFormatter);
                    break;
                case "duration":
                    duration = Long.valueOf(value);
                    break;
                case "type":
                    type = value;
                    break;
            }
        }

        jsonReader.endObject();

        switch (type) {
            case "task":
                return new Task(name, description, id, status,  startTime, duration);
            case "subtask":
                return new Subtask(name, description, id, status, epicID, startTime, duration);
            default:
                return new Epic(name, description, id);
        }
    }
}
