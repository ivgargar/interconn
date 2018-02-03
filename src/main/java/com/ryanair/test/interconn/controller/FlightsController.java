package com.ryanair.test.interconn.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

	/**
	 * Returns the interconnections from departure to arrivel on given dates.
	 * @param departure the departure airport
	 * @param arrival the arrival airport
	 * @param departureDateTime the departure date time
	 * @param arrivalDateTime the arrival departure date time
	 * @return a list of interconnections
	 */
	@RequestMapping("/interconnections")
	public List<Interconnection> getInterconnections(@RequestParam String departure, @RequestParam String arrival,
			@RequestParam String departureDateTime, @RequestParam String arrivalDateTime) {
		List<Interconnection> interconnectionList = new ArrayList<Interconnection>();
		
		List<Route> routes = routeService.getRoutes();
		
		LocalDateTime parsedDepartureDateTime = LocalDateTime.parse(departureDateTime);
		LocalDateTime parsedArrivalDateTime = LocalDateTime.parse(arrivalDateTime);
		
		List<String> validDestinations = routes.stream()
			.filter(route -> route.getConnectingAirport() == null && route.getAirportFrom().equals(departure))
			.map(route -> route.getAirportTo())
			.collect(Collectors.toList());
		
		routes.stream()
			.filter(route -> route.getConnectingAirport() == null 
					&& route.getAirportTo().equals(arrival) 
					&& (route.getAirportFrom().equals(departure) || validDestinations.contains(route.getAirportFrom())))
			.forEach(route -> {
				if (route.getAirportFrom().equals(departure)) {
					// 0 stops
					Schedule schedule = scheduleService.getSchedule(route.getAirportFrom(), route.getAirportTo(), parsedDepartureDateTime.getYear(), parsedDepartureDateTime.getMonthValue());
					
					List<Leg> legs = getLegs(schedule, parsedDepartureDateTime, parsedArrivalDateTime, route.getAirportFrom(), route.getAirportTo());
					if (!legs.isEmpty()) {
						Interconnection interconnection = new Interconnection();
						interconnection.setStops(0);
						interconnection.setLegs(legs);
						interconnectionList.add(interconnection);
					}

				} else {
					// 1 stop
					Schedule scheduleFromDeparture = scheduleService.getSchedule(departure, route.getAirportFrom(), parsedDepartureDateTime.getYear(), parsedDepartureDateTime.getMonthValue());
					List<Leg> legsFromDeparture = getLegs(scheduleFromDeparture, parsedDepartureDateTime, parsedArrivalDateTime, departure, route.getAirportFrom());
					
					Schedule scheduleToArrival = scheduleService.getSchedule(route.getAirportFrom(), arrival, parsedDepartureDateTime.getYear(), parsedDepartureDateTime.getMonthValue());
					List<Leg> legsToArrival = getLegs(scheduleToArrival, parsedDepartureDateTime.plusHours(2), parsedArrivalDateTime, route.getAirportFrom(), arrival);
					
					if (!legsToArrival.isEmpty()) {
						Interconnection interconnection = new Interconnection();
						interconnection.setStops(1);
						
						List<Leg> legs = new ArrayList<Leg>();
						for (Leg legToArrival : legsToArrival) {
							LocalDateTime legToArrivalDepartureTime = LocalDateTime.parse(legToArrival.getDepartureDateTime());
							for (Leg legFromDeparture : legsFromDeparture) {
								LocalDateTime legFromDepartureArrivalDateTime = LocalDateTime.parse(legFromDeparture.getArrivalDateTime());
								if (legFromDepartureArrivalDateTime.isBefore(legToArrivalDepartureTime)) {
									legs.add(legFromDeparture);
									legs.add(legToArrival);
								}
							}
						}
						interconnection.setLegs(legs);
						interconnectionList.add(interconnection);
					}
				}
			});
		return interconnectionList;
	}
	
	/**
	 * Returns the flights on schedule from a given departure airport to arrival airport within a departure date time and arrival date time.
	 * @param schedule the scheduled flights
	 * @param departureDateTime the departure date time
	 * @param arrivalDateTime the arrival date time
	 * @param airportFrom the departure airport
	 * @param airportTo the arrival airport
	 * @return a list of formatted flights
	 */
	private List<Leg> getLegs(Schedule schedule, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime, String airportFrom, String airportTo) {
		List<Leg> legs = new ArrayList<Leg>();
		if (schedule != null && schedule.getDays() != null) {
			for (Day day : schedule.getDays()) {
				if (day.getDay() >= departureDateTime.getDayOfMonth() && day.getDay() <= arrivalDateTime.getDayOfMonth()) {
					for (Flight flight : day.getFlights()) {
						String[] departureTime = flight.getDepartureTime().split(":");
						LocalDateTime flightDepartureDateTime = LocalDateTime.of(departureDateTime.getYear(), schedule.getMonth(), day.getDay(), Integer.parseInt(departureTime[0]), Integer.parseInt(departureTime[1]));
						
						String[] arrivalTime = flight.getArrivalTime().split(":");
						LocalDateTime flightArrivalDateTime = LocalDateTime.of(arrivalDateTime.getYear(), schedule.getMonth(), day.getDay(), Integer.parseInt(arrivalTime[0]), Integer.parseInt(arrivalTime[1]));
						
						if ((flightDepartureDateTime.isAfter(departureDateTime) || flightDepartureDateTime.isEqual(departureDateTime)) 
								&& (flightArrivalDateTime.isBefore(arrivalDateTime) || flightArrivalDateTime.isEqual(arrivalDateTime))) {
							Leg leg = new Leg();
							leg.setDepartureAirport(airportFrom);
							leg.setArrivalAirport(airportTo);
							
							LocalDateTime auxDateTime = LocalDateTime.of(departureDateTime.getYear(), schedule.getMonth(), day.getDay(), flightDepartureDateTime.getHour(), flightDepartureDateTime.getMinute());
							String formattedAuxDateTime = auxDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
							leg.setDepartureDateTime(formattedAuxDateTime);
							
							auxDateTime = LocalDateTime.of(arrivalDateTime.getYear(), schedule.getMonth(), day.getDay(), flightArrivalDateTime.getHour(), flightArrivalDateTime.getMinute());
							formattedAuxDateTime = auxDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
							leg.setArrivalDateTime(formattedAuxDateTime);
							
							legs.add(leg);
						}
					}
				}
			}
		}
		return legs;
	}}
