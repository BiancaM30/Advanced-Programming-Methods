package com.example.toysocialnetworkgui.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class User extends Entity<Long> {
    private static final AtomicLong count = new AtomicLong(0);
    private String FirstName;
    private String LastName;
    private String Gender;
    private LocalDate Birthday;
    private String Location;
    private String Email;
    private String Password;

    private List<User> friendsList;

    public User(String firstName, String lastName, String gender, LocalDate birthday, String location, String email, String password) {
        this.setId(count.incrementAndGet());
        FirstName = firstName;
        LastName = lastName;
        Gender = gender;
        Birthday = birthday;
        Location = location;
        Email = email;
        Password = password;
    }

    public User(long id, String firstName, String lastName, String gender, LocalDate birthday, String location, String email, String password) {
        this.setId(id);
        FirstName = firstName;
        LastName = lastName;
        Gender = gender;
        Birthday = birthday;
        Location = location;
        Email = email;
        Password = password;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getGender() {
        return Gender;
    }

    public LocalDate getBirthday() {
        return Birthday;
    }

    public String getLocation() {
        return Location;
    }

    public String getEmail() {
        return Email;
    }

    public List<User> getFriends() { return friendsList; }

    public void addFriend(User fr) { this.friendsList.add(fr); }

    public void removeFriend(User fr){ this.friendsList.remove(fr); }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public void setBirthday(LocalDate birthday) {
        Birthday = birthday;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setEmail(String email) {
        Email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", Gender='" + Gender + '\'' +
                ", Birthday=" + Birthday +
                ", Location='" + Location + '\'' +
                ", Email='" + Email + '\'' +
                '}';
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
