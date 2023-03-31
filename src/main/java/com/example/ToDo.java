package com.example;

import java.time.LocalDate;

public class ToDo {
	private int id;
	private String title;
	/**
	 * Local date string
	 * e.g. 2022-12-01
	 */
	private String date;
	private int priority;
	private boolean completed;
	
	public ToDo(int id, String title, String date, int priority, boolean completed) {
		this.id = id;
		this.title = title;
		this.date = date;
		this.priority = priority;
		this.completed = completed;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return date string e.g. 2022-12-01
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date date string e.g. 2022-12-01
	 */
	public void setDate(String date) {
		this.date = date;
	}
	public LocalDate getLocalDate() {
		return LocalDate.parse(date);
	}
	public void setLocalDate(LocalDate localDate) {
		this.date = localDate.toString();
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
}
