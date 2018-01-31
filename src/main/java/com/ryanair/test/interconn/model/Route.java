package com.ryanair.test.interconn.model;

public class Route {
	private String airportFrom;
	private String airportTo;
	private String connectingAirport;
	private boolean newRoute;
	private boolean seasonalRoute;
	private String group;
	public String getAirportFrom() {
		return airportFrom;
	}
	public void setAirportFrom(String airportFrom) {
		this.airportFrom = airportFrom;
	}
	public String getAirportTo() {
		return airportTo;
	}
	public void setAirportTo(String airportTo) {
		this.airportTo = airportTo;
	}
	public String getConnectingAirport() {
		return connectingAirport;
	}
	public void setConnectingAirport(String connectingAirport) {
		this.connectingAirport = connectingAirport;
	}
	public boolean isNewRoute() {
		return newRoute;
	}
	public void setNewRoute(boolean newRoute) {
		this.newRoute = newRoute;
	}
	public boolean isSeasonalRoute() {
		return seasonalRoute;
	}
	public void setSeasonalRoute(boolean seasonalRoute) {
		this.seasonalRoute = seasonalRoute;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
}
