package com.ex2.shenkar.todolist;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Task implements Serializable {
    private int ID;
    private String task;
    private String priority;
    private double lat;
    private double lng;
    private String address;
    private int member_id;
    private String member_name;
    private Long date;
    private String status;
    private String floor;

    public Task() {
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public Task(int ID, String task, String priority, double lat, double lng, String address, int member_id, String member_name, Long date, String status, String floor) {
        this.ID = ID;
        this.task = task;
        this.priority = priority;
        this.lat = lat;
        this.lat = lng;
        this.address = address;
        this.member_id = member_id;
        this.member_name = member_name;
        this.date = date;
        this.status = status;
        this.floor = floor;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String member) {
        this.status = status;
    }
}
