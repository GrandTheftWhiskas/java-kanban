package tasks;

public class SubTask extends Task {
    private int epic;

    private Type type = Type.SUBTASK;

    public SubTask(String name, Status status, String description, int epic) {
        super(name, status, description);
        this.epic = epic;
    }
    public int getEpic() {
        return epic;
    }

    public void setEpic(int epic) {
        this.epic = epic;
    }
}
