package com.ryanair.test.interconn.model;

import java.util.List;

public class Schedule {
	private int month;
	private List<Day> days;
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public List<Day> getDays() {
		return days;
	}
	public void setDays(List<Day> days) {
		this.days = days;
	}
}
