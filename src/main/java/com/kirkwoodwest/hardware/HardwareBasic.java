// Written by Kirkwood West - kirkwoodwest.com
// (c) 2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package com.kirkwoodwest.hardware;

import com.bitwig.extension.callback.ShortMidiDataReceivedCallback;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;

public class HardwareBasic {

  public MidiIn  inputPort  = null;
  public MidiOut outputPort = null;
  //private outputPort =null;

  public HardwareBasic(MidiIn input_port, MidiOut output_port, ShortMidiDataReceivedCallback input_callback) {
    this.inputPort 	= input_port;
    this.outputPort = output_port;
    if (input_callback != null) this.inputPort.setMidiCallback(input_callback);
  }

  public void sendMidi(int status, int data1, int data2) {
    this.outputPort.sendMidi(status, data1, data2);
  }

  public void sendSysex(String s) {
    this.outputPort.sendSysex(s);
  }
}
