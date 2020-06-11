package com.example.bigsmartclockapp.core.clock;

public class BaseClock {

    private boolean isActive;
    private boolean isRunning;
    private boolean is24HoursClock;
    private int seconds;
    private int minutes;
    private int hours;

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public boolean isActive() {
        return isActive;
    }


    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isIs24HoursClock() {
        return is24HoursClock;
    }

    public void setIs24HoursClock(boolean is24HoursClock) {
        this.is24HoursClock = is24HoursClock;
    }

    @Override
    public String toString() {
        return "BaseClock{" +
                "isActive=" + isActive +
                ", isRunning=" + isRunning +
                ", seconds=" + seconds +
                ", minutes=" + minutes +
                ", hours=" + hours +
                '}';
    }

}
