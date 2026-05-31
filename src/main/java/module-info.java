module co.ciencias.finalcc {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jdk.jsobject;
    exports co.ciencias.finalcc.model;
    exports co.ciencias.finalcc.model.enums;
    exports co.ciencias.finalcc.view;
    opens co.ciencias.finalcc.view to javafx.graphics, javafx.fxml, javafx.web;
}