package com.example.tiendaproductos.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileRepository {
    private final Path dirPath = Paths.get("data");
    private final Path pathFile = Paths.get("data", "productos.csv");
    private void ensureFile() throws IOException {

        if(Files.notExists(dirPath)){
            Files.createFile((dirPath));
        }
        if (Files.notExists(pathFile)){
            Files.createFile((pathFile));
            Files.writeString(pathFile, "codigo,nombre,precio,stock,categoria\n", StandardCharsets.UTF_8);
        }
    }
    public List<String> readAllLines() throws IOException{
        ensureFile();
        return Files.readAllLines(pathFile, StandardCharsets.UTF_8);

    }


    public void saveFile(List<String> lines) throws IOException {
        ensureFile();
        lines.add(0, "codigo,nombe,precio,stock,categoria");
        Files.write(pathFile, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

}
