package tasks;

public class SubTask extends Task {
    private int epic;

    public SubTask(String name, Status status, String description, int epic) {
        super(name, status, description);
        this.epic = epic;
    }
    public int getEpic() {
        return epic;
    }
}
