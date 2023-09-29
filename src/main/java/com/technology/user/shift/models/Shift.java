package com.technology.user.shift.models;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.Collection;

@Entity
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "shift_time")
    private LocalTime shiftTime;

    @OneToMany(mappedBy = "shift")
    private Collection<UserShift> userShifts;

    public Shift(Integer id, LocalTime shiftTime, Collection<UserShift> userShifts) {
        this.id = id;
        this.shiftTime = shiftTime;
        this.userShifts = userShifts;
    }

    public Shift() {
    }

    public static ShiftBuilder builder() {
        return new ShiftBuilder();
    }

    public Integer getId() {
        return this.id;
    }

    public LocalTime getShiftTime() {
        return this.shiftTime;
    }

    public Collection<UserShift> getUserShifts() {
        return this.userShifts;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setShiftTime(LocalTime shiftTime) {
        this.shiftTime = shiftTime;
    }

    public void setUserShifts(Collection<UserShift> userShifts) {
        this.userShifts = userShifts;
    }

    public static class ShiftBuilder {
        private Integer id;
        private LocalTime shiftTime;
        private Collection<UserShift> userShifts;

        ShiftBuilder() {
        }

        public ShiftBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public ShiftBuilder shiftTime(LocalTime shiftTime) {
            this.shiftTime = shiftTime;
            return this;
        }

        public ShiftBuilder userShifts(Collection<UserShift> userShifts) {
            this.userShifts = userShifts;
            return this;
        }

        public Shift build() {
            return new Shift(this.id, this.shiftTime, this.userShifts);
        }

        public String toString() {
            return "Shift.ShiftBuilder(id=" + this.id + ", shiftTime=" + this.shiftTime + ", userShifts=" + this.userShifts + ")";
        }
    }
}
