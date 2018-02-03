package com.ryanair.test.interconn.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryanair.test.interconn.model.Route;
import com.ryanair.test.interconn.model.Schedule;
import com.ryanair.test.interconn.service.RouteService;
import com.ryanair.test.interconn.service.ScheduleService;

import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(FlightsController.class)
public class FlightsControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
    private RouteService routeService;
	
	@MockBean
    private ScheduleService scheduleService;
	
	@MockBean
	private RestTemplate restTemplate;
	
	@Before
	public void setUp() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Route[] routes = mapper.readValue(new File(this.getClass().getResource("/routes.json").getFile()), Route[].class);
		when(routeService.getRoutes()).thenReturn(Arrays.asList(routes));
		
		Schedule schedule = mapper.readValue(new File(this.getClass().getResource("/schedules_dub.json").getFile()), Schedule.class);
		when(scheduleService.getSchedule("DUB", "WRO", 2018, 3)).thenReturn(schedule);
		
		Schedule scheduleDub = mapper.readValue(new File(this.getClass().getResource("/schedules_dub.json").getFile()), Schedule.class);
		when(scheduleService.getSchedule("DUB", "STN", 2018, 3)).thenReturn(scheduleDub);
		
		Schedule scheduleStn = mapper.readValue(new File(this.getClass().getResource("/schedules_stn.json").getFile()), Schedule.class);
		when(scheduleService.getSchedule("STN", "WRO", 2018, 3)).thenReturn(scheduleStn);
	}
	
	@Test
	public void testBadRequest() throws Exception {
		mockMvc.perform(get("/api/interconnections"))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testNoFlights() throws Exception {
		mockMvc.perform(get("/api/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", empty()));
	}
	
	@Test
	public void testFlightsWithNoRoute() throws Exception {
		mockMvc.perform(get("/api/interconnections?departure=MLA&arrival=FRA&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", empty()));
	}
	
	@Test
	public void testFlightsDepartureDayOnTime() throws Exception {
		mockMvc.perform(get("/api/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-11T07:00&arrivalDateTime=2018-03-15T23:00"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].stops", is(0)))
			.andExpect(jsonPath("$[0].legs", hasSize(3)))
			.andExpect(jsonPath("$[0].legs[0].departureAirport", is("DUB")))
			.andExpect(jsonPath("$[0].legs[0].arrivalAirport", is("WRO")))
			.andExpect(jsonPath("$[0].legs[1].departureAirport", is("DUB")))
			.andExpect(jsonPath("$[0].legs[1].arrivalAirport", is("WRO")))
			.andExpect(jsonPath("$[0].legs[2].departureAirport", is("DUB")))
			.andExpect(jsonPath("$[0].legs[2].arrivalAirport", is("WRO")))
			
			.andExpect(jsonPath("$[1].stops", is(1)))
			.andExpect(jsonPath("$[1].legs", hasSize(8)))
			.andExpect(jsonPath("$[1].legs[0].departureAirport", is("DUB")))
			.andExpect(jsonPath("$[1].legs[0].arrivalAirport", is("STN")))
			.andExpect(jsonPath("$[1].legs[1].departureAirport", is("STN")))
			.andExpect(jsonPath("$[1].legs[1].arrivalAirport", is("WRO")));
	}
	
	@Test
	public void testFlightsDepartureDayLater() throws Exception {
		mockMvc.perform(get("/api/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-17T07:00&arrivalDateTime=2018-03-19T21:00"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", empty()));
	}
	
	@Test
	public void testFlightsDepartureDayEarlier() throws Exception {
		mockMvc.perform(get("/api/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", empty()));
	}
	
	@Test
	public void testFlightsDepartureHourLater() throws Exception {
		mockMvc.perform(get("/api/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-11T19:30&arrivalDateTime=2018-03-11T22:00"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", empty()));
	}
	
	@Test
	public void testFlightsDepartureHourEarlier() throws Exception {
		mockMvc.perform(get("/api/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-11T07:00&arrivalDateTime=2018-03-11T13:00"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", empty()));
	}
	
	@Test
	public void testOneStopFlights() throws Exception {
		mockMvc.perform(get("/api/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-11T18:00&arrivalDateTime=2018-03-11T23:59"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].stops", is(0)))
			.andExpect(jsonPath("$[0].legs", hasSize(1)))
			.andExpect(jsonPath("$[0].legs[0].departureAirport", is("DUB")))
			.andExpect(jsonPath("$[0].legs[0].arrivalAirport", is("WRO")))
			
			.andExpect(jsonPath("$[1].stops", is(1)))
			.andExpect(jsonPath("$[1].legs", hasSize(2)))
			.andExpect(jsonPath("$[1].legs[0].departureAirport", is("DUB")))
			.andExpect(jsonPath("$[1].legs[0].arrivalAirport", is("STN")))
			.andExpect(jsonPath("$[1].legs[1].departureAirport", is("STN")))
			.andExpect(jsonPath("$[1].legs[1].arrivalAirport", is("WRO")));
	}
}
