public class DecisionState {
    private String lastClass;   // HOT / WARM / COLD
    private long lastUpdated;

    public DecisionState(String lastClass, long lastUpdated) {
        this.lastClass = lastClass;
        this.lastUpdated = lastUpdated;
    }

    public String getLastClass() {
        return lastClass;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void update(String newClass) {
        this.lastClass = newClass;
        this.lastUpdated = System.currentTimeMillis();
    }
}
