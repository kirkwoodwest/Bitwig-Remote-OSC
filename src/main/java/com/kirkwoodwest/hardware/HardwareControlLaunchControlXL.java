package com.kirkwoodwest.hardware;

public class HardwareControlLaunchControlXL {

    public enum CONTROL_GROUPS {
        KNOBS_ROW_1,
        KNOBS_ROW_2,
        KNOBS_ROW_3,
        FADERS,
        BUTTONS_ROW_1,
        BUTTONS_ROW_2,
    }

    CONTROL_GROUPS control_group;
    int knob_index;
    public HardwareControlLaunchControlXL(CONTROL_GROUPS control_group, int knob_index){
        this.control_group = control_group;
        this.knob_index = knob_index;
    }
}
