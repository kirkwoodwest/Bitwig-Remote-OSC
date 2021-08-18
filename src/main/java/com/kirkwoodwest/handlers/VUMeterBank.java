package com.kirkwoodwest.handlers;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;
import com.kirkwoodwest.utils.osc.OscHandler;

public class VUMeterBank {

	private final CursorTrack cursor_track;
	private final TrackBank track_bank;
	private int NUM_TRACKS_IN_BANK = 8;
	private boolean vu_meters_enabled = true;

	public VUMeterBank(ControllerHost host, OscHandler osc_handler) {
		
		cursor_track = host.createCursorTrack("VU Meter Bank", "VU Meter Bank", 0,0,true);
		track_bank = cursor_track.createSiblingsTrackBank(NUM_TRACKS_IN_BANK,0,0,true, true);

		for(int i=0;i<NUM_TRACKS_IN_BANK;i++) {
			Track channel = track_bank.getItemAt(i);
			final String vu_base = "/track/" + (i+1)+ "/vu";
			{
				//VU Meter  Peak
				final String target_peak = vu_base + "/peak";
				boolean      is_peak     = true;
				channel.addVuMeterObserver(1023, -1, is_peak, (vu_level) -> {
					if(vu_meters_enabled) {
						osc_handler.addMessageToQueue(target_peak, (int) vu_level);
					}
				});
			}
			{
				//VU Meter RMS
				final String target_rms = vu_base + "/rms";
				boolean      is_peak    = false;
				channel.addVuMeterObserver(1023, -1, is_peak, (vu_level) -> {
					if (vu_meters_enabled) {
						osc_handler.addMessageToQueue(target_rms, (int) vu_level);
					}
				});
			}
		}
	}
}
