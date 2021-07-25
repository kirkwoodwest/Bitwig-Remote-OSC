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
    private final int[] user_controls_values;
    private final String[] user_controls_names;
    private boolean debug_mode_enabled;
    private int resolution;
    private String target;

    public UserParameterHandler (ControllerHost host, OscHandler osc_handler, int user_controls_count){
        this.osc_handler = osc_handler;
        this.host = host;
        this.user_controls_count = user_controls_count;
        this.target = "";

        user_controls = host.createUserControls(user_controls_count);
        user_controls_values = new int[user_controls_count];
        user_controls_names = new String[user_controls_count];
        for(int i=0;i<user_controls_count;i++){
            Parameter control = user_controls.getControl(i);
            control.value().markInterested();
            control.name().markInterested();
            final int osc_index = i;
            osc_handler.registerOscCallback("/user/" + i +"/value","osc index: " + i, (OscConnection osc_connection, OscMessage osc_message)->this.updateParameter(osc_connection, osc_message, osc_index));
        }
    }

    public void refresh() {
        //Loop thru controls and determine if anything changed, update via osc if so...
        for(int i=0;i<user_controls_count;i++){
            Parameter control = user_controls.getControl(i);
            double user_control_value = control.value().getAsDouble();
            String user_control_name = control.name().get();

            int user_control_value_int = (int) java.lang.Math.round(user_control_value * (resolution-1));

            //if(java.lang.Math.abs(user_controls_values[i]-user_control_value) <= 0.0000001) {
            if ( user_control_value_int != user_controls_values[i] ){
                String target = this.target + i;
                osc_handler.addMessageToQueue(target, (int) user_control_value_int);
                user_controls_values[i] = user_control_value_int;
            }

            /*

            if(!user_control_name.equals(user_controls_names[i])) {
                String target = "/user/" + i +"/name";
                osc_handler.addMessageToQueue("/user/" + i +"/name", user_control_name);
                user_controls_names[i] = user_control_name;
            }
            */
        }
    }

    public void debugModeEnable(boolean enable){
        this.debug_mode_enabled = enable;
    }

    private void updateParameter(OscConnection oscConnection, OscMessage oscMessage, int osc_index) {

        int message_value = oscMessage.getInt(0); // First argument of message.
        message_value = Math.valueLimit(osc_index,0,resolution-1); //limit the message
        double message_value_float  = message_value/(resolution-1); //translated to float.

        Parameter control = user_controls.getControl(osc_index);
        SettableRangedValue value = control.value();
        double target_value = value.getAsDouble();
        int target_value_int = (int) java.lang.Math.round(target_value * (resolution-1));
        if(target_value_int != message_value) {
            value.set(message_value);
            user_controls_values[osc_index] = message_value;
        }

        if (this.debug_mode_enabled) {
            host.println("OSC IN: " + oscMessage.getAddressPattern() + "  " + message_value );
        }
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
