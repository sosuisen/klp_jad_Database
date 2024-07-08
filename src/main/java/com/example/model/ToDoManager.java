package com.example.model;

import java.time.LocalDate;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class ToDoManager {
	private String dbPath = "jad.db";
	private final DAO dao = new DAO("jdbc:sqlite:" + dbPath);
	
	private ListProperty<ToDo> todos = new SimpleListProperty<>(FXCollections.observableArrayList());

	public ListProperty<ToDo> todosProperty() {
		return todos;
	}

	public void remove(ToDo todo) {
		todos.remove(todo);
		// System.out.println("Removed #" + todo.getId());
		dao.delete(todo.getId());
	}

	private void addListener(ToDo todo) {
		todo.titleProperty().addListener((observable, oldValue, newValue) -> 
		//		System.out.println("Title changed #" + todo.getId() + " : " + newValue));
		dao.updateTitle(todo.getId(), newValue));
		
		todo.dateProperty().addListener((observable, oldValue, newValue) -> 
		//		System.out.println("Date changed #" + todo.getId() + " : " + newValue));
		dao.updateDate(todo.getId(), newValue));
		
		todo.priorityProperty().addListener((observable, oldValue, newValue) ->
		        System.out.println("Priority changed #" + todo.getId() + " : " + newValue));
		// ここは発展課題で変更

		todo.completedProperty().addListener((observable, oldValue, newValue) -> 
		// 		System.out.println("Completed changed #" + todo.getId() + " : " + newValue));
		dao.updateCompleted(todo.getId(), newValue));
	}

	public void create(String title, LocalDate date, int priority, boolean completed) {
		var todo = dao.create(title, date, priority, completed);
		addNewToDo(todo);
	}

	private void addNewToDo(ToDo todo) {
		addListener(todo);
		todos.add(todo);
	}

	public void loadInitialData() {
		// addNewToDo(0, "Design", LocalDate.parse("2022-12-01"), 4, true);
		// addNewToDo(1, "Implementation", LocalDate.parse("2022-12-07"), 3, false);
		dao.getAll().forEach(todo -> addNewToDo(todo));	
	}
}
