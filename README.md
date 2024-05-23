# Remote OSC Extension For Bitwig
This is a basic osc extension for bitwig that allows for many remote parameter mappings as you need. This source could also be used for writing a new extension with more features. If you do use it please drop me a message.

#Whats New
## 1.2 

### Values Only Mode
You can now choose between Values Only or have also displayed values and parameter names. Read further to find the paths to the new addresses.

### No Midi Option (Default)


## Up to 1024 User Parameters
User Parameters are the right click mapped values.
- `/user/0001/value` `,f`
- `/user/0002/value` `,f` 

### Standard Values Mode
- `/user/0001/value` `,f`
- `/user/0001/name` `,s`
- `/user/0001/display_value` `,s`

### Values Only mode
- `/user/0001` `,f`

## VU Meters
- `/track/1/vu/peak`
- `/track/1/vu/rms`

## Paths

## Midi Remote
This also includes a midi input which you can use for midi mapping as well. If you aren't using this feature just select any other device to fill it out and bitwig will be ok.

...

## Configuration

### User Controls
- You'll need to fill in the ports and addresses for your OSC Device
- osc Base Target is your base address... i.e. /user/
- Index Zero Padding is to padd out the addresses so they appear in bitwig in order.
- Send Values after received will resend the data back to your device after its set in Bitwig. (Careful this can cause feedback loops)

<img width="657" alt="image" src="https://github.com/kirkwoodwest/Bitwig-Remote-OSC/assets/6645471/7ab1799e-0c53-428a-bf16-cf6249c69f50">


### VU Meters
- If you enable VU Meters, you'll have access to 8 VU Meters that follow a cursorTrack

#### Addresses for VU Meters
The index in the middle goes up to 8,
/track/1/vu/peak
/track/1/vu/rms
/track/.../vu/peak
/track/.../vu/rms

![image](https://user-images.githubusercontent.com/6645471/211904600-8746b846-01dc-4dfe-afd1-be1de5346476.png)

In the Bitwig Studio I/O panel there is a cursor track which you can use to set the first index of your VU Meter point. The remaining 7 channels follow to the right of it. 

---
Thanks to Reframinator for helping to design and test this extension. Check out his youtube channel. https://www.youtube.com/@Reframinator

Also bigups to #controllerism on the Bitwig Discord Server and Moss for helping to inspire the Bitwig API Community.
