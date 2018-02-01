package com.ryanair.test.interconn.service;

import com.ryanair.test.interconn.model.Schedule;

public interface ScheduleService {
	public Schedule getSchedule(String departure, String arrival, int year, int month);
}
