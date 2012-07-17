package com.rushfusion.mat.control;

public class PlayStatus {
	int duration;
	int maxvol;
	int pos;
	int status;
	int volume;

	public PlayStatus() {
	}

	public PlayStatus(int duration, int maxvol, int pos, int status, int volume) {
		this.duration = duration;
		this.maxvol = maxvol;
		this.pos = pos;
		this.status = status;
		this.volume = volume;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getMaxvol() {
		return maxvol;
	}

	public void setMaxvol(int maxvol) {
		this.maxvol = maxvol;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

}