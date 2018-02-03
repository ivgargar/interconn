package com.ryanair.test.interconn.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ryanair.test.interconn.model.Schedule;
import com.ryanair.test.interconn.service.ScheduleService;

@Service
public class ScheduleServiceImpl implements ScheduleService{
    
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Schedule getSchedule(String departure, String arrival, int year, int month) {
		ResponseEntity<Schedule> response = restTemplate.getForEntity("https://api.ryanair.com/timetable/3/schedules/" + departure + "/" + arrival + "/years/" + year + "/months/" + month, Schedule.class);
		return response.getBody();
	}

}
