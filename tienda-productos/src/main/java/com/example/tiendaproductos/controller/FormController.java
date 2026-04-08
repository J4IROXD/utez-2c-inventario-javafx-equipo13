package com.example.tiendaproductos.controller;

import com.example.tiendaproductos.model.Producto;
import com.example.tiendaproductos.service.ProductoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador para la gestión de productos.
 * La clase se encarga de capturar los datos que registre el usuario
 * al igual que actualizarlos mientras se aplica la validación de datos.
 */
public class FormController {

    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;
    @FXML
    private ComboBox<String> cbCategoria;
    @FXML
    private Label lblMsg;

    private ProductoService service;
    private Producto productoActual;
    private ObservableList<Producto> listaProductos;
    private boolean esEdicion = false;

    /**
     * Se inicia las opciones y las opciones predefinidas
     * en las categorias.
     */
    @FXML
    public void initialize() {
        cbCategoria.setItems(FXCollections.observableArrayList("Alimentos", "Bebidas", "Higiene", "Limpieza", "Otros"));

    }

    /**
     * recibe como configura los datos para que estos funcionen ara que cuando reciba
     * el dato este pase al modo de edición.
     * @param p productos editables.
     * @param listaProductos se encarga de enseñar los datos que estan registrados en la tabla principal.
     * @param service es el servicio que se encarga de validar.
     */
    public void initData(Producto p, ObservableList<Producto> listaProductos, ProductoService service) {
        this.listaProductos = listaProductos;
        this.service = service;

        if (p != null) {
            this.productoActual = p;
            this.esEdicion = true;

            txtCodigo.setText(p.getCodigo());
            txtCodigo.setEditable(false); //Se encarga de que el codigo no se pueda editar
            txtNombre.setText(p.getNombre());
            txtPrecio.setText(String.valueOf(p.getPrecio()));
            txtStock.setText(String.valueOf(p.getStock()));
            cbCategoria.setValue(p.getCategoria());
        }
    }

    /**
     * Se encarga de gestionar el guardado también valida los datos que se hayan registrado
     * como tambien actualiza, o crea un nuevo dato.
     * @param event es el evento que se encarga del click en guardar.
     */
    @FXML
    private void onGuardar(ActionEvent event) {
        try {
            //Captura y limpia los datos registrados.
            String codigo = txtCodigo.getText() == null ? "" : txtCodigo.getText().trim();
            String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
            String precioTxt = txtPrecio.getText() == null ? "" : txtPrecio.getText().trim();
            String stockTxt = txtStock.getText() == null ? "" : txtStock.getText().trim();
            String categoria = cbCategoria.getValue();
            //Se separan las responsabilidades.
            service.validate(codigo, nombre, precioTxt, stockTxt, categoria, listaProductos, esEdicion);

            if (esEdicion) {
                //Actualiza los datos.
                productoActual.setNombre(nombre);
                productoActual.setPrecio(Double.parseDouble(precioTxt));
                productoActual.setStock(Integer.parseInt(stockTxt));
                productoActual.setCategoria(categoria);
            } else {
                //Se crea una nueva instancia.
                Producto nuevo = new Producto(codigo, nombre, Double.parseDouble(precioTxt), Integer.parseInt(stockTxt), categoria);
                listaProductos.add(nuevo);
            }
            mostrarAlerta(Alert.AlertType.INFORMATION, "Exito", esEdicion ? "El producto se actualizó correctamente." : "El producto se agregó con éxito a la lista.");
            cerrarVentana();
        } catch (IllegalArgumentException e) {
            //Manejo de errores.
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
            lblMsg.setText(e.getMessage());
            lblMsg.setStyle("-fx-text-fill: red");
        }

    }

    /**
     * Cierra la ventana sin realizar cambios.
     */
    @FXML
    public void onCancelar (ActionEvent event){
        cerrarVentana();
    }

    /**
     * Metodo que auxilia el Stage actual y cerrarlo.
     */
    @FXML
    private void cerrarVentana () {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    /**
     * Es para mostrar una ventana temporal para informar al usario.
     * @param tipo se usa para saber si es una ventana de error o de información.
     * @param titulo se hace para mostrarle que es lo que paso al usuario.
     * @param mensaje se encarga de mostrar a detalle lo que paso.
     */
    @FXML
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje){
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

