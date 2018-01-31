package com.ryanair.test.interconn.model;

import java.util.List;

public class Flight {
	private int stops;
	private List<Leg> legs;
	public int getStops() {
		return stops;
	}
	public void setStops(int stops) {
		this.stops = stops;
	}
	public List<Leg> getLegs() {
		return legs;
	}
	public void setLegs(List<Leg> legs) {
		this.legs = legs;
	}
}
