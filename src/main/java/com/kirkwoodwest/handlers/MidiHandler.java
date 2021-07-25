package com.kirkwoodwest.handlers;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.RemoteControlsPage;

import java.util.HashMap;

public interface MidiHandler {
  boolean handleMidi(ShortMidiMessage midi_message);
  void updateLeds();
  void enable(boolean enabled);
  void bindController();
}
