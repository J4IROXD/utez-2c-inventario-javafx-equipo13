package com.example.tiendaproductos.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Esta clase hace que los datos se guarden en un archivo fisico en productos.csv
 */
public class FileRepository {
    private final Path dirPath = Paths.get("data");
    private final Path pathFile = Paths.get("data", "productos.csv");

    /**
     * Esta clase verifica que exista el archivo de los datos.
     * @throws IOException Es por si ocurre un error al crear el archivo o al crear las carpetas.
     */
    private void ensureFile() throws IOException {
        //Sirve para crear la carpeta data si es que no existe.
        if(Files.notExists(dirPath)){
            Files.createDirectories(dirPath);
        }
        //Es para crear un archivo (productos.csv) si es que no existe.
        if (Files.notExists(pathFile)){
            Files.createFile(pathFile);
            Files.writeString(pathFile, "codigo,nombre,precio,stock,categoria\n", StandardCharsets.UTF_8);
        }
    }

    /**
     * Lee todos los datos almacenados en el archivo CSV.
     * @return Es la lista de cadenas que estan en la fila del archivo.
     * @throws IOException es por si hay problemas para acceder el archivo.
     */
    public List<String> readAllLines() throws IOException{
        ensureFile();
        return Files.readAllLines(pathFile, StandardCharsets.UTF_8);
    }

    /**
     * Sobreescribe el archivo actual por los datos actualizados de la lista de productos proporcionados por el usuario.
     * @param lines es la lista de los datos convertidos para estar en el formato csv.
     * @throws IOException Es por si ocurre algun problema al momento de sobreescribir.
     */
    public void saveFile(List<String> lines) throws IOException {
        ensureFile();
        lines.add(0, "codigo,nombre,precio,stock,categoria");
        Files.write(pathFile, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
