package domain;

import domain.exceptions.CircularRequireException;
import utils.FilesHelper;
import utils.GraphHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * Класс для соединения набора файлов с использованием инструкций require
 * @param directoryPath Путь к директории, в которой лежат файлы, которые необходимо соединить
 */
public record FileLinker(String directoryPath) {
    // Набор пар "файл" - "требуемые для него зависимости"
    private static final HashMap<Path, List<Path>> filesDependencies = new HashMap<>();

    /**
     * Функия для связывания всех файлов в один с учётом инструкции require
     */
    public void link() {
        // Получаем все файлы в директории directoryPath
        try (Stream<Path> pathStream = FilesHelper.getAllFilesInDirectory(directoryPath, "")) {
            // для каждого из них получаем зависимости
            pathStream.forEach((file) -> {
                List<Path> dependencies = getRequiredFiles(file.toFile());
                filesDependencies.put(file, dependencies);
            });

            // сортируем файлы в соответствии с полученными зависимостями
            var sortedFiles = sortFilesByDependencies();
            // выводим полученный список на экран
            sortedFiles.forEach(System.out::println);

            // соединяем содержимое файлов в соответствии с полученными ранее списком
            var concatenatedFiles = concatenateFiles(sortedFiles);
            // строим путь до файла с результатом
            Path resultFilepath = Path.of(directoryPath, "result.txt");
            // записываем результат в файл
            FilesHelper.writeToFile(resultFilepath.toString(), concatenatedFiles);
        } catch (CircularRequireException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получение зависимостей для конкретного файла (на основе инструкции require)
     * @param file Файл, для которого необходимо получить зависимости
     * @return Список путей к файлам, от которых зависит данный файл
     */
    private List<Path> getRequiredFiles(File file) {
        List<Path> fileDependencies = new ArrayList<>();

        FilesHelper.readFileByLine(file.getAbsolutePath(), (line) -> {
            if (!line.matches("require\\s+.+")) {
                return;
            }

                Path requiredFilepath = Path.of(directoryPath, currLine.replaceFirst("require\\s+", ""));
                fileDependencies.add(requiredFilepath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
            Path requiredFilepath = Path.of(directoryPath, line.replaceFirst("require\\s+", ""));
            fileDependencies.add(requiredFilepath);
        });

        return fileDependencies;
    }
            Path requiredFilepath = Path.of(directoryPath, line.replaceFirst("require\\s+", ""));
            fileDependencies.add(requiredFilepath);
        });

        return fileDependencies;
    }
        }

        return builder.toString();
    }
}
