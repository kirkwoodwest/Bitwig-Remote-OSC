package com.kirkwoodwest.hardware;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiDataReceivedCallback;
import com.bitwig.extension.controller.api.*;
import com.kirkwoodwest.utils.led.LedCCRanged;
import com.kirkwoodwest.utils.led.LedNoteOnOff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

public class HardwareLaunchControlXL extends HardwareBasic {
  HardwareSurface hardware_surface;

  public ArrayList<AbsoluteHardwareKnob>  hardware_knobs_row_1;
  public ArrayList<AbsoluteHardwareKnob>  hardware_knobs_row_2;
  public ArrayList<AbsoluteHardwareKnob>  hardware_knobs_row_3;
  public ArrayList<AbsoluteHardwareKnob>  hardware_faders;
  private final ArrayList<HardwareButton> hardware_buttons_row_1;
  private final ArrayList<HardwareButton> hardware_buttons_row_2;

  //Sysex Template Selection
  public static final int LAUNCH_CTRL_SYSEX_TEMPLATE_ID = 9;
  public static final String LAUNCH_CTRL_SELECT_TEMPLATE = arrayToSysex(new int[]{240, 0, 32, 41, 2, 17, 119, LAUNCH_CTRL_SYSEX_TEMPLATE_ID, 247});
  public static final String LAUNCH_CTRL_LED_SYSEX_START = arrayToSysex(new int[]{240, 0, 32, 41, 2, 17, 120, LAUNCH_CTRL_SYSEX_TEMPLATE_ID});
  public static final String LAUNCH_CTRL_LED_SYSEX_END = arrayToSysex(new int[]{247});




  //LED COLORS
  public static final int LED_OFF = 12;
  public static final int LED_RED_LOW = 13;
  public static final int LED_RED_HIGH = 15;
  public static final int LED_AMBER_LOW = 29;
  public static final int LED_AMBER_HIGH = 63;
  public static final int LED_YELLOW_HIGH = 62;
  public static final int LED_GREEN_LOW = 28;
  public static final int LED_GREEN_HIGH = 60;

  public static final int LED_RED_FLASH = 11;
  public static final int LED_AMBER_FLASH = 59;
  public static final int LED_YELLOW_FLASH = 58;
  public static final int LED_GREEN_FLASH = 56;

  public static final int[] LED_KNOBS_1 = fillArrayWithRange(0, 7);
  public static final int[] LED_KNOBS_2 = fillArrayWithRange(8, 15);
  public static final int[] LED_KNOBS_3 = fillArrayWithRange(16, 23);
  public static final int[] LED_BTNS_1 = fillArrayWithRange(24, 31);
  public static final int[] LED_BTNS_2 = fillArrayWithRange(32, 49);

  public static final int[][] KNOB_LEDS_INIT = {
          new int[]{LED_KNOBS_1[0], LED_GREEN_LOW,    LED_KNOBS_2[0], LED_GREEN_LOW,   LED_KNOBS_3[0], LED_GREEN_LOW,   LED_BTNS_1[0], LED_OFF, LED_BTNS_1[0], LED_OFF},
          new int[]{LED_KNOBS_1[1], LED_AMBER_LOW,    LED_KNOBS_2[1], LED_AMBER_LOW,   LED_KNOBS_3[1], LED_AMBER_LOW,   LED_BTNS_2[1], LED_OFF, LED_BTNS_2[1], LED_OFF},
          new int[]{LED_KNOBS_1[2], LED_RED_LOW,      LED_KNOBS_2[2], LED_RED_LOW,     LED_KNOBS_3[2], LED_RED_LOW,     LED_BTNS_2[2], LED_OFF, LED_BTNS_2[2], LED_OFF},
          new int[]{LED_KNOBS_1[3], LED_RED_LOW,      LED_KNOBS_2[3], LED_RED_LOW,     LED_KNOBS_3[3], LED_RED_LOW,     LED_BTNS_2[3], LED_OFF, LED_BTNS_2[3], LED_OFF},
          new int[]{LED_KNOBS_1[4], LED_RED_LOW,      LED_KNOBS_2[4], LED_RED_LOW,     LED_KNOBS_3[4], LED_RED_LOW,     LED_BTNS_2[4], LED_OFF, LED_BTNS_2[4], LED_OFF},
          new int[]{LED_KNOBS_1[5], LED_GREEN_LOW,    LED_KNOBS_2[5], LED_GREEN_LOW,   LED_KNOBS_3[5], LED_GREEN_LOW,   LED_BTNS_2[5], LED_OFF, LED_BTNS_2[5], LED_OFF},
          new int[]{LED_KNOBS_1[6], LED_AMBER_LOW,    LED_KNOBS_2[6], LED_AMBER_LOW,   LED_KNOBS_3[6], LED_AMBER_LOW,   LED_BTNS_2[6], LED_OFF, LED_BTNS_2[6], LED_OFF},
          new int[]{LED_KNOBS_1[7], LED_YELLOW_HIGH,  LED_KNOBS_2[7], LED_YELLOW_HIGH, LED_KNOBS_3[7], LED_YELLOW_HIGH, LED_BTNS_2[7], LED_OFF, LED_BTNS_2[7], LED_OFF},
  };

