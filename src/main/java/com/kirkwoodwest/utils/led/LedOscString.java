package com.kirkwoodwest.utils.led;

import com.kirkwoodwest.utils.osc.OscHandler;

import java.util.function.Supplier;

public class LedOscString  implements Led {
  private final Supplier<String> results_supplier;
  String     internal_value;
  String  osc_target;

  LedHandler led_handler;
  private final OscHandler osc_handler;

  public LedOscString(OscHandler osc_handler, String osc_target, Supplier<String> results_func){
    this.osc_handler = osc_handler;
    this.osc_target = osc_target;
    this.results_supplier = results_func;
  }

  @Override
  public void update(boolean force_update) {
    String value = results_supplier.get();
    if (!value.equals(internal_value) || force_update == true) {
      osc_handler.addMessageToQueue(osc_target, value);
      internal_value = value;
    }
  }
}
