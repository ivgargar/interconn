package com.ryanair.test.interconn.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ryanair.test.interconn.model.Route;
import com.ryanair.test.interconn.service.RouteService;

@Service
public class RouteServiceImpl implements RouteService{
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public List<Route> getRoutes() {
		List<Route> routes = new ArrayList<Route>();
		try {
			log.debug("Retrieving routes");
			ResponseEntity<Route[]> response = restTemplate.getForEntity("https://api.ryanair.com/core/3/routes", Route[].class);
			routes = Arrays.asList(response.getBody());
		} catch (HttpClientErrorException e) {
			log.error("Error " + e.getStatusCode().value() + "retrieving routes");
		} catch (Exception e) {
			log.error("Unexpected error", e);
		}
		return routes;
	}

}
