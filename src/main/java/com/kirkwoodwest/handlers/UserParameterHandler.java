package com.kirkwoodwest.handlers;

import com.bitwig.extension.api.opensoundcontrol.OscConnection;
import com.bitwig.extension.api.opensoundcontrol.OscMessage;
import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.utils.Math;
import com.kirkwoodwest.utils.osc.OscHandler;

import java.util.ArrayList;
import java.util.List;

public class UserParameterHandler {
  private final OscHandler oscHandler;
  private final ControllerHost host;

  private final UserControlBank userControls;
  private final int userControlsCount;

  
  private final List<UserParameterData> userParameterDataList = new ArrayList<>();

  private boolean debug_mode_enabled;
  private String oscAddress;

  private boolean send_values_after_received = false;
  private boolean indexPaddingEnabled;
  private int indexPaddingCount;
  private boolean valuesOnlyMode = true;

  public UserParameterHandler(ControllerHost host, OscHandler oscHandler, int numUserControls, String oscPath, boolean indexPaddingEnabled, boolean valuesOnlyMode) {
    this.oscHandler = oscHandler;
    this.host = host;
    this.userControlsCount = numUserControls;
    this.oscAddress = oscPath;
    this.indexPaddingEnabled = indexPaddingEnabled;
    this.valuesOnlyMode = valuesOnlyMode;

    if (indexPaddingEnabled) {
      this.indexPaddingCount = 4;
    }

    userControls = host.createUserControls(numUserControls);
    oscHandler.registerDefaultCallback(this::basicOscCallback);

    for (int i = 0; i < numUserControls; i++) {
      final int osc_index = i;
      Parameter parameter = userControls.getControl(i);
      String index_string = getIndexString(i);
      String path = oscPath + index_string;

      UserParameterData userParameterData = new UserParameterData(parameter, "", 0.0, "", path, valuesOnlyMode);
      userParameterDataList.add(userParameterData);

      if (oscPath.isEmpty()) continue;
      oscHandler.registerOscCallback(userParameterData.getPathValue(), "User Parameter " + index_string + " value", (OscConnection osc_connection, OscMessage osc_message) -> this.updateParameter(osc_connection, osc_message, osc_index));
    }
  }


  private void basicOscCallback(OscConnection oscConnection, OscMessage oscMessage) {
    if (debug_mode_enabled) {
      host.println("osc message:" + oscMessage.getAddressPattern() + " : " + oscMessage.getArguments());
    }
  }

  public void refresh() {
    //Loop thru controls and determine if anything changed, update via osc if so...

    userParameterDataList.forEach(userParameterData -> {
      if (userParameterData.isValueDirty()) {
        String path = userParameterData.getPathValue();
        oscHandler.addMessageToQueue(path, userParameterData.getValue().floatValue());
        userParameterData.clearValueDirty();
      }
      if(!valuesOnlyMode){
        if (userParameterData.isNameDirty()) {
          String path = userParameterData.getPathName();
          oscHandler.addMessageToQueue(path, userParameterData.getName());
          userParameterData.clearNameDirty();
        }

        if (userParameterData.isDisplayedValueDirty()) {
          String path = userParameterData.getPathDisplayedValue();
          oscHandler.addMessageToQueue(path, userParameterData.getDisplayedValue());
          userParameterData.clearDisplayedValueDirty();
        }
      }
    });
  }

  public String getIndexString(int index) {
    if (indexPaddingEnabled) return Math.padInt(this.indexPaddingCount, index);
    return String.valueOf(index);
  }

  public void debugModeEnable(boolean enable) {
    this.debug_mode_enabled = enable;
  }

  private void updateParameter(OscConnection oscConnection, OscMessage oscMessage, int osc_index) {
    double message_value = -1;
    try {
      message_value = (double) oscMessage.getFloat(0); // First argument of message.
    } catch (Exception e) {
      host.println("OSC IN: " + oscMessage.getAddressPattern() + " INCORRECT TYPE: for " + oscMessage.getAddressPattern() + ". Must be (float)");
    }

    if (Double.compare(message_value, 0) < 0 || Double.compare(message_value, 1) > 0) {
      host.println("OSC IN: " + oscMessage.getAddressPattern() + " INCORRECT RANGE: for " + oscMessage.getAddressPattern() + ". Range: 0-1");
      return;
    }

    Parameter parameter = userControls.getControl(osc_index);
    SettableRangedValue value = parameter.value();
    double target_value = value.getAsDouble();
    if (Double.compare(message_value, target_value) != 0) {
      value.set(message_value);
      UserParameterData userParameterData = userParameterDataList.get(osc_index);
      userParameterData.setValue(message_value);
      parameter.set(message_value);

      if (!send_values_after_received) {
        userParameterData.clearValueDirty();
      }
    }

    if (this.debug_mode_enabled) {
      host.println("OSC IN: " + oscMessage.getAddressPattern() + "  " + message_value);
    }
  }

  public void setSendValuesAfterReceived(boolean b) {
    send_values_after_received = b;
  }

  private void setValuesOnlyMode(boolean b) {
    valuesOnlyMode = b;
  }
}
