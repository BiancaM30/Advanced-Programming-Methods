package com.example.toysocialnetworkgui.domain;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Message extends Entity<Long> {
    private static final AtomicLong count = new AtomicLong(0);
    private Long from;
    private List<Long> to;
    private String message;
    private LocalDateTime data;
    private Long idReply;


    public Message(Long from, List<Long> to, String message) {
        this.setId(count.incrementAndGet());
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = LocalDateTime.now();
        this.idReply = null;
    }

    public Message(Long from, List<Long> to, String message, LocalDateTime data) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.idReply = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Message message1 = (Message) o;
        return from.equals(message1.from) && to.equals(message1.to) && message.equals(message1.message) && data.equals(message1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, to, message, data);
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public List<Long> getTo() {
        return to;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Long getIdReply() {
        return idReply;
    }

    public void setIdReply(Long idReply) {
        this.idReply = idReply;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", idReply=" + idReply +
                '}';
    }
}