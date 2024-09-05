package com.example.toysocialnetworkgui.domain;


import java.util.Objects;


public class Tuple<T1, T2> {
    private T1 t1;
    private T2 t2;

    public Tuple(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T1 getLeftMember() {
        return t1;
    }

    public T2 getRightMember() {
        return t2;
    }

    public void setLeftMember(T1 t1) {
        this.t1 = t1;
    }

    public void setRightMember(T2 t2) {
        this.t2 = t2;
    }

    @Override
    public String toString() {
        return "" + t1 + "," + t2;
    }

    @Override
    public boolean equals(Object obj) {

        if (this.t1 == ((Tuple) obj).t1 && this.t2 == ((Tuple) obj).t2) return true;
        if(this.t1 == ((Tuple) obj).t2 && this.t2 == ((Tuple) obj).t1) return true;
        return false;
        // return this.t1.equals(((Tuple) obj).t1) && this.t2.equals(((Tuple) obj).t2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t1, t2);
    }
}