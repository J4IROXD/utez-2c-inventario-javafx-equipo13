package com.example.tiendaproductos.service;

import com.example.tiendaproductos.model.Producto;
import com.example.tiendaproductos.repository.FileRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Este servicio que se encarga de la logica y la gestion de los productos.
 * Es el intermedio entre el control y el repositorio.
 */
public class ProductoService {
    private FileRepository repo = new FileRepository();

    /**
     * Se encarga de cargar los productos desde el repositorio y convierte el String a objetos.
     * @return lista de objetos de los productos cargados.
     * @throws IOException Esto es por si ocurre un error al momento de leer el archivo.
     */
    public List<Producto> loadProductos() throws IOException {
        List<String> lines = repo.readAllLines();
        List<Producto> result= new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null || line.isBlank())
                continue;
            String[] parts = line.split(",");
            if (parts.length < 5)
                continue;
            try {
                //Conversión de los datos planos a objeto Producto.
                result.add(new Producto(
                        parts[0].trim(),
                        parts[1].trim(),
                        Double.parseDouble(parts[2].trim()),
                        Integer.parseInt(parts[3].trim()),
                        parts[4].trim()));
            } catch (NumberFormatException e) {
                //Señalización de errores en lineas específicas del archivo.
                System.err.println("Error de formato de línea" + (i + 1) + ":" + line);
            }
        }
        return result;
    }
    /**
     *hace que la lista de producto se convierta en String y le manda solicitud al repositorio para poder guardarlos.
     * @param productos lista de productos a persistir.
     * @throws IOException por si ocurre un error al escribir en el almacenamiento.
     */
    public void saveProductos(List<Producto> productos) throws IOException {
        List<String> lines =new ArrayList<>();
        for (Producto p: productos){
            lines.add(p.toString()); //Este metodo ayuda a que el producto se devuelve al formato csv.
        }
        repo.saveFile(lines);
    }

    /**
     * Valida que los dato ingresados por el usuario cumplan con las reglas de negocio del sistema.
     * @param codigo Indentificador del producto.
     * @param nombre Nombre del dispositivo.
     * @param precioStr Precio pero en formato cadena para que sea valido la conversión.
     * @param stockStr Stack pero en formato cadena para que sea valido la conversión.
     * @param categoria Categoria seleccionada.
     * @param currentList Lista actual para verificar que no haya duplicados.
     * @param esEdicion Define si es una actualización o un nuevo producto.
     * @throws IllegalArgumentException Se encarga de revisar si algun dato es invalido.
     */
    public void validate(String codigo, String nombre, String precioStr, String stockStr, String categoria, List<Producto> currentList, boolean esEdicion){
        //Validar los campos que sean obligatorios.
        if(codigo==null || codigo.isBlank() || codigo.length()<3 ){
            throw new IllegalArgumentException(" El codigo es incorrecto");
        }
        if(nombre==null || nombre.isBlank() || nombre.length()<3 ){
            throw new IllegalArgumentException(" El nombre del producto es incorrecto");
        }
        if (categoria==null || categoria.isBlank()){ throw  new IllegalArgumentException("No haz seleccionado ninguna categoria");
        }
        //Validar que se un codigo unico
        if (!esEdicion) {
        boolean existe = currentList.stream()
                .anyMatch(p -> p.getCodigo().equalsIgnoreCase(codigo.trim())); // Paréntesis corregido
        if (existe) {
            throw new IllegalArgumentException("El código ya existe en el inventario.");
        }
    }

        //Validación numérica de el precio.
        try {
        double precio = Double.parseDouble(precioStr);
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("El precio debe ser un número válido.");
    }
        //Validación númerica del stock.
        try {
        int stock = Integer.parseInt(stockStr);
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("El stock debe ser un número entero.");
    }
}
}