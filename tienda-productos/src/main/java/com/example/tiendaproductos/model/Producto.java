package com.example.tiendaproductos.model;

public class Producto {
    private String codigo;
    private String nombre;
    private double precio;
    private  int stock;
    private String categoria;

    /**
     * Es para iniciar un producto con todos los atributos.
     * @param codigo es el identificador unico del producto.
     * @param nombre es el nombre descriptivo del producto.
     * @param precio es el precio que el usuario le puso al producto.
     * @param stock es la cantidad de productos en la que hay en la en tienda.
     * @param categoria es la clasificación del producto.
     */
    public Producto(String codigo, String nombre, double precio, int stock, String categoria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    /**
     * @return es el identificador del producto
     */
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return es el nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return es el precio del producto
     */
    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * @return es la cantidad de existencia que hay en el producto.
     */
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * @return es la categoria en la que pertenece el producto
     */
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Convierte los datos en una cadena con un formato csv.
     * @return es la cadena formada como: codigo,nombre,precio,stock,categoria
     */
    @Override
    public String toString() {
        return codigo + "," + nombre + "," + precio + "," + stock + "," + categoria;

    }
}
