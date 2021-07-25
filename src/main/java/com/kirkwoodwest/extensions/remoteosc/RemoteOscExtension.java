// Written by Kirkwood West - kirkwoodwest.com
// (c) 2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt


package com.kirkwoodwest.extensions.remoteosc;

import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.bitwig.extension.GenericControllerExtension;
import com.kirkwoodwest.handlers.UserParameterHandler;
import com.kirkwoodwest.utils.Log;
import com.kirkwoodwest.utils.Math;
import com.kirkwoodwest.utils.osc.OscHandler;

import java.util.Arrays;

public class RemoteOscExtension extends GenericControllerExtension {
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

  private final String[] resolution_enum = new String[]{"128","1024","1608"};
  private int resolution;


  protected RemoteOscExtension(final RemoteOscExtensionDefinition definition, final ControllerHost host) {
    super(definition, host);
  }

  @Override
  public void init() {
    host = getHost();
    Log.init(host);

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
      setting_resolution = host.getPreferences().getEnumSetting("Resolution", "User Controls", resolution_enum, resolution_enum[0]);
      setting_resolution.addValueObserver(this::settingResolution);
    }

    double number_user_controls =  setting_number_of_user_controls.get();
    int user_controls_count = (int) Math.map(number_user_controls,0,1,1,USER_CONTROL_LIMIT);
    if(user_controls_count<1) user_controls_count = 1;
    user_parameter_handler = new UserParameterHandler(host, osc_handler, user_controls_count);


    setting_debug_osc_in = host.getPreferences().getBooleanSetting("Debug Osc In", "Osc Debug", false);
    setting_debug_osc_in.addValueObserver(this::settingDebugOscIn);

    setting_debug_osc_out = host.getPreferences().getBooleanSetting("Debug Osc Out", "Osc Debug", false);
    setting_debug_osc_out.addValueObserver(this::settingDebugOscOut);

    user_parameter_handler.debugModeEnable(debug_osc_in);

    //TODO: update LED states on everything on init.

    //Always rescan on init.
    //If your reading this... I hope you say hello to a loved one today. <3
    host.println("Remote OSC Initialized. 0.5");
    host.showPopupNotification("Remote OSC Initialized. v0.5");
  }

  private void settingResolution(String s) {
    if (s.equals(resolution_enum[1])){
      resolution = 1024;
    } else if (s.equals(resolution_enum[2])){
      resolution = 16384;
    } else {
      resolution = 127;
    }
    user_parameter_handler.setResolution(resolution);
  }

  private void settingDebugOscIn(boolean b) {
    this.debug_osc_in = b;
    user_parameter_handler.debugModeEnable(debug_osc_in);
  }

  private void settingDebugOscOut(boolean b) {
    this.debug_osc_out = b;
    osc_handler.debugModeEnable(debug_osc_out);
  }

  @Override
  public void exit() {
    // TODO: Perform any cleanup once the driver exits
    // For now just show a popup notification for verification that it is no longer running.
    getHost().showPopupNotification("KIRKWOOD OSC Exited");
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
