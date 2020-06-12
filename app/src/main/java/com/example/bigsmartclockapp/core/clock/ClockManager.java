package com.example.bigsmartclockapp.core.clock;

import android.util.Log;

import java.util.TreeMap;

public class ClockManager {

    public static final int CLOCK_MODE = 0;
    public static final int CHRONO_MODE = 1;
    public static final int COUNTDOWN_MODE = 2;
    public static final int AUTO_CHRONO_MODE = 3;
    public static final int AUTO_COUNTDOWN_MODE = 4;

    public static final String MODE_CODE = "M";
    public static final String RED_COLOR_CODE = "CR";
    public static final String GREEN_COLOR_CODE = "CG";
    public static final String BLUE_COLOR_CODE = "CB";
    public static final String BRIGHTNESS_CODE = "B";
    public static final String CLOCK_CODE = "T";
    public static final String CHRONO_CODE = "C";
    public static final String COUNTDOWN_CODE = "CD";
    public static final String AUTO_CHRONO_CODE = "AC";
    public static final String AUTO_COUNTDOWN_CODE = "ACD";

    public static ClockOutput processOutput(String input){
        ClockOutput output = new ClockOutput();
        if(input != null && input.length() > 0 && input.startsWith("&") && input.contains("|")){
            input = input.substring(1, input.indexOf("|"));
            Log.d("Input", input);
            String[] splits = input.split(";");
            for(String splitData : splits){
                String[] keyValuePair = splitData.split("=");
                String key = keyValuePair[0];
                String value = keyValuePair[1];
                if(key.equals(MODE_CODE)){
                    if(Integer.parseInt(value) == CLOCK_MODE){
                        output.activeMode = CLOCK_CODE;
                    } else if(Integer.parseInt(value) == CHRONO_MODE){
                        output.activeMode = CHRONO_CODE;
                    } else if(Integer.parseInt(value) == COUNTDOWN_MODE){
                        output.activeMode = COUNTDOWN_CODE;
                    } else if(Integer.parseInt(value) == AUTO_CHRONO_MODE){
                        output.activeMode = AUTO_CHRONO_CODE;
                    } else if(Integer.parseInt(value) == AUTO_COUNTDOWN_MODE){
                        output.activeMode = AUTO_COUNTDOWN_CODE;
                    }
                } else if(key.equals(RED_COLOR_CODE)){
                    output.redColor = Integer.parseInt(value);
                } else if(key.equals(GREEN_COLOR_CODE)){
                    output.greenColor = Integer.parseInt(value);
                } else if(key.equals(BLUE_COLOR_CODE)){
                    output.blueColor = Integer.parseInt(value);
                } else if(key.equals(BRIGHTNESS_CODE)){
                    output.brightness = Integer.parseInt(value);
                } else {
                    output.clocks.put(key, processKeyValuePair(output.activeMode, key, value));
                }
            }
            return output;
        }
        return null;
    }

    private static BaseClock processKeyValuePair(String activeMode, String key, String value){
        String[] valueSplit = value.split(":");
        if(key.equals(CLOCK_CODE)){
            BaseClock clock = new BaseClock();
            clock.setHours(Integer.parseInt(valueSplit[0]));
            clock.setMinutes(Integer.parseInt(valueSplit[1]));
            clock.setSeconds(Integer.parseInt(valueSplit[2]));
            clock.setIs24HoursClock(valueSplit[3].equals("1"));
            clock.setActive(activeMode.equals(CLOCK_CODE));
            clock.setRunning(true);
            return clock;
        } else if(key.equals(CHRONO_CODE)){
            Chrono chrono = new Chrono();
            chrono.setHours(Integer.parseInt(valueSplit[0]));
            chrono.setMinutes(Integer.parseInt(valueSplit[1]));
            chrono.setSeconds(Integer.parseInt(valueSplit[2]));
            chrono.setActive(activeMode.equals(CHRONO_CODE));
            chrono.setRunning(valueSplit[3].equals("1"));
            return chrono;
        } else if(key.equals(COUNTDOWN_CODE)){
            CountDown countdown = new CountDown();
            countdown.setHours(Integer.parseInt(valueSplit[0]));
            countdown.setMinutes(Integer.parseInt(valueSplit[1]));
            countdown.setSeconds(Integer.parseInt(valueSplit[2]));
            countdown.setActive(activeMode.equals(COUNTDOWN_CODE));
            countdown.setInitialHours(Integer.parseInt(valueSplit[3]));
            countdown.setInitialMinutes(Integer.parseInt(valueSplit[4]));
            countdown.setInitialSeconds(Integer.parseInt(valueSplit[5]));
            countdown.setRunning(valueSplit[6].equals("1"));
            return countdown;
        } else if(key.equals(AUTO_CHRONO_CODE)){
            AutoChrono autoChrono = new AutoChrono();
            autoChrono.setHours(Integer.parseInt(valueSplit[0]));
            autoChrono.setMinutes(Integer.parseInt(valueSplit[1]));
            autoChrono.setSeconds(Integer.parseInt(valueSplit[2]));
            autoChrono.setActive(activeMode.equals(AUTO_CHRONO_CODE));
            autoChrono.setHoursLimit(Integer.parseInt(valueSplit[3]));
            autoChrono.setMinutesLimit(Integer.parseInt(valueSplit[4]));
            autoChrono.setSecondsLimit(Integer.parseInt(valueSplit[5]));
            autoChrono.setCurrentLoop(Integer.parseInt(valueSplit[6]));
            autoChrono.setMaxLoops(Integer.parseInt(valueSplit[7]));
            autoChrono.setRunning(valueSplit[8].equals("1"));
            return autoChrono;
        } else if(key.equals(AUTO_COUNTDOWN_CODE)){
            AutoCountDown autoCountDown = new AutoCountDown();
            autoCountDown.setHours(Integer.parseInt(valueSplit[0]));
            autoCountDown.setMinutes(Integer.parseInt(valueSplit[1]));
            autoCountDown.setSeconds(Integer.parseInt(valueSplit[2]));
            autoCountDown.setActive(activeMode.equals(AUTO_COUNTDOWN_CODE));
            autoCountDown.setInitialHours(Integer.parseInt(valueSplit[3]));
            autoCountDown.setInitialMinutes(Integer.parseInt(valueSplit[4]));
            autoCountDown.setInitialSeconds(Integer.parseInt(valueSplit[5]));
            autoCountDown.setCurrentLoop(Integer.parseInt(valueSplit[6]));
            autoCountDown.setMaxLoops(Integer.parseInt(valueSplit[7]));
            autoCountDown.setRunning(valueSplit[8].equals("1"));
            return autoCountDown;
        }
        return null;
    }

}
