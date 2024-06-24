package report;

public class Report {
    private int reportId;
    private String parkName;
    private String date;
    private String type;
    private String details;

    public Report(int reportId, String parkName, String date, String type, String details) {
        this.reportId = reportId;
        this.parkName = parkName;
        this.date = date;
        this.type = type;
        this.details = details;    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}