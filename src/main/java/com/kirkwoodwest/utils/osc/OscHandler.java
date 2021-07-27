package com.kirkwoodwest.utils.osc;

import com.bitwig.extension.api.opensoundcontrol.OscAddressSpace;
import com.bitwig.extension.api.opensoundcontrol.OscConnection;
import com.bitwig.extension.api.opensoundcontrol.OscMethodCallback;
import com.bitwig.extension.api.opensoundcontrol.OscModule;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.kirkwoodwest.utils.Array;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class OscHandler {

  private final ControllerHost host;
  private SettableRangedValue setting_port_in;
  private SettableStringValue settting_address_out;
  private SettableRangedValue setting_port_out;

  int                  osc_in_port     = 8010;

  //TODO: Put these in the host preferences.
  String               osc_out_address  = "192.168.86.246";
  int                  osc_out_port     = 8000;
  OscConnection        osc_connection;

  OscAddressSpace address_space;

  ArrayList<String> osc_targets = new ArrayList<>();
  ArrayList<Object> osc_messages = new ArrayList<>();
  private boolean debug_osc_out;


  public OscHandler(ControllerHost host, boolean setup_preferences){
    this.host = host;


    if (setup_preferences == true) {
      setting_port_in = host.getPreferences().getNumberSetting("port in", "OSC Settings", 8000,9999, 1, "" , 8010);
      settting_address_out = host.getPreferences().getStringSetting("target address", "OSC Settings", 20, "192.168.0.1");
      setting_port_out = host.getPreferences().getNumberSetting("target port", "OSC Settings", 8000,9999, 1, "" , 8000);

      osc_in_port = (int) setting_port_in.getRaw();
      osc_out_port = (int) setting_port_out.getRaw();
      osc_out_address = settting_address_out.get();
    }

    {
      String in_address = ".";
      try {
        in_address = InetAddress.getLocalHost().toString();
      } catch (UnknownHostException e) {
      }

      host.println("-------");
      host.println("IN:  " + in_address + ":" + osc_in_port);
      host.println("OUT: " + osc_out_address+ ":" + osc_out_port);
      host.println("-------");
    }

    OscModule       osc_module    = host.getOscModule();
    address_space = osc_module.createAddressSpace();

    osc_connection = osc_module.connectToUdpServer(osc_out_address, osc_out_port, address_space);
    osc_module.createUdpServer(osc_in_port, address_space);
  }

  public void registerOscCallback(String target, String description, OscMethodCallback callback){
    address_space.registerMethod(target, "*", description, callback);
  }

  public void registerDefaultCallback(OscMethodCallback callback){
    address_space.registerDefaultMethod(callback);
  }

  public void addMessageToQueue(String target, Object message) {
    osc_targets.add(target);
    osc_messages.add(message);
  }

  public void sendMessage(String target, Object message) {
    //Send updates here.
    try {
      osc_connection.sendMessage(target, message);
    } catch (IOException e) {
      host.println("modulatedValueChanged IO Exception:" + e);
    }

  }

  public void sendQueue() {
    //Send updates here.
    try {
     // osc_connection.startBundle();
      int size = osc_targets.size();

      //Go through list
      for(int i = 0; i < size; i++) {
        String target = osc_targets.get(i);
        Object message  = osc_messages.get(i);
        osc_connection.sendMessage(target, message);
        if(this.debug_osc_out == true) host.println("OSC OUT: " + target + " " + message);

      }
      // osc_connection.endBundle();

      osc_targets.clear();
      osc_messages.clear();

    } catch (IOException e) {
      host.println("modulatedValueChanged IO Exception:" + e);
    }

    //

  }

  public void debugModeEnable(boolean b) {
    this.debug_osc_out = b;
  }
}
