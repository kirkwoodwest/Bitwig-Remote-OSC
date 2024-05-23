// Written by Kirkwood West - kirkwoodwest.com
// (c) 2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt


package com.kirkwoodwest.extensions.oscplayground;

import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.bitwig.extension.GenericControllerExtension;
import com.kirkwoodwest.handlers.UserParameterHandler;
import com.kirkwoodwest.utils.Log;
import com.kirkwoodwest.utils.Math;
import com.kirkwoodwest.utils.osc.OscHandler;

public class OscPlaygroundExtension extends GenericControllerExtension {
  //Class Variables
  private ControllerHost host;

  private OscHandler osc_handler;
  private UserParameterHandler user_parameter_handler;
  private SettableRangedValue setting_number_of_user_controls;
  private SettableBooleanValue setting_debug_osc_in;
  private SettableBooleanValue setting_debug_osc_out;
  private SettableEnumValue setting_resolution;
  private boolean debug_osc_in;
  private boolean debug_osc_out;

  private final String[] resolution_enum = new String[]{"128","1024","16384"};
  private int resolution;
  private SettableStringValue setting_target;
  private String osc_target;
  private SettableBooleanValue setting_send_values_on_received;
  private SettableBooleanValue setting_deadzone_enabled;
  private Signal setting_restart;
  private SettableBooleanValue setting_zero_pad;
  private boolean zero_pad;


  protected OscPlaygroundExtension(final OscPlaygroundExtensionDefinition definition, final ControllerHost host) {
    super(definition, host);
  }

  @Override
  public void init() {
    host = getHost();
    Log.init(host);

    String version = getExtensionDefinition().getVersion();
    host.println("\n-------------------------------------------");
    host.println("Remote OSC " + version + " Initializing...");

    osc_handler = new OscHandler(host, true);

    int USER_CONTROL_LIMIT = 1024;
    {
      String label = "Number Of User Controls";
      final String  	category ="User Controls";
      final double  	min_value = 1;
      final double  	max_value = USER_CONTROL_LIMIT;
      final double  	step_resolution = 1;
      final String  	unit = "";
      final double  	initial_value = 64;
      setting_number_of_user_controls = host.getPreferences().getNumberSetting(label, category, 1, USER_CONTROL_LIMIT, step_resolution, unit, initial_value);
    }

    {
      setting_target = host.getPreferences().getStringSetting("Osc Base target", "OSC Settings", 20, "/user/");
      osc_target = setting_target.get();
    }

    {
      setting_zero_pad = host.getPreferences().getBooleanSetting("Index Zero Padding (i.e. user/001, user/002)", "OSC Settings", false);
      zero_pad = setting_zero_pad.get();
    }

    double number_user_controls =  setting_number_of_user_controls.get();
    int user_controls_count = (int) Math.map(number_user_controls,0,1,1,USER_CONTROL_LIMIT);
    if(user_controls_count<1) user_controls_count = 1;
    user_parameter_handler = new UserParameterHandler(host, osc_handler, user_controls_count, osc_target, zero_pad, true);

    {
      setting_send_values_on_received = host.getPreferences().getBooleanSetting("Send Values After Received", "OSC Settings", false);
      setting_send_values_on_received.addValueObserver(this::settingSendValuesOnReceived);
    }

//    {
//      setting_deadzone_enabled = host.getPreferences().getBooleanSetting("Deadzone Enabled", "OSC Settings", false);
//      setting_deadzone_enabled.addValueObserver(this::settingDeadzoneEnabled);
//    }



    setting_restart = host.getPreferences().getSignalSetting("Changing OSC Settings Requires Restart...", "Restart","Restart");
    setting_restart.addSignalObserver(this::settingRestart);

    setting_debug_osc_in = host.getPreferences().getBooleanSetting("Debug Osc In", "Osc Debug", false);
    setting_debug_osc_in.addValueObserver(this::settingDebugOscIn);

    setting_debug_osc_out = host.getPreferences().getBooleanSetting("Debug Osc Out", "Osc Debug", false);
    setting_debug_osc_out.addValueObserver(this::settingDebugOscOut);




    user_parameter_handler.debugModeEnable(debug_osc_in);



    CursorTrack cursor_track = host.createCursorTrack("cursor Track", "Cursor Track", 1, 1, true);
    cursor_track.addVuMeterObserver(1023, -1,false, this::reportVu);


    //Always rescan on init.
    //If your reading this... I hope you say hello to a loved one today. <3

    host.println("Complete.\n---");
    host.showPopupNotification("Remote OSC " + version + " Initialized.");
  }

  private void reportVu(int i) {
    String target = "/vu_meter";
    osc_handler.addMessageToQueue(target, (int) i);
  }


  private void settingRestart() {
    host.restart();
  }


  private void settingSendValuesOnReceived(boolean b) {
    user_parameter_handler.setSendValuesAfterReceived(b);
  }
  
  private void settingDebugOscIn(boolean b) {
    this.debug_osc_in = b;
    user_parameter_handler.debugModeEnable(debug_osc_in);
    if (b==true){
      host.println("debug osc in enabled.");
    } else {
      host.println("debug osc in disabled.");
    }
  }

  private void settingDebugOscOut(boolean b) {
    this.debug_osc_out = b;
    osc_handler.debugModeEnable(debug_osc_out);
    if (b==true){
      host.println("debug osc out enabled.");
    } else {
      host.println("debug osc out disabled.");
    }
  }

  @Override
  public void exit() {
    // TODO: Perform any cleanup once the driver exits
    // For now just show a popup notification for verification that it is no longer running.
    getHost().showPopupNotification("Remote OSC Exited");
  }

  @Override
  public void flush() {
    user_parameter_handler.refresh();
    osc_handler.sendQueue();
  }

  @Override
  public void refreshControllerParams(String controllerID) {
  }

}
