package com.kirkwoodwest.extensions.testscript;
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class TestScriptExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("00e43239-8c98-4cfa-9b05-4edb673b6cf2");
   
   public TestScriptExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "TestScript";
   }
   
   @Override
   public String getAuthor()
   {
      return "kirkwoodwest";
   }

   @Override
   public String getVersion()
   {
      return "0.1";
   }

   @Override
   public UUID getId()
   {
      return DRIVER_ID;
   }
   
   @Override
   public String getHardwareVendor()
   {
      return "Kirkwood West";
   }
   
   @Override
   public String getHardwareModel()
   {
      return "TestScript";
   }

   @Override
   public int getRequiredAPIVersion()
   {
      return 12;
   }

   @Override
   public int getNumMidiInPorts()
   {
      return 1;
   }

   @Override
   public int getNumMidiOutPorts()
   {
      return 1;
   }

   @Override
   public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
   {
   }

   @Override
   public TestScriptExtension createInstance(final ControllerHost host)
   {
      return new TestScriptExtension(this, host);
   }
}
