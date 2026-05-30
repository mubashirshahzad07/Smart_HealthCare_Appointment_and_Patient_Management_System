package patient.management.system.dto;

public class EmergencyReportDTO {

    private String triageColor;
    private int totalCases;
    private int completed;
    private int movedToICU;
    private int deceased;

    public EmergencyReportDTO() {}

    public EmergencyReportDTO(String triageColor,
                              int totalCases,
                              int completed,
                              int movedToICU,
                              int deceased) {
        this.triageColor = triageColor;
        this.totalCases = totalCases;
        this.completed = completed;
        this.movedToICU = movedToICU;
        this.deceased = deceased;
    }

    public String getTriageColor() {
        return triageColor;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public int getCompleted() {
        return completed;
    }

    public int getMovedToICU() {
        return movedToICU;
    }

    public int getDeceased() {
        return deceased;
    }
}