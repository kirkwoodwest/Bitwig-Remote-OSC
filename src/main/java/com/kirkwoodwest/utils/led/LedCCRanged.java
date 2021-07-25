package com.kirkwoodwest.utils.led;

import com.bitwig.extension.controller.api.MidiOut;
import com.kirkwoodwest.utils.osc.OscHandler;

import java.util.function.Supplier;

public class LedCCRanged implements Led {
  private final Supplier<Integer> results_supplier;
  MidiOut midi_out;
  int     status;
  int     data1;
  int internal_value;
  String osc_target = "";

  LedHandler led_handler;
  private OscHandler osc_handler;

  public LedCCRanged(MidiOut midi_out, int status, int data1, Supplier<Integer> results_func){
    this.midi_out = midi_out;
    this.status = status;
    this.data1 = data1;
    this.results_supplier = results_func;
  }

  @Override
  public void update(boolean force_update) {
    int value = results_supplier.get();
    if (value != internal_value || force_update == true) {

      midi_out.sendMidi(status, data1, value);
      internal_value = value;

      if (!osc_target.isEmpty()) {
        osc_handler.addMessageToQueue(osc_target, value);
      }
    }
  }

  public void addOSCTarget(OscHandler osc_handler, String osc_target) {
    this.osc_handler = osc_handler;
    this.osc_target = osc_target;
  }
}
