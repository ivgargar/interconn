package com.ryanair.test.interconn.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ryanair.test.interconn.model.Flight;
import com.ryanair.test.interconn.model.Route;
import com.ryanair.test.interconn.model.Schedule;
import com.ryanair.test.interconn.service.RouteService;
import com.ryanair.test.interconn.service.ScheduleService;

@RestController
@RequestMapping("/api")
public class FlightsController {
	
	@Autowired
	RouteService routeService;
	
	@Autowired
	ScheduleService scheduleService;

	@RequestMapping("/interconnections")
	public List<Flight> getInterconnections(@RequestParam String departure, @RequestParam String arrival,
			@RequestParam String departureDateTime, @RequestParam String arrivalDateTime) {
		List<Route> routes = routeService.getRoutes();
		
		List<Route> validRoutes = routes
			.stream()
			.filter(route -> route.getConnectingAirport() == null 
					&& (route.getAirportFrom().equals(departure) || route.getAirportTo().equals(arrival)))
			.collect(Collectors.toList());
		
		Schedule schedule = scheduleService.getSchedule();
		
		return new ArrayList<Flight>();
	}
}
