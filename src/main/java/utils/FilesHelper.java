package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Класс для упрощения взаимодействия с файловой системой устройства
 */
public class FilesHelper {
    /**
     * Получение всех файлов из директории <b>directoryPath</b>
     * @param directoryPath Путь к директории, из которой необходимо получить все файлы
     * @return Список всех файлов, находящихся в директории, в виде потока (Stream)
     */
    public static Stream<Path> getAllFilesInDirectory(String directoryPath, String extension) {
        try {
            Path path = Path.of(directoryPath);
            return Files.walk(path).filter(Files::isRegularFile);
        } catch (IOException e) {
            System.err.println("An unexpected error occurred while crawling files: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Построчное чтение данных из файла, расположенного по пути <b>sourceFilepath</b>
     * @param sourceFilepath Путь к файлу, из которого необходимо прочитать данные
     * @param onLineTransform Функция, которая применяется к каждой считанной строке
     */
    public static void readFileByLine(String sourceFilepath, Consumer<String> onLineTransform) {
        try (FileReader fileReader = new FileReader(sourceFilepath); BufferedReader reader = new BufferedReader(fileReader)) {
            String currLine;
            while ((currLine = reader.readLine()) != null) {
                onLineTransform.accept(currLine);
            }
        } catch (FileNotFoundException e) {
            System.err.println("The specified file could not be found: " + e.getMessage());
        } catch (InvalidPathException e) {
            System.err.println("The path could not be processed correctly (" + e.getMessage() + ")");
        } catch (IOException e) {
            System.err.println("An unexpected error occurred while reading the file: " + e.getMessage());
        }
    }

    /**
     * Функция для записи данных в файл, расположенный по пути <b>targetFilepath</b>
     * @param targetFilepath Путь к файлу, в который необходимо записать данные
     * @param content Данные для записи в файл
     */
    public static void writeToFile(String targetFilepath, String content) {
        try (OutputStream stream = new FileOutputStream(targetFilepath); OutputStreamWriter writer = new OutputStreamWriter(stream)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            System.err.println("An unexpected error occurred while writing to the file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
