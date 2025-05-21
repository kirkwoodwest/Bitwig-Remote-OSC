// Written by Kirkwood West - kirkwoodwest.com
// (c) 2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt


package com.kirkwoodwest.extensions.remoteosc;

import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.bitwig.extension.GenericControllerExtension;
import com.kirkwoodwest.handlers.UserParameterHandler;
import com.kirkwoodwest.handlers.VUMeterBank;
import com.kirkwoodwest.utils.Log;
import com.kirkwoodwest.utils.Math;
import com.kirkwoodwest.utils.osc.OscHandler;

public class RemoteOscExtension extends GenericControllerExtension {
  //Class Variables
  private ControllerHost host;

  private OscHandler oscHandler;
  private UserParameterHandler userParameterHandler;
  private SettableRangedValue settingNumberOfUserControls;
  private SettableBooleanValue settingDebugOscIn;
  private SettableBooleanValue settingDebugOscOut;
  private SettableEnumValue dataResolutionSetting;
  private DataResolution dataResolution;


  private SettableStringValue settingOscPath;
  private String osc_target;
  private SettableBooleanValue settingSendValues;
  private Signal setting_restart;
  private SettableBooleanValue settingZeroPad;
  private VUMeterBank vuMeterBank;

  private SettableBooleanValue vuMeterEnabled;
  private SettableBooleanValue settingVuMeterPeak;
  private SettableBooleanValue settingVuMeterRms;
  private SettableBooleanValue settingValuesOnlyMode;


  protected RemoteOscExtension(final RemoteOscExtensionDefinition definition, final ControllerHost host) {
    super(definition, host);
  }

  @Override
  public void init() {
    host = getHost();
    Log.init(host);

    //Set up midi ports for mapping any midi data that you'd want in this same extension.
    if(getExtensionDefinition().getNumMidiInPorts() > 0) {
      MidiIn input_port = host.getMidiInPort(0);
      input_port.createNoteInput("RemoteOscInput", "??????");
    }

    String version = getExtensionDefinition().getVersion();
    host.println("\n-------------------------------------------");
    host.println("Remote OSC " + version + " Initializing...");

    oscHandler = new OscHandler(host, true);

    int USER_CONTROL_LIMIT = 1024;
    {
      String label = "Number Of User Controls";
      final String  	category ="User Controls";
      final double  	min_value = 1;
      final double  	max_value = USER_CONTROL_LIMIT;
      final double  	step_resolution = 1;
      final String  	unit = "";
      final double  	initial_value = 64;
      settingNumberOfUserControls = host.getPreferences().getNumberSetting(label, category, 1, USER_CONTROL_LIMIT, step_resolution, unit, initial_value);
    }

    {
      settingOscPath = host.getPreferences().getStringSetting("Osc Base Path", "OSC Settings", 20, "/user");
    }

    {
      settingZeroPad = host.getPreferences().getBooleanSetting("Index Zero Padding (i.e. /user/0001, /user/0002)", "OSC Settings", true);
      settingZeroPad.markInterested();
    }

    {
      settingValuesOnlyMode = host.getPreferences().getBooleanSetting("Values Only Mode", "OSC Settings", true);
      settingValuesOnlyMode.markInterested();
    }

    {
      settingSendValues = host.getPreferences().getBooleanSetting("Send Values After Received", "OSC Settings", false);
      settingSendValues.addValueObserver(this::settingSendValuesOnReceived);
    }

    {
      dataResolutionSetting = host.getPreferences().getEnumSetting("Data Resolution", "OSC Settings", DataResolutionEnum.getValues(), DataResolutionEnum.getValueText(0));
      dataResolution = DataResolutionEnum.getResolutionFor(dataResolutionSetting.get());
    }

    setting_restart = host.getPreferences().getSignalSetting("Changing OSC Settings Requires Restart...", "Restart","Restart");
    setting_restart.addSignalObserver(this::settingRestart);

    Signal settings_ping = host.getPreferences().getSignalSetting("Ping OSC Server", "Ping", "Ping");
    settings_ping.addSignalObserver(this::pingOscServer);

    settingDebugOscIn = host.getPreferences().getBooleanSetting("Debug Osc In", "Osc Debug", false);
    settingDebugOscIn.addValueObserver(this::settingDebugOscIn);

    settingDebugOscOut = host.getPreferences().getBooleanSetting("Debug Osc Out", "Osc Debug", false);
    settingDebugOscOut.addValueObserver(this::settingDebugOscOut);

    vuMeterEnabled = host.getPreferences().getBooleanSetting("VU Meter Enabled", "VU Meter", false);

    settingVuMeterPeak = host.getPreferences().getBooleanSetting("VU Meter Peak Enabled", "VU Meter", false);
    settingVuMeterPeak.addValueObserver(this::settingVuMeterPeakOutput);

    settingVuMeterRms = host.getPreferences().getBooleanSetting("VU Meter RMS Enabled", "VU Meter", false);
    settingVuMeterRms.addValueObserver(this::settingVuMeterRmsOutput);

    double number_user_controls =  settingNumberOfUserControls.get();
    int user_controls_count = (int) Math.map(number_user_controls,0,1,1,USER_CONTROL_LIMIT);
    if(user_controls_count<1) user_controls_count = 1;
    userParameterHandler = new UserParameterHandler(host, oscHandler, user_controls_count, settingOscPath.get(),
      settingZeroPad.get(), settingValuesOnlyMode.get(), dataResolution);

    userParameterHandler.debugModeEnable(settingDebugOscIn.get());

    vuMeterBank = new VUMeterBank(host, oscHandler, vuMeterEnabled.getAsBoolean(), settingVuMeterPeak.getAsBoolean(), settingVuMeterRms.getAsBoolean());

    //Always rescan on init.
    //If your reading this... I hope you say hello to a loved one today. <3

    host.println("Complete.\n---");

    if (oscHandler.init_success()) {
      host.showPopupNotification("Remote OSC " + version + " Initialized.");
    } else {
      host.showPopupNotification("Remote OSC " + version + " Failed Init. Check OSC Settings and Log");
    }
  }

  private void pingOscServer() {
    oscHandler.addMessageToQueue( "/ping", true);
  }

  private void settingVuMeterRmsOutput(boolean b) {
    vuMeterBank.setRmsOutput(b);
  }

  private void settingVuMeterPeakOutput(boolean b) {
    vuMeterBank.setPeakOutput(b);
  }


  private void settingRestart() {
    host.restart();
  }

  private void settingSendValuesOnReceived(boolean b) {
    userParameterHandler.setSendValuesAfterReceived(b);
  }
  
  private void settingDebugOscIn(boolean b) {
    userParameterHandler.debugModeEnable(b);
    if (b){
      host.println("debug osc in enabled.");
    } else {
      host.println("debug osc in disabled.");
    }
  }

  private void settingDebugOscOut(boolean b) {
    oscHandler.debugModeEnable(b);
    if (b){
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
    userParameterHandler.refresh();
    oscHandler.sendQueue();
  }

}
