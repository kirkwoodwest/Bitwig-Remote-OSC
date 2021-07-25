package com.kirkwoodwest.bitwig.extension;

// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;


public abstract class GenericControllerExtension extends ControllerExtension
{

  /**
   * Constructor.
   *
   * @param definition A definition
   * @param host The DAW host
   */
  public GenericControllerExtension (final ControllerExtensionDefinition definition, final ControllerHost host)
  {
    super (definition, host);
  }

  public void refreshControllerParams(String controllerID) {}
}
