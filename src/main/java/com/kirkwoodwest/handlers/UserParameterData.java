package com.kirkwoodwest.handlers;

import com.bitwig.extension.controller.api.Parameter;

public class UserParameterData {

  private final String path;
  private final Parameter parameter;
  private final String pathValue;
  private final String pathName;
  private final String pathDisplayedValue;
  private String name;
  private Double value;
  private Integer valueInt;
  private String displayedValue;

  private boolean nameIsDirty = false;
  private boolean valueIsDirty = false;
  private boolean displayedValueIsDirty = false;

  public UserParameterData(Parameter parameter, String name, Double value, String displayedValue, String path, boolean valuesOnlyMode) {
    this.parameter = parameter;
    this.name = name;
    this.value = value;

    if(valuesOnlyMode) {
      this.pathValue = path;
      this.pathName = path;
      this.pathDisplayedValue = path;
    } else {
      this.pathValue = path + "/value";
      this.pathName = path + "/name";
      this.pathDisplayedValue = path + "/display_value";
    }

    parameter.name().addValueObserver(this::setName);
    parameter.value().addValueObserver(this::setValue);
    parameter.displayedValue().addValueObserver(this::setDisplayedValue);

    this.path = path;
    this.displayedValue = displayedValue;
  }

  public String getName() {
    return name;
  }

  public Double getValue() {
    return value;
  }

  public void setValueInt(int value) {
    valueInt = value;
  }

  public int getValueInt() {
    return valueInt;
  }

  public String getDisplayedValue() {
    return displayedValue;
  }

  public void setName(String name) {
    this.name = name;
    nameIsDirty = true;
  }

  public void setValue(Double value) {
    this.value = value;
    valueIsDirty = true;
  }

  public void setDisplayedValue(String displayedValue) {
    this.displayedValue = displayedValue;
    displayedValueIsDirty = true;
  }

  public boolean isNameDirty() {
    return nameIsDirty;
  }

  public boolean isValueDirty() {
    return valueIsDirty;
  }

  public boolean isDisplayedValueDirty() {
    return displayedValueIsDirty;
  }

  public void clearNameDirty() {
    nameIsDirty = false;
  }

  public void clearValueDirty() {
    valueIsDirty = false;
  }

  public void clearDisplayedValueDirty() {
    displayedValueIsDirty = false;
  }

  public Parameter getParameter() {
    return parameter;
  }

  public String getPathValue() {
    return pathValue;
  }

  public String getPathName() {
    return pathName;
  }

  public String getPathDisplayedValue() {
    return pathDisplayedValue;
  }
}
