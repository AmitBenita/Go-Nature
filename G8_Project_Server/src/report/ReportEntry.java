package report;

import java.io.Serializable;

/**
 * Represents an entry in the report, containing the park name and the number of visitors for each category.
 */
public class ReportEntry implements Serializable {
    private String parkName;
    private int individualVisitors;
    private int groupVisitors;
    private int memberVisitors;

    /**
     * Constructs a new ReportEntry object with the given park name and visitor counts.
     *
     * @param parkName           the name of the park
     * @param individualVisitors the number of individual visitors
     * @param groupVisitors      the number of group visitors
     * @param memberVisitors     the number of member visitors
     */
    public ReportEntry(String parkName, int individualVisitors, int groupVisitors, int memberVisitors) {
        this.parkName = parkName;
        this.individualVisitors = individualVisitors;
        this.groupVisitors = groupVisitors;
        this.memberVisitors = memberVisitors;
    }

    /**
     * Returns the name of the park.
     *
     * @return the park name
     */
    public String getParkName() {
        return parkName;
    }

    /**
     * Returns the number of individual visitors.
     *
     * @return the number of individual visitors
     */
    public int getIndividualVisitors() {
        return individualVisitors;
    }

    /**
     * Returns the number of group visitors.
     *
     * @return the number of group visitors
     */
    public int getGroupVisitors() {
        return groupVisitors;
    }

    /**
     * Returns the number of member visitors.
     *
     * @return the number of member visitors
     */
    public int getMemberVisitors() {
        return memberVisitors;
    }
}