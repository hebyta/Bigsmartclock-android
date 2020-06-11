package com.example.bigsmartclockapp.core.clock;

import java.util.TreeMap;

public class ClockOutput {

    public String activeMode;
    public int redColor;
    public int greenColor;
    public int blueColor;
    public int brightness;
    public TreeMap<String, BaseClock> clocks = new TreeMap<String, BaseClock>();

    @Override
    public String toString() {
        return "ClockOutput{" +
                "activeMode=" + activeMode +
                ", redColor=" + redColor +
                ", greenColor=" + greenColor +
                ", blueColor=" + blueColor +
                ", brightness=" + brightness +
                ", clocks=" + clocks.toString() +
                '}';
    }

    public static String formatTime(int hours, int minutes, int seconds){
        String timeFormated = "";
        if(hours < 10)
            timeFormated += "0" + hours;
        else
            timeFormated += hours;
        timeFormated += ":";
        if(minutes < 10)
            timeFormated += "0" + minutes;
        else
            timeFormated += minutes;
        timeFormated += ":";
        if(seconds < 10)
            timeFormated += "0" + seconds;
        else
            timeFormated += seconds;
        return timeFormated;
    }
}
