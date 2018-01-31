package com.ryanair.test.interconn.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ryanair.test.interconn.model.Route;
import com.ryanair.test.interconn.service.RouteService;

@Service
public class RouteServiceImpl implements RouteService{
	
	private final RestTemplate restTemplate;
    
    public RouteServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

	@Override
	public List<Route> getRoutes() {
		ResponseEntity<Route[]> response = restTemplate.getForEntity("https://api.ryanair.com/core/3/routes", Route[].class);
		return Arrays.asList(response.getBody());
	}

}
