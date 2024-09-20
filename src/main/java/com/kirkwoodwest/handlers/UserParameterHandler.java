package com.kirkwoodwest.handlers;

import com.bitwig.extension.api.opensoundcontrol.OscConnection;
import com.bitwig.extension.api.opensoundcontrol.OscMessage;
import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.extensions.remoteosc.DataResolution;
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
  private final DataResolution dataResolution;



  public UserParameterHandler(ControllerHost host, OscHandler oscHandler, int numUserControls, String oscPath, boolean indexPaddingEnabled, boolean valuesOnlyMode, DataResolution dataResolution) {
    this.oscHandler = oscHandler;
    this.host = host;
    this.userControlsCount = numUserControls;
    this.oscAddress = oscPath;
    this.indexPaddingEnabled = indexPaddingEnabled;
    this.valuesOnlyMode = valuesOnlyMode;
    this.dataResolution = dataResolution;

    if (indexPaddingEnabled) {
      this.indexPaddingCount = 4;
    }

    userControls = host.createUserControls(numUserControls);
    oscHandler.registerDefaultCallback(this::basicOscCallback);

    for (int i = 0; i < numUserControls; i++) {
      final int osc_index = i;
      Parameter parameter = userControls.getControl(i);
      String index_string = getIndexString(i);
      String path = oscPath + "/" + index_string;

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
      //Value
      if (userParameterData.isValueDirty()) {
        updateValue(userParameterData);
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

  private void updateParameter(OscConnection oscConnection, OscMessage oscMessage, int oscIndex) {

    switch (this.dataResolution) {
      case FLOAT:
        double messageValue = 0.0;
        try {
          messageValue = (double) oscMessage.getFloat(0); // First argument of message.
        } catch (Exception e) {
          host.println("OSC IN: " + oscMessage.getAddressPattern() + " INCORRECT TYPE: for " + oscMessage.getAddressPattern() + ". Must be (float)");
        }

        if (Double.compare(messageValue, 0) < 0 || Double.compare(messageValue, 1) > 0) {
          host.println("OSC IN: " + oscMessage.getAddressPattern() + " INCORRECT RANGE: for " + oscMessage.getAddressPattern() + ". Range: 0-1");
          return;
        }

        updateParameterFloat(messageValue, oscIndex);


        if (this.debug_mode_enabled) {
          host.println("OSC IN: " + oscMessage.getAddressPattern() + "  " + messageValue);
        }
        break;
      default:
        int messageValueInt = oscMessage.getInt(0);
        updateParameterInt(messageValueInt, oscIndex);
        if (this.debug_mode_enabled) {
          host.println("OSC IN: " + oscMessage.getAddressPattern() + "  " + messageValueInt);
        }
        break;
    }

  }

  private void updateParameterInt(int messageValueInt, int oscIndex) {
    //Compare against current value
    UserParameterData userParameterData = userParameterDataList.get(oscIndex);
    if(messageValueInt != userParameterDataList.get(oscIndex).getValueInt()){
      //set up integer values... 127,1023, 16383

      //limit the range
      int valueLimited = minMaxInt(messageValueInt);
      //Convert to double
      double messageValue = toDouble(valueLimited);
      //set to parameter
      Parameter parameter = userControls.getControl(oscIndex);
      SettableRangedValue value = parameter.value();
      value.set(messageValue);

      userParameterData.setValueInt(valueLimited);
      if (!send_values_after_received) {
        userParameterData.clearValueDirty();
      }

    }

  }

  private void updateParameterFloat(double messageValue, int oscIndex) {
    Parameter parameter = userControls.getControl(oscIndex);
    SettableRangedValue value = parameter.value();
    double parameterValue = value.getAsDouble();
    if (Double.compare(messageValue, parameterValue) != 0) {
      value.set(messageValue);
      //set up integer values... 127,1023, 16383
      UserParameterData userParameterData = userParameterDataList.get(oscIndex);
      userParameterData.setValue(messageValue);
      parameter.set(messageValue);

      if (!send_values_after_received) {
        userParameterData.clearValueDirty();
      }
    }

  }

  public void updateValue(UserParameterData userParameterData) {
    String path = userParameterData.getPathValue();
    switch (dataResolution) {
      case FLOAT:
        oscHandler.addMessageToQueue(path, userParameterData.getValue().floatValue());
        break;
      default:
        int v = toInt(userParameterData.getValue());
        oscHandler.addMessageToQueue(path, v);
        break;
    }
    userParameterData.clearValueDirty();
  }

  private int toInt(Double value) {
    switch (dataResolution) {
      case INT127:
        return (int) java.lang.Math.round(value * 127);
      case INT1023:
        return (int) java.lang.Math.round(value * 1023);
      case INT16383:
        return (int) java.lang.Math.round(value * 16383);
      default:
        return 0;
    }
  }

  private int minMaxInt(int value){
    switch (dataResolution) {
      case INT127:
        return Math.valueLimit(value, 0, 127);
      case INT1023:
        return Math.valueLimit(value, 0, 1023);
      case INT16383:
        return Math.valueLimit(value, 0, 16383);
      default:
        return 0;
    }
  }


  private double toDouble(int value) {
    switch (dataResolution) {
      case INT127:
        if(value == 64) return 0.5;
        return value / 127.0;
      case INT1023:
        if(value == 512) return 0.5;
        return value / 1023.0;
      case INT16383:
        if(value == 8192) return 0.5;
        return value / 16383.0;
      default:
        return 0;
    }

  }

  public void setSendValuesAfterReceived(boolean b) {
    send_values_after_received = b;
  }

  private void setValuesOnlyMode(boolean b) {
    valuesOnlyMode = b;
  }
}
