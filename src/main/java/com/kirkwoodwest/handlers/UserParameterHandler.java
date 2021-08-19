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

    private int[] user_controls_values;
    private boolean[] user_controls_values_dirty;
    private String[] user_controls_names;

    private boolean debug_mode_enabled;
    private int resolution;
    private String osc_address;

    private boolean send_values_after_received = false;
    private double deadzone_value;
    private boolean deadzone_enabled = false;
    private boolean index_padding_enabled;
    private int index_padding_count;

    public UserParameterHandler (ControllerHost host, OscHandler osc_handler, int user_controls_count, String osc_address, boolean index_padding_enabled, int resolution){
        this.osc_handler = osc_handler;
        this.host = host;
        this.user_controls_count = user_controls_count;
        this.osc_address = osc_address;
        this.resolution = resolution;
        this.index_padding_enabled = index_padding_enabled;

        if(index_padding_enabled) {
            String padding_string = String.valueOf(user_controls_count);
            this.index_padding_count =  padding_string.length();
        }

        user_controls = host.createUserControls(user_controls_count);
        user_controls_values = new int[user_controls_count];
        user_controls_targets = new String[user_controls_count];
        user_controls_names = new String[user_controls_count];
        user_controls_values_dirty = new boolean[user_controls_count];


        osc_handler.registerDefaultCallback(this::basicOscCallbak);
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

    private void basicOscCallbak(OscConnection oscConnection, OscMessage oscMessage) {
        if (debug_mode_enabled){
            host.println("osc message:" + oscMessage.getAddressPattern() + " : " + oscMessage.getArguments());
        }
    }

    public void refresh() {
        //Loop thru controls and determine if anything changed, update via osc if so...
        for(int i=0;i<user_controls_count;i++){
            Parameter control = user_controls.getControl(i);
            double user_control_value = control.value().getAsDouble();
            String user_control_name = control.name().get();

            double rescaled_message_value = Math.map(user_control_value, 0, 1 , 0, resolution-1);
            rescaled_message_value = rescaled_message_value -1;
            int user_control_value_int = (int) java.lang.Math.round(rescaled_message_value);

            if ( user_control_value_int != user_controls_values[i] || user_controls_values_dirty[i] == true){
                String target = this.osc_address + user_controls_targets[i];
                osc_handler.addMessageToQueue(target, (int) user_control_value_int);
                user_controls_values[i] = user_control_value_int;
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
        int message_value = -1;
        try {
            message_value = (int) java.lang.Math.round(oscMessage.getFloat(0)); // First argument of message.
        } catch (Exception e)  {

        }

        if (message_value == -1) {
            try {
                message_value = (int) java.lang.Math.round(oscMessage.getInt(0)); // First argument of message.
            } catch (Exception e)  {

            }
        }

        if (message_value == -1) {
            host.println("OSC IN: " + oscMessage.getAddressPattern() + " INCORRECT TYPE, must be float or int. Range: 0-" + (resolution-1));
            return;
        }

        message_value = Math.valueLimit(message_value,0,resolution-1); //limit the message

        double rescaled_message_value = Math.map(message_value, 0.0, resolution - 1.0, 0, 1);
        double message_value_double = rescaled_message_value;

        Parameter control = user_controls.getControl(osc_index);
        SettableRangedValue value = control.value();
        double target_value = value.getAsDouble();
        int target_value_int = (int) java.lang.Math.round(target_value * (resolution));
        if(target_value_int != message_value) {
            value.set(message_value_double);
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
