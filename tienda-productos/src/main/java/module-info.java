module com.example.tiendaproductos {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.tiendaproductos to javafx.fxml, javafx.graphics;
    exports com.example.tiendaproductos to javafx.graphics, javafx.fxml;

    opens com.example.tiendaproductos.controller to javafx.fxml;
    exports com.example.tiendaproductos.controller;


    opens com.example.tiendaproductos.model to javafx.base;
    exports com.example.tiendaproductos.model;
}