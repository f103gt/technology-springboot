package com.technology.user.shift.models;

import com.technology.user.registration.models.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Entity
@Table(name = "client_shift")
public class UserShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User user;

    @ManyToOne
    private Shift shift;

    @Column(name = "shift_date")
    private LocalDate shiftDate;

    public UserShift(BigInteger id, User user, Shift shift, LocalDate shiftDate) {
        this.id = id;
        this.user = user;
        this.shift = shift;
        this.shiftDate = shiftDate;
    }

    public UserShift() {
    }

    public static UserShiftBuilder builder() {
        return new UserShiftBuilder();
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public static class UserShiftBuilder {
        private BigInteger id;
        private User user;
        private Shift shift;
        private LocalDate shiftDate;

        UserShiftBuilder() {
        }

        public UserShiftBuilder id(BigInteger id) {
            this.id = id;
            return this;
        }

        public UserShiftBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserShiftBuilder shift(Shift shift) {
            this.shift = shift;
            return this;
        }

        public UserShiftBuilder shiftDate(LocalDate shiftDate) {
            this.shiftDate = shiftDate;
            return this;
        }

        public UserShift build() {
            return new UserShift(this.id, this.user, this.shift, this.shiftDate);
        }

        public String toString() {
            return "UserShift.UserShiftBuilder(id=" + this.id + ", user=" + this.user + ", shift=" + this.shift + ", shiftDate=" + this.shiftDate + ")";
        }
    }
}
