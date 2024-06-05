package com.example;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import com.example.model.ToDo;
import com.example.model.ToDoManager;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainController {
	private final String TODO_ID_PREFIX = "todo-";

	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	private MenuItem menuItemClose;
	
	@FXML
	private Button addBtn;

	@FXML
	private DatePicker headerDatePicker;

	@FXML
	private TextField headerTitleField;

	@FXML
	private VBox todoListVBox;

	@FXML
	private ChoiceBox<Integer> headerPriorityChoiceBox;

	private ToDoManager model;

	private void showInfo(String txt) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("アプリの情報");
		alert.setHeaderText(null);
		alert.setContentText(txt);
		alert.showAndWait();
	}
		
	private HBox createToDoHBox(ToDo todo) {
		// Create View Items
		var completedCheckBox = new CheckBox();
		completedCheckBox.setSelected(todo.isCompleted());
		completedCheckBox.getStyleClass().add("todo-completed");

		var titleField = new TextField(todo.getTitle());
		titleField.getStyleClass().add("todo-title");
		HBox.setHgrow(titleField, Priority.ALWAYS);

		var datePicker = new DatePicker(todo.getDate());
		datePicker.getStyleClass().add("todo-date");
		datePicker.setPrefWidth(105);
		HBox.setHgrow(datePicker, Priority.NEVER);

		var priorityChoiceBox = new ChoiceBox<Integer>();
		priorityChoiceBox.getItems().addAll(1, 2, 3, 4, 5);
		priorityChoiceBox.setValue(todo.getPriority());
		HBox.setHgrow(priorityChoiceBox, Priority.NEVER);

		var deleteBtn = new Button("削除");
		deleteBtn.getStyleClass().add("todo-delete");

		var todoItem = new HBox(completedCheckBox, titleField, datePicker, priorityChoiceBox, deleteBtn);
		todoItem.getStyleClass().add("todo-item");

		todoItem.setId(TODO_ID_PREFIX + todo.getId());

		// Bind Model to View
		completedCheckBox.selectedProperty().bindBidirectional(todo.completedProperty());
		titleField.textProperty().bindBidirectional(todo.titleProperty());
		datePicker.valueProperty().bindBidirectional(todo.dateProperty());
		priorityChoiceBox.valueProperty().bindBidirectional(todo.priorityProperty());

		// Event Handler for sorting
		ChangeListener<Object> listener = (observable, newValue, oldValue) -> sortByCompletedAndDate();
		completedCheckBox.selectedProperty().addListener(listener);
		datePicker.valueProperty().addListener(listener);

		// Event Handler
		deleteBtn.setOnAction(e -> model.remove(todo));

		return todoItem;
	}

	public void initModel(ToDoManager manager) {
		if (this.model != null)
			throw new IllegalStateException("Model can only be initialized once");

		model = manager;

		ObservableList<Node> todoListItems = todoListVBox.getChildren();

		// Event Handler
		addBtn.setOnAction(e -> {
			model.create(headerTitleField.getText(), headerDatePicker.getValue(), headerPriorityChoiceBox.getValue(), false);
			headerTitleField.clear();
		});

		// Observe Model to update View
		model.todosProperty().addListener((ListChangeListener<ToDo>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					change.getAddedSubList().forEach(todo -> todoListItems.add(createToDoHBox(todo)));
				}
				if (change.wasRemoved()) {
					List<String> ids = change.getRemoved().stream().map(todo -> TODO_ID_PREFIX + todo.getId()).toList();
					todoListItems.removeIf(node -> ids.contains(node.getId()));
				}
			}
			sortByCompletedAndDate();
		});

		model.loadInitialData();
		sortByCompletedAndDate();
	}

	private void sortByCompletedAndDate() {
		FXCollections.sort(todoListVBox.getChildren(), 
				Comparator.comparing(node -> ((CheckBox)((HBox)node).getChildren().get(0)).isSelected())
					.thenComparing(node -> ((DatePicker)((HBox)node).getChildren().get(2)).getValue()));
	}

	public void initialize() {
		// Set today
		headerDatePicker.setValue(LocalDate.now());
		
	    headerPriorityChoiceBox.getItems().addAll(1, 2, 3, 4, 5);
	    headerPriorityChoiceBox.setValue(3);

		menuItemAbout.setOnAction(e -> showInfo("ToDo App"));		
		menuItemClose.setOnAction(e -> Platform.exit());
	}
}