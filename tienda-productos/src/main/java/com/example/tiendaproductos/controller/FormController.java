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

    @FXML
    public void initialize() {
        cbCategoria.setItems(FXCollections.observableArrayList("Alimentos", "Bebidas", "Higiene", "Limpieza", "Otros"));

    }

    public void initData(Producto p, ObservableList<Producto> listaProductos, ProductoService service) {
        this.listaProductos = listaProductos;
        this.service = service;

        if (p != null) {
            this.productoActual = p;
            this.esEdicion = true;

            txtCodigo.setText(p.getCodigo());
            txtCodigo.setEditable(false);
            txtNombre.setText(p.getNombre());
            txtPrecio.setText(String.valueOf(p.getPrecio()));
            txtStock.setText(String.valueOf(p.getStock()));
            cbCategoria.setValue(p.getCategoria());
        }
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        try {
            String codigo = txtCodigo.getText() == null ? "" : txtCodigo.getText().trim();
            String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
            String precioTxt = txtPrecio.getText() == null ? "" : txtPrecio.getText().trim();
            String stockTxt = txtStock.getText() == null ? "" : txtStock.getText().trim();
            String categoria = cbCategoria.getValue();

            service.validate(codigo, nombre, precioTxt, stockTxt, categoria, listaProductos, esEdicion);

            if (esEdicion) {
                productoActual.setNombre(nombre);
                productoActual.setPrecio(Double.parseDouble(precioTxt));
                productoActual.setStock(Integer.parseInt(stockTxt));
                productoActual.setCategoria(categoria);
            } else {
                Producto nuevo = new Producto(codigo, nombre, Double.parseDouble(precioTxt), Integer.parseInt(stockTxt), categoria);
                listaProductos.add(nuevo);
            }
            cerrarVentana();
        } catch (IllegalArgumentException e) {
            lblMsg.setText(e.getMessage());
            lblMsg.setStyle("-fx-text-fill: red");
        }

    }
    @FXML
    public void onCancelar (ActionEvent event){
        cerrarVentana();
    }
    @FXML
    private void cerrarVentana () {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}

