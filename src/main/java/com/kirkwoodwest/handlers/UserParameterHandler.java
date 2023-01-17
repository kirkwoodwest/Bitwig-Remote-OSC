package com.kirkwoodwest.handlers;

import com.bitwig.extension.api.opensoundcontrol.OscConnection;
import com.bitwig.extension.api.opensoundcontrol.OscMessage;
import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.utils.Math;
import com.kirkwoodwest.utils.osc.OscHandler;

public class UserParameterHandler {
    private final OscHandler osc_handler;
    private final ControllerHost host;

    private final UserControlBank user_controls;
    private final int user_controls_count;
    private final String[] user_controls_targets;

    private double[] user_controls_values;
    private boolean[] user_controls_values_dirty;

    private boolean debug_mode_enabled;
    private String osc_address;

    private boolean send_values_after_received = false;
    private double deadzone_value;
    private boolean deadzone_enabled = false;
    private boolean index_padding_enabled;
    private int index_padding_count;

    public UserParameterHandler (ControllerHost host, OscHandler osc_handler, int user_controls_count, String osc_address, boolean index_padding_enabled){
        this.osc_handler = osc_handler;
        this.host = host;
        this.user_controls_count = user_controls_count;
        this.osc_address = osc_address;
        this.index_padding_enabled = index_padding_enabled;

        if(index_padding_enabled) {
            this.index_padding_count =  4;
        }

        user_controls = host.createUserControls(user_controls_count);
        user_controls_values = new double[user_controls_count];
        user_controls_targets = new String[user_controls_count];
        user_controls_values_dirty = new boolean[user_controls_count];


        osc_handler.registerDefaultCallback(this::basicOscCallback);
        for(int i=0;i<user_controls_count;i++){
            Parameter control = user_controls.getControl(i);
            user_controls_values_dirty[i] = true;
            control.value().markInterested();
            control.name().markInterested();
            final int osc_index = i;
            String index_string = getIndexString(i);
            user_controls_targets[i] = index_string;
            if(osc_address.isEmpty()) continue;
            osc_handler.registerOscCallback(osc_address + index_string ,"User Parameter "+ index_string + " value", (OscConnection osc_connection, OscMessage osc_message)->this.updateParameter(osc_connection, osc_message, osc_index));
        }
    }

    private void basicOscCallback(OscConnection oscConnection, OscMessage oscMessage) {
        if (debug_mode_enabled){
            host.println("osc message:" + oscMessage.getAddressPattern() + " : " + oscMessage.getArguments());
        }
    }

    public void refresh() {
        //Loop thru controls and determine if anything changed, update via osc if so...
        for(int i=0;i<user_controls_count;i++){
            Parameter control = user_controls.getControl(i);
            double user_control_value = control.value().getAsDouble();

            if ( Double.compare(user_control_value, user_controls_values[i]) != 0  || user_controls_values_dirty[i] == true){
                String target = this.osc_address + user_controls_targets[i];
                osc_handler.addMessageToQueue(target, (float) user_control_value);
                user_controls_values[i] = user_control_value;
                user_controls_values_dirty[i] = false;
            }
        }
    }

    public String getIndexString(int index){
        if(index_padding_enabled) return Math.padInt(this.index_padding_count , index);
        return String.valueOf(index);
    }

    public void debugModeEnable(boolean enable){
        this.debug_mode_enabled = enable;
    }

    private void updateParameter(OscConnection oscConnection, OscMessage oscMessage, int osc_index) {
        double message_value = -1;
        try {
            message_value = (double) oscMessage.getFloat(0); // First argument of message.
        } catch (Exception e)  {
            host.println("OSC IN: " + oscMessage.getAddressPattern() + " INCORRECT TYPE: for " + oscMessage.getAddressPattern()  + ". Must be (float)" );
        }

        if (Double.compare(message_value, 0) < 0 || Double.compare(message_value, 1) > 0)  {
            host.println("OSC IN: " + oscMessage.getAddressPattern() + " INCORRECT RANGE: for " + oscMessage.getAddressPattern()  + ". Range: 0-1" );
            return;
        }

        Parameter control = user_controls.getControl(osc_index);
        SettableRangedValue value = control.value();
        double target_value = value.getAsDouble();
        if(Double.compare(message_value, target_value) != 0) {
            value.set(message_value);
            user_controls_values[osc_index] = message_value;

            if(send_values_after_received) {
                user_controls_values_dirty[osc_index] = true;
            }
        }

        if (this.debug_mode_enabled) {
            host.println("OSC IN: " + oscMessage.getAddressPattern() + "  " + message_value );
        }
    }

    public void setOsc_address(String osc_address) {
        this.osc_address = osc_address;
        host.println("User Parameter Handler OSC TARGET: " + osc_address);
    }

    public void setSendValuesAfterReceived(boolean b){
        send_values_after_received = b;
    }

    public void setDeadzoneEnabled(boolean b) {
        deadzone_enabled = b;
    }
}
