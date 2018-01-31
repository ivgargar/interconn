package com.ryanair.test.interconn.service.impl;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ryanair.test.interconn.model.Schedule;
import com.ryanair.test.interconn.service.ScheduleService;

@Service
public class ScheduleServiceImpl implements ScheduleService{
    
	private final RestTemplate restTemplate;
    
    public ScheduleServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    // TODO add parameters
	@Override
	public Schedule getSchedule() {
		ResponseEntity<Schedule> response = restTemplate.getForEntity("https://api.ryanair.com/timetable/3/schedules/DUB/WRO/years/2018/months/6", Schedule.class);
		return response.getBody();
	}

}
