// Written by Kirkwood West - kirkwoodwest.com
// (c) 2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt


package com.kirkwoodwest.extensions.testscript;
import com.bitwig.extension.controller.api.*;
import com.bitwig.extension.controller.ControllerExtension;

public class TestScriptExtension extends ControllerExtension {

  private ControllerHost host;

  protected TestScriptExtension(final TestScriptExtensionDefinition definition, final ControllerHost host) {
    super(definition, host);
  }

  @Override
  public void init() {
    host = getHost();
  }


  @Override
  public void exit() {
    // TODO: Perform any cleanup once the driver exits
    // For now just show a popup notification for verification that it is no longer running.
    getHost().showPopupNotification("NewScript Exited");
  }

  @Override
  public void flush() {
    // TODO Send any updates you need here.
  }
}
