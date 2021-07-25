package com.kirkwoodwest.utils.led;
import com.kirkwoodwest.utils.osc.OscHandler;

import java.util.ArrayList;

public class LedHandler {
  ArrayList<Led> led_list;
  OscHandler osc_handler;


  public LedHandler(){
    led_list = new ArrayList<>();
  }
  public void add(Led led){
    led_list.add(led);
  }

  /**
   * Updates all Leds
   */
  public void updateLeds(boolean force_update){
    for(Led led : led_list){
      led.update(force_update);
    }
  }
}

