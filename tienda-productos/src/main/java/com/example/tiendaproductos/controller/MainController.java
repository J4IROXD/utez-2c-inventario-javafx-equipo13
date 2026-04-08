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

/**
 * Es el controlador principal del programa.
 * Se encarga de gestionar la visualización de los datos de la tabla en tiempo real.
 */
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
    //Lista princial en la que contiene los datos en la memoria
    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private ProductoService service = new ProductoService();


    /**
     * Métodos conectados con onAction en MainView.fxml
      */

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

    /**
     * Recupera los datos del archivo persistente.
     */
    private void cargarDatos() {
        try {
            listaProductos.setAll(service.loadProductos());

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR,"Error", "Error al leer el archivo de productos.");
        }
    }

    /**
     * Se implementa el filtrado y que la tabla pueda ser ordenada
     * sin perder el filtro que el usuario aplica.
     */
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

    /**
     * Llama la formula para implementar un nuevo producto.
     */
    @FXML
    public void onNuevo(ActionEvent event){
        abrirFormulario(null);
    }

    /**
     * Llama la formula para poder editar un producto ya existe.
     */
    @FXML
    public void onEditar(ActionEvent event){
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null){
            abrirFormulario(seleccionado);
        } else {
            mostrarAlerta(Alert.AlertType.WARNING,"Atención", "Seleccione un producto para editar.");

        }

    }

    /**
     * Se hace la eliminación de un producto pero antes confirma al usuario.
     * Se guarda en automatico los cambios.
     */
    @FXML
    public void onEliminar(ActionEvent event){
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Está seguro de eliminar el producto " + seleccionado.getNombre() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()&& result.get() == ButtonType.OK){
                listaProductos.remove(seleccionado);
                onGuardar(null); //Sincroniza en automatico con el archivo
                mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminado", "El producto se borró correctamente.");
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Error", "Seleccione un producto para eliminar.");
        }
    }

    /**
     * Se obliga en la recarga de los datos del archivo.
     */
    @FXML
    public void onRecargar(ActionEvent event) {
        cargarDatos();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Actualizado", "Se actualizó correctamente");
    }

    /**
     * Hace los cambios actuales en la lista csv.
     */
    @FXML
    public void onGuardar(ActionEvent event) {
        try {
            service.saveProductos(listaProductos);
            if(event != null)
                mostrarAlerta(Alert.AlertType.INFORMATION,"Guardado exitoso", "Datos guardados correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error","Error al guardar en el archivo.");
        }
    }

    /**
     * Abre y muestra una ventana secundaria en el formulario.
     * @param producto Producto a editar o se hace null para un dato nuevo.
     */
    private void abrirFormulario(Producto producto){
        try {
            FXMLLoader loader= new FXMLLoader(getClass().getResource("/com/example/tiendaproductos/Form-View.fxml"));
            Parent root = loader.load();
            //Se configura la segunda ventana.
            FormController controller = loader.getController();
            controller.initData(producto, listaProductos, service);
            Stage stage = new Stage();
            stage.setTitle(producto == null ? "Nuevo producto" : "Editar producto");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            //Lo que hace es cerrar, guardar y actualizar los datos.
            onGuardar(null);
            tablaProductos.refresh();
        } catch (IOException e){
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR,"Error", "Error al abrir el formulario");
        }
    }

    /**
     * Este metodo lo que hace es crear mensajes la alerta.
     * @param tipo se encarga se enseñar el tipo de la categoria de la alerta.
     * @param titulo es lo que se muestra al usuario lo que paso.
     * @param mensaje enseña más a detalle lo que pasó.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