  public static final String LAUNCH_LED_INIT = array2dToSysexMessage(KNOB_LEDS_INIT);

  public static final int[] LAUNCH_CONTROL_KNOBS_1 = fillArrayWithRange(13, 20);
  public static final int[] LAUNCH_CONTROL_KNOBS_2 = fillArrayWithRange(29, 36);
  public static final int[] LAUNCH_CONTROL_KNOBS_3 = fillArrayWithRange(49, 56);
  public static final int[] LAUNCH_CONTROL_FADERS = fillArrayWithRange(77, 84);

  //TODO: REMOVE the old xtouch compact references...
  private final int MIDI_CHANNEL = 0;

  //Launch Control XL Buttons
  private final int[] BTN_ROW_1 = combineArrays(fillArrayWithRange(41, 44), fillArrayWithRange(57,60));
  private final int[] BTN_ROW_2 = combineArrays(fillArrayWithRange(73, 76), fillArrayWithRange(89,92));

  public HardwareLaunchControlXL(ControllerHost host, int midi_port, ShortMidiDataReceivedCallback input_callback) {
    super(host.getMidiInPort(midi_port), host.getMidiOutPort(midi_port), input_callback );
    hardware_surface = host.createHardwareSurface();

    //Build Buttons
    hardware_buttons_row_1 = buildHardwareButtons("LAUCNCH_CONTROL_XL_BUTTONS_1", BTN_ROW_1,  MIDI_CHANNEL);
    hardware_buttons_row_2 = buildHardwareButtons("LAUCNCH_CONTROL_XL_BUTTONS_2", BTN_ROW_2,  MIDI_CHANNEL);

    hardware_knobs_row_1 = buildAbsoluteControls("LAUCNCH_CONTROL_XL_KNOBS_1", LAUNCH_CONTROL_KNOBS_1, MIDI_CHANNEL);
    hardware_knobs_row_2 = buildAbsoluteControls("LAUCNCH_CONTROL_XL_KNOBS_2", LAUNCH_CONTROL_KNOBS_2, MIDI_CHANNEL);
    hardware_knobs_row_3= buildAbsoluteControls("LAUCNCH_CONTROL_XL_KNOBS_3", LAUNCH_CONTROL_KNOBS_3, MIDI_CHANNEL);
    hardware_faders = buildAbsoluteControls("LAUCNCH_CONTROL_XL_FADERS", LAUNCH_CONTROL_FADERS, MIDI_CHANNEL);

    //TODO: Setup Leds?
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


  private void bindHardwareButton(HardwareButton button, HardwareActionBindable pressed_action, HardwareActionBindable released_action){
    if(pressed_action != null) button.pressedAction().setBinding( pressed_action);
    if(released_action != null) button.releasedAction().setBinding( released_action);
    if(pressed_action == null && released_action == null) {
      button.pressedAction().clearBindings();
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

  public static String arrayToSysex(int[] sysex_array) {
    String sysex_string = String.format("%02X", sysex_array[0]);
    for(int i=1;i<sysex_array.length;i++){
      sysex_string = sysex_string.concat(" " + String.format("%02X", sysex_array[i]));
    }
    return sysex_string;
  }

  private static String array2dToSysex(int[][] sysex_array_2d) {
    String sysex_string = arrayToSysex(sysex_array_2d[0]);
    for(int i=1;i<sysex_array_2d.length;i++){
      sysex_string = sysex_string.concat(" " + arrayToSysex(sysex_array_2d[i]));
    }
    return sysex_string;
  }

  private static String arrayToSysexMessage(int[] sysex_array) {
    String s = arrayToSysex(sysex_array);
    String sysex_string = LAUNCH_CTRL_LED_SYSEX_START + " " + s + " " + LAUNCH_CTRL_LED_SYSEX_END;
    return sysex_string;
  }

  private static String array2dToSysexMessage(int[][] sysex_array_2d) {
    String s = array2dToSysex(sysex_array_2d);
    String sysex_string = LAUNCH_CTRL_LED_SYSEX_START + " " + s + " " + LAUNCH_CTRL_LED_SYSEX_END;
    return sysex_string;
  }
}