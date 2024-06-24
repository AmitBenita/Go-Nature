package report;

import java.io.Serializable;

public class UsageReportEntry implements Serializable {
    private String time;
    private double occupancyPercentage;

    public UsageReportEntry(String time, double occupancyPercentage) {
        this.time = time;
        this.occupancyPercentage = occupancyPercentage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getOccupancyPercentage() {
        return occupancyPercentage;
    }

    public void setOccupancyPercentage(double occupancyPercentage) {
        this.occupancyPercentage = occupancyPercentage;
    }
}