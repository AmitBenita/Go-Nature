package report;

import java.io.Serializable;
import java.time.LocalDate;

public class ReportNumberOfVisitorsRequest implements Serializable {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String parkName;

    public ReportNumberOfVisitorsRequest(LocalDate fromDate, LocalDate toDate,String parkName) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.parkName=parkName;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }
    public String getParkName() {
        return parkName;
    }

    public LocalDate getToDate() {
        return toDate;
    }
}