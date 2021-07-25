package com.kirkwoodwest.utils.led;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.MidiOut;

import java.util.function.Supplier;

public class LedNoteOnOff implements Led {
  private final Supplier<Boolean> results_supplier;
  MidiOut midi_out;
  int     status = ShortMidiMessage.NOTE_ON;
  int note;
  boolean internal_value;

  public LedNoteOnOff(MidiOut midi_out, int note, Supplier<Boolean> results_func){
    this.midi_out = midi_out;
    this.note = note;
    this.results_supplier = results_func;
  }

  @Override
  public void update(boolean force_update) {
    boolean value = results_supplier.get();
    if (value != internal_value || force_update == true) {
      int data2 = 0;
      if (value == true)  data2 = 127;
      midi_out.sendMidi(status, note, data2);
      internal_value = value;
    }
  }
}
