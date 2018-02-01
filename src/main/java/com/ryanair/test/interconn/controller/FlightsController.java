package com.ryanair.test.interconn.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ryanair.test.interconn.model.Day;
import com.ryanair.test.interconn.model.Flight;
import com.ryanair.test.interconn.model.Interconnection;
import com.ryanair.test.interconn.model.Leg;
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
	public List<Interconnection> getInterconnections(@RequestParam String departure, @RequestParam String arrival,
			@RequestParam String departureDateTime, @RequestParam String arrivalDateTime) {
		List<Interconnection> interconnectionList = new ArrayList<Interconnection>();
		
		List<Route> routes = routeService.getRoutes();
		
		/*
		routes
			.stream()
			.filter(route -> route.getConnectingAirport() == null && route.getAirportFrom().equals(departure))
			.forEach(route -> {
				System.out.println(route.getAirportFrom() + "-" + route.getAirportTo());
			});
		*/
		LocalDateTime parsedDepartureDateTime = LocalDateTime.parse(departureDateTime);
		int departureYear = parsedDepartureDateTime.getYear();
		int departureMonth = parsedDepartureDateTime.getMonthValue();
		int departureDay = parsedDepartureDateTime.getDayOfMonth();
		
		LocalDateTime parsedArrivalDateTime = LocalDateTime.parse(arrivalDateTime);
		int arrivalYear = parsedArrivalDateTime.getYear();
		int arrivalMonth = parsedArrivalDateTime.getMonthValue();
		int arrivalDay = parsedArrivalDateTime.getDayOfMonth();
		
		List<String> validDestinations = routes
			.stream()
			.filter(route -> route.getConnectingAirport() == null && route.getAirportFrom().equals(departure))
			.map(route -> route.getAirportTo())
			.collect(Collectors.toList());
		
		routes
			.stream()
			.filter(route -> route.getConnectingAirport() == null 
					&& route.getAirportTo().equals(arrival) 
					&& (route.getAirportFrom().equals(departure) || validDestinations.contains(route.getAirportFrom())))
			.forEach(route -> {
				System.out.println(route.getAirportFrom() + "-" + route.getAirportTo());
				// 0 stops
				if (route.getAirportFrom().equals(departure)) {
					
					
					Leg leg = new Leg();
					leg.setDepartureAirport(route.getAirportFrom());
					leg.setArrivalAirport(route.getAirportTo());
					
					Schedule schedule = scheduleService.getSchedule(route.getAirportFrom(), route.getAirportTo(), departureYear, departureMonth);
					
					//TODO filter fligths by day and time
					schedule.getDays()
						.stream()
						.filter(day -> day.getDay() >= departureDay && day.getDay() <= arrivalDay)
						.collect(Collectors.toList());
					
					//TODO set leg departure and arrival time
					
					List<Leg> legs = new ArrayList<Leg>();
					legs.add(leg);
					
					Interconnection interconnection = new Interconnection();
					interconnection.setStops(0);
					interconnection.setLegs(legs);
					
					interconnectionList.add(interconnection);
				} else {
					// 1 stop
					//TODO schedule from departure to airportFrom
					Schedule schedule1 = scheduleService.getSchedule(departure, route.getAirportFrom(), departureYear, departureMonth);
					
					//TODO filter fligths by day and time
					
					//TODO schedule from airportFrom to arrival
					Schedule schedule2 = scheduleService.getSchedule(route.getAirportFrom(), arrival, departureYear, departureMonth);
					
					//TODO filter fligths by day and time
				}
			});
		
		
		return interconnectionList;
	}
}
