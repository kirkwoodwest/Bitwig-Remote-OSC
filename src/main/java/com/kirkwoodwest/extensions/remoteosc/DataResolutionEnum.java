package com.kirkwoodwest.extensions.remoteosc;

import com.bitwig.extension.controller.api.EnumDefinition;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.EnumValueDefinition;

public class DataResolutionEnum {
  private static final String RESOLUTION_1 = "Float";
  private static final String RESOLUTION_2 = "127";
  private static final String RESOLUTION_3 = "1024";
  private static final String RESOLUTION_4 = "16384";

  private static final String[] VALUES = new String[] {RESOLUTION_1, RESOLUTION_2, RESOLUTION_3, RESOLUTION_4};

  static public int size() {
    return VALUES.length;
  }

  static public String getValueText(int index) {
    return VALUES[index];
  }

  static public String[] getValues() {
    return VALUES;
  }

  static public String getIndexFor(String id) {
    for (int i = 0; i < VALUES.length; i++) {
      if (VALUES[i].equals(id)) {
        return getValueText(i);
      }
    }
    return null;
  }

  static public DataResolution getResolutionFor(String id) {
    for (int i = 0; i < VALUES.length; i++) {
      if (VALUES[i].equals(id)) {
        return DataResolution.values()[i];
      }
    }
    return null;
  }
}