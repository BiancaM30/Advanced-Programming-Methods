package com.example.toysocialnetworkgui.domain;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Tuple<Long, Long>> {
    private LocalDate date;

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Friendship(Long id1, Long id2) {
        this.date = LocalDate.now();
        this.setId(new Tuple(id1, id2));
    }

    public Friendship(Long id1, Long id2, LocalDate date) {
        this.date = date;
        this.setId(new Tuple(id1, id2));
    }
    /**
     * @return the date when the friendship was created
     */
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Friendship that = (Friendship) o;
        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), date);
    }


}
