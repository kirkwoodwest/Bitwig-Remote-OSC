package com.kirkwoodwest.hardware;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiDataReceivedCallback;
import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.utils.led.LedCCRanged;
import com.kirkwoodwest.utils.led.LedNoteOnOff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

public class HardwareMidiMix extends HardwareBasic {
  HardwareSurface hardware_surface;

  public ArrayList<AbsoluteHardwareKnob>  hardware_knobs_row_1;
  public ArrayList<AbsoluteHardwareKnob>  hardware_knobs_row_2;
  public ArrayList<AbsoluteHardwareKnob>  hardware_knobs_row_3;
  public ArrayList<AbsoluteHardwareKnob>  hardware_faders;
  private final ArrayList<HardwareButton> hardware_buttons_row_1;
  private final ArrayList<HardwareButton> hardware_buttons_row_2;
  private final ArrayList<HardwareButton> hardware_buttons_row_3;


  public static int MIDI_CHANNEL = 4; //MIDI channel is 5.

  public static final int[] CC_KNOBS_1 = fillArrayWithRange(16, 23);
  public static final int[] CC_KNOBS_2 = fillArrayWithRange(24, 31);
  public static final int[] CC_KNOBS_3 = fillArrayWithRange(32, 39);
  public static final int[] CC_FADERS  = fillArrayWithRange(40, 48);

  private final int[] BTN_ROW_1 = fillArrayWithRange(1, 8); //MUTE
  private final int[] BTN_ROW_2 = fillArrayWithRange(9, 16); //SOLO
  private final int[] BTN_ROW_3 = fillArrayWithRange(17, 24); //RECORD ARM

  public static final int[] LED_BTNS_1 =  new int[]{1,4,7,10,13,16, 19, 22 }; //MUTE
  public static final int[] LED_BTNS_2 =  new int[]{2,5,8,11,14,17, 20, 23 }; //SOLO
  public static final int[] LED_BTNS_3 =  new int[]{3,6,9,12,15,18, 21, 24 }; //RECORD ARM

  public static final int BTN_RESAMPLE_1BAR = LED_BTNS_1[4];
  public static final int BTN_RESAMPLE_2BAR = LED_BTNS_1[5];
  public static final int BTN_RESAMPLE_4BAR = LED_BTNS_2[4];
  public static final int BTN_RESAMPLE_8BAR = LED_BTNS_2[5];



  public HardwareMidiMix(ControllerHost host, int midi_port, ShortMidiDataReceivedCallback input_callback) {
    super(host.getMidiInPort(midi_port), host.getMidiOutPort(midi_port), input_callback );
    hardware_surface = host.createHardwareSurface();

    //Build Buttons
    hardware_buttons_row_1 = buildHardwareButtons("MIDIMIX_BUTTONS_1", BTN_ROW_1,  MIDI_CHANNEL);
    hardware_buttons_row_2 = buildHardwareButtons("MIDIMIX_BUTTONS_2", BTN_ROW_2,  MIDI_CHANNEL);
    hardware_buttons_row_3 = buildHardwareButtons("MIDIMIX_BUTTONS_3", BTN_ROW_3,  MIDI_CHANNEL);

    hardware_knobs_row_1 = buildAbsoluteControls("MIDIMIX_KNOBS_1", CC_KNOBS_1, MIDI_CHANNEL);
    hardware_knobs_row_2 = buildAbsoluteControls("MIDIMIX_KNOBS_2", CC_KNOBS_2, MIDI_CHANNEL);
    hardware_knobs_row_3= buildAbsoluteControls("MIDIMIX_KNOBS_3", CC_KNOBS_3, MIDI_CHANNEL);
    hardware_faders = buildAbsoluteControls("MIDIMIX_FADERS", CC_FADERS, MIDI_CHANNEL);

    //TODO: Setup Leds?
    //
  }

  private ArrayList<AbsoluteHardwareKnob> buildAbsoluteControls(String base_name, int[] cc_list, int midi_channel) {
    ArrayList<AbsoluteHardwareKnob> absolute_controls = new ArrayList<>();
    int num_knobs = cc_list.length;
    for (int i=0; i < num_knobs; i++ ) {
      AbsoluteHardwareKnob         hardware_knob = hardware_surface.createAbsoluteHardwareKnob(base_name + i);
      AbsoluteHardwareValueMatcher value_matcher = inputPort.createAbsoluteCCValueMatcher(midi_channel, cc_list[i]);
      hardware_knob.setAdjustValueMatcher(value_matcher);
      absolute_controls.add(hardware_knob);
    }
    return absolute_controls;
  }

