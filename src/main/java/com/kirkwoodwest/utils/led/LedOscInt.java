package com.kirkwoodwest.utils.led;

import com.bitwig.extension.controller.api.MidiOut;
import com.kirkwoodwest.utils.osc.OscHandler;

import java.util.function.Supplier;

public class LedOscInt implements Led {
  private final Supplier<Integer> results_supplier;
  int     internal_value;
  String  osc_target;

  LedHandler led_handler;
  private final OscHandler osc_handler;

  public LedOscInt(OscHandler osc_handler, String osc_target, Supplier<Integer> results_func){
    this.osc_handler = osc_handler;
    this.osc_target = osc_target;
    this.results_supplier = results_func;
  }

  @Override
  public void update(boolean force_update) {
    int value = results_supplier.get();
    if (value != internal_value || force_update == true) {
      if (!osc_target.isEmpty()) {
        osc_handler.addMessageToQueue(osc_target, value);
      }
    }
  }
}
