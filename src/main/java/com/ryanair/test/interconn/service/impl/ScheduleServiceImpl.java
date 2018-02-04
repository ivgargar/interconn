package com.ryanair.test.interconn.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ryanair.test.interconn.model.Schedule;
import com.ryanair.test.interconn.service.ScheduleService;

@Service
public class ScheduleServiceImpl implements ScheduleService{
	private final Logger log = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Schedule getSchedule(String departure, String arrival, int year, int month) {
		Schedule schedule = null;
		try {
			log.debug("Retrieving schedule");
			ResponseEntity<Schedule> response = restTemplate.getForEntity("https://api.ryanair.com/timetable/3/schedules/" + departure + "/" + arrival + "/years/" + year + "/months/" + month, Schedule.class);
			schedule = response.getBody();
		} catch (HttpClientErrorException e) {
			log.error("Error " + e.getStatusCode().value() + " retrieving schedule for departure " + departure + " and arrival " + arrival + " on year " + year + " and month " + month);
		} catch (Exception e) {
			log.error("Unexpected error", e);
		}
		return schedule;
	}

}
