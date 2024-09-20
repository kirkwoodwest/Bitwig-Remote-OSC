package com.kirkwoodwest.extensions.remoteosc;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.UUID;

public class RemoteOscNoMidiExtensionDefinition extends RemoteOscExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("12514402-b6f9-441d-896a-abb73eb61396");

   public RemoteOscNoMidiExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "Remote OSC";
   }
   
   @Override
   public String getAuthor()
   {
      return "Kirkwood West";
   }

   @Override
   public String getVersion()
   {
      return "1.3.2";
   }

   @Override
   public UUID getId()
   {
      return DRIVER_ID;
   }
   
   @Override
   public String getHardwareVendor()
   {
      return "Open Sound Control";
   }
   
   @Override
   public String getHardwareModel()
   {
      return "Remote OSC";
   }

   @Override
   public int getRequiredAPIVersion()
   {
      return 17;
   }

   @Override
   public int getNumMidiInPorts() { return 0; }

   @Override
   public int getNumMidiOutPorts()
   {
      return 0;
   }

   @Override
   public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
   {
   }

   @Override
   public RemoteOscExtension createInstance(final ControllerHost host)
   {
      return new RemoteOscExtension(this, host);
   }
}