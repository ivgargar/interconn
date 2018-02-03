package com.ryanair.test.interconn.service;

import java.util.List;

import com.ryanair.test.interconn.model.Route;

public interface RouteService {
	/**
	 * Returns a list of all available routes based on the airport's IATA codes.
	 * @return a list of routes
	 */
	public List<Route> getRoutes();
}
