package com.example.bigsmartclockapp.core.clock;

public class AutoCountDown extends CountDown {

    private int currentLoop;
    private int maxLoops;

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