  private ArrayList<HardwareButton> buildHardwareButtons(String base_name, int[] button_notes, int midi_channel) {
    //Build Buttons
    ArrayList absolute_buttons = new ArrayList<HardwareButton>();
    for(int index=0; index<button_notes.length; index++) {
      HardwareButton hardware_button = hardware_surface.createHardwareButton(base_name + index);
      HardwareActionMatcher   pressedButtonAction = inputPort.createNoteOnActionMatcher(midi_channel, button_notes[index]);
      hardware_button.pressedAction().setActionMatcher(pressedButtonAction);
      HardwareActionMatcher   releasedButtonAction = inputPort.createNoteOffActionMatcher(midi_channel, button_notes[index]);
      hardware_button.releasedAction().setActionMatcher(releasedButtonAction);
      absolute_buttons.add(hardware_button);
    }
    return absolute_buttons;
  }

  private void addBinding(AbsoluteHardwareKnob knob, HardwareBindable bindable){
    if (bindable == null) {
      knob.clearBindings();
    } else {
      knob.setBinding(bindable);
    }
  }

  public HardwareButton getHardwareButtonRow1(int index) {
    return hardware_buttons_row_1.get(index);
  }
  public HardwareButton getHardwareButtonRow2(int index) {
    return hardware_buttons_row_2.get(index);
  }
  public HardwareButton getHardwareButtonRow3(int index) { return hardware_buttons_row_3.get(index); }


  private void bindHardwareButton(HardwareButton button, HardwareActionBindable pressed_action, HardwareActionBindable released_action){
    if(pressed_action != null) {
      button.pressedAction().setBinding( pressed_action);
    } else {
      button.pressedAction().clearBindings();
    }
    if(released_action != null) {
      button.releasedAction().setBinding( released_action);
    } else {
      button.releasedAction().clearBindings();
    }
  }

  public void bindHardwareButtonRow1(int index, HardwareActionBindable pressed_action, HardwareActionBindable released_action) {
    HardwareButton button          = getHardwareButtonRow1(index);
    bindHardwareButton(button, pressed_action, released_action);
  }

  public void bindHardwareButtonRow2(int index, HardwareActionBindable pressed_action, HardwareActionBindable released_action) {
    HardwareButton button          = getHardwareButtonRow2(index);
    bindHardwareButton(button, pressed_action, released_action);
  }

  public void bindHardwareButtonRow3(int index, HardwareActionBindable pressed_action, HardwareActionBindable released_action) {
    HardwareButton button          = getHardwareButtonRow3(index);
    bindHardwareButton(button, pressed_action, released_action);
  }

  private void releaseBindingsInternal(ArrayList<AbsoluteHardwareKnob> knobs) {
    int size = knobs.size();
    for (int i = 0; i < size; i++) {
      knobs.get(i).clearBindings();
    }
  }

  private void releaseButtonBindingsInternal(ArrayList<HardwareButton> buttons) {
    int size = buttons.size();
    for (int i = 0; i < size; i++) {
      buttons.get(i).pressedAction().clearBindings();
      buttons.get(i).releasedAction().clearBindings();
    }
  }

  public void releaseBindings() {
    releaseBindingsInternal(hardware_faders);
    releaseBindingsInternal(hardware_knobs_row_1);
    releaseBindingsInternal(hardware_knobs_row_2);
    releaseBindingsInternal(hardware_knobs_row_3);
    releaseButtonBindingsInternal(hardware_buttons_row_1);
    releaseButtonBindingsInternal(hardware_buttons_row_2);
  }

  private LedCCRanged getEncoderLed(int[] cc_list, int index, Supplier<Integer> supplier) {
    int status = ShortMidiMessage.CONTROL_CHANGE;
    int cc = cc_list[index];
    LedCCRanged led = new LedCCRanged(outputPort, status, cc, supplier);
    return led;
  }


  /**
   * Returns an the button led matching the row / index.
   * @param cc_list list of cc values
   * @param index index of the button
   * @param supplier method to determine the value for the knob led.
   * @return
   */
  private LedNoteOnOff getButtonLed(int[] cc_list, int index, Supplier<Boolean> supplier){
    int status = ShortMidiMessage.CONTROL_CHANGE;
    int note = cc_list[index];
    LedNoteOnOff led = new LedNoteOnOff(outputPort, note, supplier);
    return led;
  }

  public LedNoteOnOff getButtonLedRow1( int index, Supplier<Boolean> supplier) {
    return getButtonLed(LED_BTNS_1, index, supplier);
  }
  public LedNoteOnOff getButtonLedRow2( int index, Supplier<Boolean> supplier) {
    return getButtonLed(LED_BTNS_2, index, supplier);
  }
  public LedNoteOnOff getButtonLedRow3( int index, Supplier<Boolean> supplier) {
    return getButtonLed(LED_BTNS_3, index, supplier);
  }

  private static int[] fillArrayWithRange(int min, int max){
    int count = max-min+1;
    int[] array = new int[count];
    for(int i=0; i < count; i++){
      array[i] = min+i;
    }
    return array;
  }

  private static int[] combineArrays(int[] array1, int[]array2) {
    int[] both_arrays = Arrays.copyOf(array1, array1.length + array2.length);
    System.arraycopy(array2, 0, both_arrays, array1.length, array2.length);
    return both_arrays;
  }

}