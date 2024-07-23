package domain.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FilesHelper {
    public static Stream<Path> getAllFilesInDirectory(String directoryPath) {
        try {
            Path path = Path.of(directoryPath);
            return Files.walk(path).filter(Files::isRegularFile);
        } catch (IOException e) {
            System.err.println("Возникла ошибка во время обхода файлов: " + e.getLocalizedMessage());
            return Stream.empty();
        }
    }
}
