package com.training.issuing.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Member {
    private String id;
    private String name;
    private LocalDate registeredDay;

    public enum Status {
        ACTIVE,
        SUSPENDED,
        WITHDRAWN;
    }

    private Status currentStatus;

    public Member(String id, String name, LocalDate registeredDay) {
        this.id = id;
        this.name = name;
        this.registeredDay = registeredDay;
        this.currentStatus = Status.ACTIVE;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public LocalDate getRegisteredDay() {
        return this.registeredDay;
    }

    public Status getCurrentStatus() {
        return this.currentStatus;
    }

    public boolean isCardIssuable() {
        return this.currentStatus == Status.ACTIVE;
    }

    public void suspend() {
        this.currentStatus = Status.SUSPENDED;
    }

    public void withdraw() {
        this.currentStatus = Status.WITHDRAWN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member)) {
            return false;
        }
        Member other = (Member) o;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
