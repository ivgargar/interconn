package com.ryanair.test.interconn.model;

import java.util.List;

public class Day {
	private int day;
	private List<ScheduledFlight> flights;
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public List<ScheduledFlight> getFlights() {
		return flights;
	}
	public void setFlights(List<ScheduledFlight> flights) {
		this.flights = flights;
	}
}
