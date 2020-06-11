package com.example.bigsmartclockapp.core.clock;

public class AutoChrono extends Chrono {

    private int secondsLimit;
    private int minutesLimit;
    private int hoursLimit;
    private int currentLoop;
    private int maxLoops;

    public int getSecondsLimit() {
        return secondsLimit;
    }

    public void setSecondsLimit(int secondsLimit) {
        this.secondsLimit = secondsLimit;
    }

    public int getMinutesLimit() {
        return minutesLimit;
    }

    public void setMinutesLimit(int minutesLimit) {
        this.minutesLimit = minutesLimit;
    }

    public void setHoursLimit(int hoursLimit) {
        this.hoursLimit = hoursLimit;
    }

    public int getHoursLimit() {
        return hoursLimit;
    }

    public void setCurrentLoop(int currentLoop) {
        this.currentLoop = currentLoop;
    }

    public int getCurrentLoop() {
        return currentLoop;
    }

    public void setMaxLoops(int maxLoops) {
        this.maxLoops = maxLoops;
    }

    public int getMaxLoops() {
        return maxLoops;
    }
}
