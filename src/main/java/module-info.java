// 'mvcapp': Your module name
// 'com.example': Your package name
module mvcapp {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.base;
	requires java.sql;
    opens com.example to javafx.graphics, javafx.fxml;
}
