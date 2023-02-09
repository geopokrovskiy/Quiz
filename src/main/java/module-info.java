module com.geopokrovskiy.program {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.prefs;

    opens com.geopokrovskiy.program to javafx.fxml;
    exports com.geopokrovskiy.program;
    exports com.geopokrovskiy.model to com.fasterxml.jackson.databind;
}