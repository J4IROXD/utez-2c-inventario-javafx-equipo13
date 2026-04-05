package com.example.tiendaproductos.service;

import com.example.tiendaproductos.model.Producto;
import com.example.tiendaproductos.repository.FileRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductoService {
    private FileRepository repo = new FileRepository();

    public List<Producto> loadProductos() throws IOException {
        List<String> lines = repo.readAllLines();
        List<Producto> result= new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null || line.isBlank()) continue;

            String[] parts = line.split(",");
            if (parts.length < 5) continue;
            try {
                result.add(new Producto(
                        parts[0].trim(),
                        parts[1].trim(),
                        Double.parseDouble(parts[2].trim()),
                        Integer.parseInt(parts[3].trim()),
                        parts[5].trim()));
            } catch (NumberFormatException e) {
                System.err.println("Error de formato de línea" + (i + 1) + ":" + line);
            }
        }
        return result;
    }

    public void saveProductos(List<Producto> productos) throws IOException {
        List<String> lines =new ArrayList<>();
        for (Producto p: productos){
            lines.add(p.toString());
        }
        repo.saveFile(lines);
    }
    public void validate(String codigo, String nombre, String precioStr, String stockStr, String categoria, List<Producto> currentList, boolean esEdicion){
        if(codigo==null || codigo.isBlank() || codigo.length()<3 ){
            throw new IllegalArgumentException(" El codigo es incorrecto");
        }
        if(nombre==null || nombre.isBlank() || nombre.length()<3 ){
            throw new IllegalArgumentException(" El nombre del producto es incorrecto");
        }
        if (categoria==null || categoria.isBlank()){ throw  new IllegalArgumentException("No haz seleccionado ninguna categoria");
        }

        if (!esEdicion) {
        boolean existe = currentList.stream()
                .anyMatch(p -> p.getCodigo().equalsIgnoreCase(codigo.trim())); // Paréntesis corregido
        if (existe) {
            throw new IllegalArgumentException("El código ya existe en el inventario.");
        }
    }


        try {
        double precio = Double.parseDouble(precioStr);
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("El precio debe ser un número válido.");
    }

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