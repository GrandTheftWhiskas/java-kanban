package tasks;

public class SubTask extends Task {
    private int epic;

    public SubTask(String name, Status status, Type type, String description, int epic) {
        super(name, status, type, description);
        this.epic = epic;
    }
    public int getEpic() {
        return epic;
    }

    public void setEpic(int epic) {
        this.epic = epic;
    }
}
