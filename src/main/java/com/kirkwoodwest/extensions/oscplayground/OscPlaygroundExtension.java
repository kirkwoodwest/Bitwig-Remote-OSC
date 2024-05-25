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

}
