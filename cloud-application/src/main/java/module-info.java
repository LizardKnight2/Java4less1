module com.example.cloudapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cloudapplication to javafx.fxml;
    exports com.example.cloudapplication;
}