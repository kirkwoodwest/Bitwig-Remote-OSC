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
	private boolean peak_enabled = true;
	private boolean rms_enabled = true;

	public VUMeterBank(ControllerHost host, OscHandler osc_handler, boolean vu_meters_enabled, boolean  peak_enabled, boolean rms_enabled) {


		cursor_track = host.createCursorTrack("VU Meter Bank", "VU Meter Bank", 0,0,true);
		track_bank = cursor_track.createSiblingsTrackBank(NUM_TRACKS_IN_BANK,0,0,true, true);
		if (vu_meters_enabled) {
			for (int i = 0; i < NUM_TRACKS_IN_BANK; i++) {
				Track        channel = track_bank.getItemAt(i);
				final String vu_base = "/track/" + (i + 1) + "/vu";

				if (peak_enabled) {
					//VU Meter  Peak
					final String target_peak = vu_base + "/peak";
					boolean      is_peak     = true;
					channel.addVuMeterObserver(1023, -1, is_peak, (vu_level) -> {
						if (vu_meters_enabled) {
							//osc_handler.addMessageToQueue(target_peak, (int) vu_level);
							osc_handler.sendMessage(target_peak, (int) vu_level);
						}
					});
				}
				if (rms_enabled) {
					//VU Meter RMS
					final String target_rms = vu_base + "/rms";
					boolean      is_peak    = false;
					channel.addVuMeterObserver(1023, -1, is_peak, (vu_level) -> {
						if (vu_meters_enabled) {
							//	osc_handler.addMessageToQueue(target_rms, (int) vu_level);
							osc_handler.sendMessage(target_rms, (int) vu_level);
						}
					});
				}
			}
		}
	}

	public void setPeakOutput(boolean b) {
		peak_enabled = b;
	}

	public void setRmsOutput(boolean b) {
		rms_enabled = b;
	}
}
