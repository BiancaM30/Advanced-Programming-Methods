package com.example.toysocialnetworkgui.domain;

public class UsersRequestDTO {
    private long id1;
    private long id2;
    private String FirstName2;
    private String LastName2;
    private String Location2;
    private String status;

    public UsersRequestDTO(long id1, long id2, String firstName2, String lastName2, String location2, String status) {
        this.id1 = id1;
        this.id2 = id2;
        FirstName2 = firstName2;
        LastName2 = lastName2;
        Location2 = location2;
        this.status = status;
    }

    public UsersRequestDTO(long id1, long id2, String firstName2, String lastName2, String location2) {
        this.id1 = id1;
        this.id2 = id2;
        FirstName2 = firstName2;
        LastName2 = lastName2;
        Location2 = location2;
    }

    public long getId1() {
        return id1;
    }

    public void setId1(long id1) {
        this.id1 = id1;
    }

    public long getId2() {
        return id2;
    }

    public void setId2(long id2) {
        this.id2 = id2;
    }

    public String getFirstName2() {
        return FirstName2;
    }

    public void setFirstName2(String firstName2) {
        FirstName2 = firstName2;
    }

    public String getLastName2() {
        return LastName2;
    }

    public void setLastName2(String lastName2) {
        LastName2 = lastName2;
    }

    public String getLocation2() {
        return Location2;
    }

    public void setLocation2(String location2) {
        Location2 = location2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
