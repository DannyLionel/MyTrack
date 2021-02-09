package com.allansantosh.mytrack;

public class Tracker {

    private String id;
    private String device_id;
    private String user_id;
    private String owner_type;
    private String serial_no;
    private String name;


    public Tracker(String id, String device_id,  String user_id, String owner_type, String serial_no, String name) {

        this.id = id;
        this.device_id = device_id;
        this.user_id = user_id;
        this.owner_type = owner_type;
        this.serial_no= serial_no;
        this.name = name;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOwner_type() {
        return owner_type;
    }

    public void setOwner_type(String owner_type) {
        this.owner_type = owner_type;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

