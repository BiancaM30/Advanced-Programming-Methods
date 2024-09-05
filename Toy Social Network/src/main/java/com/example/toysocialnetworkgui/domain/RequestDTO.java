package com.example.toysocialnetworkgui.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RequestDTO {
    private long idFrom;
    private String FirstName;
    private String LastName;
    private String Location;
    private LocalDate SendingDate;

    public RequestDTO(long idFrom, String firstName, String lastName, String location, LocalDate sendingDate) {
        FirstName = firstName;
        LastName = lastName;
        Location = location;
        SendingDate = sendingDate;
        this.idFrom = idFrom;
    }

    public long getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(long idFrom) {
        this.idFrom = idFrom;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public LocalDate getSendingDate() {
        return SendingDate;
    }

    public void setSendingDate(LocalDate sendingDate) {
        SendingDate = sendingDate;
    }

    @Override
    public String toString() {
        return "RequestDTO{" +
                "FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", Location='" + Location + '\'' +
                ", SendingDate=" + SendingDate +
                '}';
    }
}
