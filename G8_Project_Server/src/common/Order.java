package common;

import java.util.ArrayList;

public class Order {
    private String parkName;
    private String orderNumber;
    private String timeOfVisit;
    private String numOfVisitors;
    private String email;
    private String phoneNum;

    public Order(String parkName, String orderNumber, String timeOfVisit, String numOfVisitors, String email, String phoneNum) {
        setParkName(parkName);
        setOrderNumber(orderNumber);
        setTimeOfVisit(timeOfVisit);
        setNumOfVisitors(numOfVisitors);
        setEmail(email);
        setPhoneNum(phoneNum);
    }
    public Order(ArrayList<String> al) {
        if (al.size() >= 1) {
            setParkName(al.get(0));
        }
        if (al.size() >= 2) {
            setOrderNumber(al.get(1));
        }
        if (al.size() >= 3) {
            setTimeOfVisit(al.get(2));
        }
        if (al.size() >= 4) {
            setNumOfVisitors(al.get(3));
        }
        if (al.size() >= 5) {
        	setPhoneNum(al.get(4));
        }
        if (al.size() >= 6) {
        	setEmail(al.get(5));
        }
    }

//    public Order(ArrayList<String> al) {
//        setParkName(al.get(0));
//        setOrderNumber(al.get(1));
//        setTimeOfVisit(al.get(2));
//        setNumOfVisitors(al.get(3));
//        setEmail(al.get(4));
//        setPhoneNum(al.get(5));
//    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTimeOfVisit() {
        return timeOfVisit;
    }

    public void setTimeOfVisit(String timeOfVisit) {
        this.timeOfVisit = timeOfVisit;
    }

    public String getNumOfVisitors() {
        return numOfVisitors;
    }

    public void setNumOfVisitors(String numOfVisitors) {
        this.numOfVisitors = numOfVisitors;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}