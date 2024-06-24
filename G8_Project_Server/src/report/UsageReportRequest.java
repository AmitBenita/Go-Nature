package report;

import java.io.Serializable;
import java.time.LocalDate;

public class UsageReportRequest implements Serializable {
    private LocalDate date;
    private String parkName;

    public UsageReportRequest(LocalDate date, String parkName) {
        this.date = date;
        this.parkName = parkName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }
}