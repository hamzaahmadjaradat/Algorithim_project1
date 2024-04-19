module com.example.algorithim_project1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.algorithim_project1 to javafx.fxml;
    exports com.example.algorithim_project1;
}