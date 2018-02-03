package com.ryanair.test.interconn.service;

import com.ryanair.test.interconn.model.Schedule;

public interface ScheduleService {
	/**
	 * Returns a list of available flights for a given departure airport IATA code, an arrival airport IATA code, a year and a month.
	 * @param departure the departure airport
	 * @param arrival the arrival airport
	 * @param year
	 * @param month
	 * @return a list of flights
	 */
	public Schedule getSchedule(String departure, String arrival, int year, int month);
}
