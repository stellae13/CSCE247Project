package enums;
public enum JobPostingStatus {
    NA("NA"),
    OPEN("Open"),
    PENDING("Pending"),
    CLOSED("Closed");

    private String status;
    private JobPostingStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
