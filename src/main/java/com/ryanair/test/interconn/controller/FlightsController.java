package com.ryanair.test.interconn.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	RouteService routeService;
	
	@Autowired
	ScheduleService scheduleService;

	/**
	 * Returns the interconnections from departure to arrival on given dates.
	 * @param departure the departure airport
	 * @param arrival the arrival airport
	 * @param departureDateTime the departure date time
	 * @param arrivalDateTime the arrival departure date time
	 * @return a list of interconnections
	 */
	@RequestMapping("/interconnections")
	public List<Interconnection> getInterconnections(@RequestParam String departure, @RequestParam String arrival,
			@RequestParam String departureDateTime, @RequestParam String arrivalDateTime) {
		log.debug("Start retrieving interconnections");
		
		List<Interconnection> interconnectionList = new ArrayList<Interconnection>();
		
		List<Route> routes = routeService.getRoutes();
		
		LocalDateTime parsedDepartureDateTime = LocalDateTime.parse(departureDateTime);
		LocalDateTime parsedArrivalDateTime = LocalDateTime.parse(arrivalDateTime);
		
		log.debug("Looking for valid routes");
		routes.stream()
		.filter(route -> route.getConnectingAirport() == null && route.getAirportTo().equals(arrival))
		.forEach(route -> {
			 log.debug("Looking for valid flights");
			 if (route.getAirportFrom().equals(departure)) {
				 log.debug("Flight with direct connection");
				 //direct connection
				 Schedule schedule = scheduleService.getSchedule(route.getAirportFrom(), route.getAirportTo(), parsedDepartureDateTime.getYear(), parsedDepartureDateTime.getMonthValue());
				 //check flights
				 if (schedule != null) {
					 log.debug("Checking direct flights");
					 List<Leg> legs = getLegs(schedule, parsedDepartureDateTime, parsedArrivalDateTime, route.getAirportFrom(), route.getAirportTo());
					 if (!legs.isEmpty()) {
						 log.debug("Adding interconnection");
						 Interconnection interconnection = new Interconnection();
						 interconnection.setStops(0);
						 interconnection.setLegs(legs);
						 interconnectionList.add(interconnection);
					 }
				 }
			 } else {
				 log.debug("Flight with no direct connection");
				 //search 1 stop connection
				 Schedule scheduleFromDeparture = scheduleService.getSchedule(departure, route.getAirportFrom(), parsedDepartureDateTime.getYear(), parsedDepartureDateTime.getMonthValue());
				 if (scheduleFromDeparture != null) {
					 //check departure flights
					 log.debug("Checking departure flights");
					 List<Leg> legsFromDeparture = getLegs(scheduleFromDeparture, parsedDepartureDateTime, parsedArrivalDateTime, departure, route.getAirportFrom());
					 if (!legsFromDeparture.isEmpty()) {
						 Schedule scheduleToArrival = scheduleService.getSchedule(route.getAirportFrom(), route.getAirportTo(), parsedDepartureDateTime.getYear(), parsedDepartureDateTime.getMonthValue());
						 //check arrival flights
						 log.debug("Checking arrival flights");
						 if (scheduleToArrival != null) {
							List<Leg> legsToArrival = getLegs(scheduleToArrival, parsedDepartureDateTime.plusHours(2), parsedArrivalDateTime, route.getAirportFrom(), arrival);
							for (Leg legToArrival : legsToArrival) {
								LocalDateTime legToArrivalDepartureTime = LocalDateTime.parse(legToArrival.getDepartureDateTime());
								for (Leg legFromDeparture : legsFromDeparture) {
									LocalDateTime legFromDepartureArrivalDateTime = LocalDateTime.parse(legFromDeparture.getArrivalDateTime());
									if (legFromDepartureArrivalDateTime.isBefore(legToArrivalDepartureTime)) {
										log.debug("Adding interconnection");
										Interconnection interconnection = new Interconnection();
										interconnection.setStops(1);
										List<Leg> legs = new ArrayList<Leg>();
										legs.add(legFromDeparture);
										legs.add(legToArrival);
										interconnection.setLegs(legs);
										interconnectionList.add(interconnection);
									}
								}
							}
						 }
					 }
				 }
			 }
		});
		
		log.debug("End retrieving interconnections");
		
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
		for (Day day : schedule.getDays()) {
			log.debug("Checking day");
			if (day.getDay() >= departureDateTime.getDayOfMonth() && day.getDay() <= arrivalDateTime.getDayOfMonth()) {
				for (Flight flight : day.getFlights()) {
					log.debug("Checking flight time");
					String[] departureTime = flight.getDepartureTime().split(":");
					LocalDateTime flightDepartureDateTime = LocalDateTime.of(departureDateTime.getYear(), schedule.getMonth(), day.getDay(), Integer.parseInt(departureTime[0]), Integer.parseInt(departureTime[1]));
					
					String[] arrivalTime = flight.getArrivalTime().split(":");
					LocalDateTime flightArrivalDateTime = LocalDateTime.of(arrivalDateTime.getYear(), schedule.getMonth(), day.getDay(), Integer.parseInt(arrivalTime[0]), Integer.parseInt(arrivalTime[1]));
					
					if ((flightDepartureDateTime.isAfter(departureDateTime) || flightDepartureDateTime.isEqual(departureDateTime)) 
							&& (flightArrivalDateTime.isBefore(arrivalDateTime) || flightArrivalDateTime.isEqual(arrivalDateTime))) {
						log.debug("Valid flight. Creating leg.");
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
		return legs;
	}}
