package com.example.flightGearAndroidApp;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class Model {
    private String ip, port;
    float rudder;
    float throttle;
    float aileron;
    float elevator;

    //to indicate if the connection succeed
    boolean isConnected = false;

    //to indicate in case of change
    boolean isRudderChanged = false;
    boolean isThrottleChanged = false;
    boolean isAileronChanged = false;
    boolean isElevatorChanged = false;

    //FG messages
    private static final String THROTTLE_MESSAGE = "set /controls/engines/current-engine/throttle ";
    private static final String RUDDER_MESSAGE = "set /controls/flight/rudder ";
    private static final String AILERON_MESSAGE = "set /controls/flight/aileron ";
    private static final String ELEVATOR_MESSAGE = "set /controls/flight/elevator ";


    //constructor
    public Model(String ip, String port){
        this.ip = ip;
        this.port = port;
    }

    //getters and setters for the fields
    @Nullable
    public String getIp() {
        return this.ip;
    }

    public void setIp(@Nullable String ip) {
        this.ip = ip;
    }

    @Nullable
    public String getPort() {
        return this.port;
    }

    public void setPort(@Nullable String port) {
        this.port = port;
    }


    public void setRudder(@Nullable float rudder) {
        this.rudder = rudder;
    }

    public void setThrottle(@Nullable float throttle) {
        this.throttle = throttle;
    }

    public void setAileron(@Nullable float aileron) {
        this.aileron = aileron;
    }

    public void setElevator(@Nullable float elevator) {
        this.elevator = elevator;
    }

    /** try to create connection in diffrent thread.
     *  create socket base on ip and port given by the user.
     *  in case that the connection goes well a succeed message prompt.
     *  if the connection failed, error message prompt
     */
    public void connect() {
        //create and run thread
        Thread t = new Thread() {
            public void run() {
                try {
                    Socket fg;
                    PrintWriter out;
                    int portAsInt = tryParse(port);
                    fg = new Socket(ip, portAsInt);
                    out = new PrintWriter(fg.getOutputStream(), true);
                    //connection succeed, set is connected to true
                    //in order to prompt the succeed message
                    isConnected = true;
                    //communication loop, send messages to FG.
                    //send messages only if they got new data.
                    while(true) {
                        if (isThrottleChanged) {
                            isThrottleChanged = false;
                            out.println(THROTTLE_MESSAGE + throttle + "\r\n");
                            out.flush();
                        }
                        if (isRudderChanged) {
                            isRudderChanged = false;
                            out.println(RUDDER_MESSAGE + rudder + "\r\n");
                            out.flush();

                        }
                        if (isAileronChanged) {
                            isAileronChanged = false;
                            out.println(AILERON_MESSAGE + aileron + "\r\n");
                            out.flush();

                        }
                        if (isElevatorChanged) {
                            isAileronChanged = false;
                            out.println(ELEVATOR_MESSAGE + elevator + "\r\n");
                            out.flush();
                        }
                    }
                } catch(SocketTimeoutException e) {
                    isConnected = false;
                    e.printStackTrace();
                } catch (IOException e) {
                    isConnected = false;
                    e.printStackTrace();
                } catch (Exception e) {
                    isConnected = false;
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    //convert string to int.
    //in case of exception (empty string, etc...) return -1.
    public static int tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    //calculate rudder
    public double calculateRudderBySeekBar(int val) {
        //the seek bar goes from 0-100 in steps of 1 unit.
        //we want the values between -1 to 1 so there are
        //100 steps of 0.02 between -1 and 1
        return val * 0.02 - 1;
    }

    //calculate throttle
    public double calculateThrottleBySeekBar(int val) {
        //the seek bar goes from 0-100 in steps of 1 unit.
        //we want the values between 0 to 1 so there are
        //100 steps of 0.01 between 0 and 1
        return val * 0.01;
    }

}
