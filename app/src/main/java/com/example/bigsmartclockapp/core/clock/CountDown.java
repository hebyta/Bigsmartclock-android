package com.example.bigsmartclockapp.core.clock;

public class CountDown extends BaseClock {

    private int initialSeconds;
    private int initialMinutes;
    private int initialHours;

    public int getInitialSeconds() {
        return initialSeconds;
    }

    public void setInitialSeconds(int initialSeconds) {
        this.initialSeconds = initialSeconds;
    }

    public int getInitialMinutes() {
        return initialMinutes;
    }

    public void setInitialMinutes(int initialMinutes) {
        this.initialMinutes = initialMinutes;
    }

    public int getInitialHours() {
        return initialHours;
    }

    public void setInitialHours(int initialHours) {
        this.initialHours = initialHours;
    }
}
