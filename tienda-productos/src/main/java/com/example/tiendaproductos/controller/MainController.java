package com.example.tiendaproductos.controller;

import com.example.tiendaproductos.model.Producto;
import com.example.tiendaproductos.service.ProductoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML
    private TextField txtBusqueda;
    @FXML
    private TableView<Producto> tablaProductos;
    @FXML
    private TableColumn<Producto, String> colCodigo;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, Double> colPrecio;
    @FXML
    private TableColumn<Producto, Integer> colStock;
    @FXML
    private TableColumn<Producto, String> colCategoria;
    @FXML
    private Label lblMsg;

    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private ProductoService service = new ProductoService();


    // Métodos conectados con onAction en MainView.fxml
    @FXML
    public void initialize() {
        //Aquí se configuraron todas las columnas
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        cargarDatos();
        configurarBusquedaYOrdenamiento();
    }

    private void cargarDatos() {
        try {
            listaProductos.setAll(service.loadProductos());

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al leer el archivo de productos ");
        }
    }
    private void configurarBusquedaYOrdenamiento(){
        FilteredList<Producto> filteredData =new FilteredList<>(listaProductos, b -> true);

        txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(producto -> {
                if (newValue ==  null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();

                if (producto.getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                if (producto.getCodigo().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });
        SortedList<Producto> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaProductos.comparatorProperty());
        tablaProductos.setItems(sortedData);
    }
    @FXML
    public void onNuevo(ActionEvent event){
        abrirFormulario(null);
    }

    @FXML
    public void onEditar(ActionEvent event){
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null){
            abrirFormulario(seleccionado);
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un producyto para editar ");

        }

    }

    @FXML
    public void onEliminar(ActionEvent event){
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Está seguro de eliminar el producto?" + seleccionado.getNombre() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()&& result.get() == ButtonType.OK){
                listaProductos.remove(seleccionado);
                onGuardar(null);
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un producto para eliminar");
        }
    }
    @FXML
    public void onRecargar(ActionEvent event) {
        cargarDatos();
    }
    @FXML
    public void onGuardar(ActionEvent event) {
        try {
            service.saveProductos(listaProductos);
            if(event != null) mostrarAlerta(Alert.AlertType.INFORMATION, "Datos guardados correctamente.");
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar en el archivo.");
        }
    }
    private void abrirFormulario(Producto producto){
        try {
            FXMLLoader loader= new FXMLLoader(getClass().getResource("/com/example/tiendaproductos/Form-View.fxml"));
            Parent root = loader.load();

            FormController controller = loader.getController();
            controller.initData(producto, listaProductos, service);
            Stage stage = new Stage();
            stage.setTitle(producto == null ? "Nuevo producto" : "Editar producto");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            onGuardar(null);
            tablaProductos.refresh();
        } catch (IOException e){
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al abrir el formulario");
        }
    }
    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

