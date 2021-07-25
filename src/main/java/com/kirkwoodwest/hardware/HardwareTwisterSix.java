package com.kirkwoodwest.hardware;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiDataReceivedCallback;
import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.utils.led.LedCCRanged;
import java.util.ArrayList;
import java.util.function.Supplier;

import static com.kirkwoodwest.utils.Array.generateIncrementalArray;

public class HardwareTwisterSix extends HardwareBasic {
  HardwareSurface hardware_surface;
  public ArrayList<AbsoluteHardwareKnob> hardware_knobs;
  public static final int      TWISTER_COLOR_MIDI_CHANNEL   = 1;
  public static final int      TWISTER_CC_KNOB_COUNT         = 96;
  public static final int      TWISTER_CC_MIN               = 16;
  public static final int[]    TWISTER_CC_LIST = generateIncrementalArray(TWISTER_CC_MIN, TWISTER_CC_KNOB_COUNT);

  public HardwareTwisterSix(ControllerHost host, int midi_port, ShortMidiDataReceivedCallback input_callback) {
    super(host.getMidiInPort(midi_port), host.getMidiOutPort(midi_port), input_callback );
    hardware_surface = host.createHardwareSurface();
    hardware_knobs = new ArrayList<AbsoluteHardwareKnob>();
  /*
    int[] cc_list = TWISTER_CC_LIST;
    int num_knobs = cc_list.length;
    for (int i=0; i < num_knobs; i++ ) {
      AbsoluteHardwareKnob hardware_knob = hardware_surface.createAbsoluteHardwareKnob("TWISTER_CC_" + i);
      hardware_knob.setAdjustValueMatcher(inputPort.createAbsoluteCCValueMatcher(0, cc_list[i]));
      hardware_knob.value().markInterested();
      hardware_knobs.add(hardware_knob);
    }

    //4. Layout the simulator UI
    hardware_surface.setPhysicalSize(800,800);

    int knob_index = 0;
    int knob_size = 50;
    int knob_spacing = 10;
    int twister_spacing = 20;

    //First three Twisters
    int start_x = 10;
    int start_y = 200;
    int knob_grid_size = 4;
    int twister_size = knob_grid_size * knob_size + knob_grid_size * knob_spacing + twister_spacing;

    for (int twister_x=0;twister_x<3;twister_x++) {
      for (int block_x = 0; block_x < knob_grid_size; block_x++) {
        for (int block_y = 0; block_y < knob_grid_size; block_y++) {
          int x1 = knob_size * block_x + knob_spacing * block_x + start_x + twister_x * twister_size;
          int x2 = knob_size;
          int y1 = -knob_size * block_y - knob_spacing * block_y + start_y;
          int y2 = knob_size;
          hardware_surface.hardwareElementWithId("TWISTER_CC_" + knob_index).setBounds(x1, y1, x2, y2);
          hardware_knobs.get(knob_index).setLabel("FRESH");
          knob_index++;
        }
      }
    }

*/
  }
  public int getHardwareKnobCount() {
    return TWISTER_CC_KNOB_COUNT;
  }

  public AbsoluteHardwareKnob getHardwareKnob(int index) {
    return hardware_knobs.get(index);
  }

  public int[] getCurrentHardwareKnobValues() {
    int size = hardware_knobs.size();
    int[] values = new int[size];
    for(int i=0;i<size;i++) {
      double knob_value = hardware_knobs.get(i).value().get();
      values[i] = (int) Math.floor(knob_value * 127.0);
    }
    return values;
  }
  public boolean[] getKnobValueIndexChanges(int[] knob_values) {
    int size = hardware_knobs.size();
    boolean[] changed_value_indexes = new boolean[size];
    for(int i=0;i<size;i++) {
      double knob_value = hardware_knobs.get(i).value().get();
      int value = (int) Math.floor(knob_value * 127.0);
      if (value != knob_values[i]) {
        changed_value_indexes[i] = true;
      }
    }
    return changed_value_indexes;
  }

  /**
   * Returns an the color led matching the index.
   * @param index index of the knob
   * @param supplier method to determine the value for the knob led.
   * @return
   */
  public LedCCRanged getColorLed(int index, Supplier<Integer> supplier){
    int status = ShortMidiMessage.CONTROL_CHANGE | TWISTER_COLOR_MIDI_CHANNEL;
    int cc = TWISTER_CC_LIST[index];
    LedCCRanged led = new LedCCRanged(outputPort, status, cc, supplier);
    return led;
  }

  /**
   * Returns an the encoder led matching the index.
   * @param index index of the knob
   * @param supplier method to determine the value for the knob led.
   * @return
   */
  public LedCCRanged getEncoderLed(int index, Supplier<Integer> supplier){
    int status = ShortMidiMessage.CONTROL_CHANGE;
    int cc = TWISTER_CC_LIST[index];
    LedCCRanged led = new LedCCRanged(outputPort, status, cc, supplier);
    return led;
  }
}

