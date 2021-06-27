package com.example.flightGearAndroidApp;

import android.widget.SeekBar;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ViewModel extends BaseObservable {
    //instance of model
    private Model model;

    //error and success messages
    private static final String SUCCESS_MESSAGE = "connection succeed!";
    private static final String ERROR_MESSAGE = "wrong ip or port number";

    @Bindable
    // toast message
    private String toastMessage = null;

    //constructor
    public ViewModel() {

        // instantiating object of
        // model class
        model = new Model("","");
    }

    // getter and setter methods
    // for toast message
    public String getToastMessage() {
        return toastMessage;
    }

    private void setToastMessage(String toastMessage) {
        this.toastMessage = toastMessage;
        notifyPropertyChanged(BR.toastMessage);
    }

    //getters and setters for the fields
    @Bindable
    public String getIp() {
        return model.getIp();
    }

    public void setIp(String ip) {
        model.setIp(ip);
        notifyPropertyChanged(BR.ip);
    }

    @Bindable
    public String getPort() {
        return model.getPort();
    }

    public void setPort(String port) {
        model.setPort(port);
    }

    public void setRudder(float rudder) {
        model.setRudder(rudder);
        model.isRudderChanged = true;
    }

    public void setAileron(float aileron) {
        model.setAileron(aileron);
        model.isAileronChanged = true;
    }

    public void setElevator(float elevator) {
        model.setElevator(elevator);
        model.isElevatorChanged = true;
    }

    public void setThrottle(float throttle) {
        model.setThrottle(throttle);
        model.isThrottleChanged = true;
    }

    //try to connect when user press connect button
    public void onButtonClicked() {
        model.connect();
        if (model.isConnected) {
            setToastMessage(SUCCESS_MESSAGE);
        } else {
            setToastMessage(ERROR_MESSAGE);
        }

    }

    public void onRudderChanged(SeekBar seekBar, int progresValue, boolean fromUser) {

        double calculateVal = model.calculateRudderBySeekBar(progresValue);
        setRudder((float) calculateVal);
    }

    public void onThrottleChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
        double calculateVal = model.calculateThrottleBySeekBar(progresValue);
        setThrottle((float) calculateVal);
    }
}
