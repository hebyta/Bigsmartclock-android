package com.example.bigsmartclockapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> activeStatus;
    private MutableLiveData<String> clockValue;
    private MutableLiveData<String> currentLoop;
    private MutableLiveData<String> maxLoops;

    public HomeViewModel() {
        activeStatus = new MutableLiveData<>();
        clockValue = new MutableLiveData<>();
        currentLoop = new MutableLiveData<>();
        maxLoops = new MutableLiveData<>();
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus.setValue(activeStatus);
    }

    public void setClockValue(String clockValue) {
        this.clockValue.setValue(clockValue);
    }

    public void setCurrentLoop(String currentLoop) {
        this.currentLoop.setValue(currentLoop);
    }

    public void setMaxLoops(String maxLoops) {
        this.maxLoops.setValue(maxLoops);
    }

    public LiveData<String> getActiveStatus(){
        return activeStatus;
    }
    public LiveData<String> getClockValue(){
        return clockValue;
    }
    public LiveData<String> getCurrentLoop(){
        return currentLoop;
    }
    public LiveData<String> getMaxLoops(){
        return maxLoops;
    }

}