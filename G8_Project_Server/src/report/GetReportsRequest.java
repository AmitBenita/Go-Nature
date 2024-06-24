package report;

import java.io.Serializable;

public class GetReportsRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String parkName;
    
    public GetReportsRequest(String parkName) {
        this.parkName = parkName;
    }
    
    public String getParkName() {
        return parkName;
    }
    
    public void setParkName(String parkName) {
        this.parkName = parkName;
    }
}