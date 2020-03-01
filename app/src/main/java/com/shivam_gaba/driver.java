package com.shivam_gaba;

public class driver {

    public driver() {
    }

    private String driverName;
    private String truckNumber;
    private String emailId;
    private String driverPhoneNumber;
    private String driverPicUrl;

    public String getTruckTrashLevel() {
        return truckTrashLevel;
    }

    public void setTruckTrashLevel(String truckTrashLevel) {
        this.truckTrashLevel = truckTrashLevel;
    }

    private String truckTrashLevel;

    public driver(String driverName, String truckNumber, String emailId, String driverPhoneNumber, String driverPicUrl, String truckTrashLevel) {
        this.driverName = driverName;
        this.truckNumber = truckNumber;
        this.emailId = emailId;
        this.driverPhoneNumber = driverPhoneNumber;
        this.driverPicUrl = driverPicUrl;
        this.truckTrashLevel = truckTrashLevel;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    public String getDriverPicUrl() {
        return driverPicUrl;
    }

    public void setDriverPicUrl(String driverPicUrl) {
        this.driverPicUrl = driverPicUrl;
    }
}
