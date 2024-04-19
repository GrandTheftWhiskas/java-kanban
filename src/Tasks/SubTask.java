package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epic;

    private Type type = Type.SUBTASK;
    private LocalDateTime startTime;
    private Duration duration;

    public SubTask(String name, Status status, String description,
                   LocalDateTime startTime, Duration duration, int epic) {
        super(name, status, description, startTime, duration);
        this.epic = epic;
    }

    public int getEpic() {
        return epic;
    }

    public void setEpic(int epic) {
        this.epic = epic;
    }

    @Override
    public Type getType() {
        return type;
    }

}

