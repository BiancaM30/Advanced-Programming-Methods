package com.example.toysocialnetworkgui.domain;


import java.time.LocalDate;
import java.util.Objects;

public class FriendshipRequest extends Entity<Tuple<Long, Long>> {
    Status status;
    private LocalDate sendingDate;

    public FriendshipRequest(Long id1, Long id2) {
        this.status = Status.PENDING;
        this.setId(new Tuple(id1, id2));
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(LocalDate sendingDate) {
        this.sendingDate = sendingDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FriendshipRequest that = (FriendshipRequest) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status);
    }

    @Override
    public String toString() {
        return "FriendshipRequest{" +
                "id=" + id +
                ", status=" + status +
                '}';
    }
}
