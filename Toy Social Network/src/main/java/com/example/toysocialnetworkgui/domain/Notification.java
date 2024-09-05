package com.example.toysocialnetworkgui.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Notification extends Entity<Tuple<Long, Long>> {
    private LocalDate date;

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Notification(Long id_user, Long id_event) {
        this.date = LocalDate.now();
        this.setId(new Tuple(id_user, id_event));
    }

    public Notification(Long id_user, Long id_event, LocalDate date) {
        this.date = date;
        this.setId(new Tuple(id_user, id_event));
    }
    /**
     * @return the date when the user subscribed to event
     */
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Notification that = (Notification) o;
        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), date);
    }
}

