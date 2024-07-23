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

            Path requiredFilepath = Path.of(directoryPath, line.replaceFirst("require\\s+", ""));
            fileDependencies.add(requiredFilepath);
        });

        return fileDependencies;
    }

    /**
     * Сортировка файлов в зависимости от имеющихся у них зависимостей
     * @return Список файлов, отсортированных по возрастанию кол-ва зависимостей
     * @throws CircularRequireException Если была обнаружена циклическая зависимость
     */
    private List<Path> sortFilesByDependencies() throws CircularRequireException {
        // результирующий список
        List<Path> result = new ArrayList<>();

        // набор пар "файл" - "степень входа файла"
        Map<Path, Integer> degrees = new HashMap<>();
        for (var file: filesDependencies.keySet()) {
            // инициализируем степень входа для каждого файла нулём
            degrees.put(file, 0);
        }

        // Проходимся по зависимостям файла и для каждой из них увеличиваем степень входа
        for (var dependencies: filesDependencies.values()) {
            for (Path dependency: dependencies) {
                degrees.put(dependency, degrees.get(dependency) + 1);
            }
        }

        // создаём очередь для хранения узлов с нулёвой степенью входа
        Queue<Path> queue = new LinkedList<>();
        // набор уже посещённых вершин
        Set<Path> visitedPaths = new HashSet<>();
        // добавляем в очередб узлы с нулёвой степенью входа
        for (var entry: degrees.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        // пока очередь не опустеет
        while (!queue.isEmpty()) {
            // получаем узел со степенью входа 0
            Path currentPath = queue.poll();
            // добавляем его в результирующий список
            result.addFirst(currentPath);
            // и в список посещённых вершин
            visitedPaths.add(currentPath);
            // для каждого из файлов, от которых зависит текущий файл
            for (var dependency: filesDependencies.get(currentPath)) {
                // уменьшаем степень вход на 1
                degrees.put(dependency, degrees.get(dependency) - 1);
                // и как только она будет равна 0, добавляем в очередь
                if (degrees.get(dependency) == 0) {
                    queue.add(dependency);
                }
            }
        }

        // если количество отсортированных файлов меньше количества всех файлов, значит, есть цикл
        if (result.size() != filesDependencies.size()) {
            // находим этот цикл
            List<Path> cycle = findCycle(visitedPaths);
            throw new CircularRequireException(cycle);
        }

        return result;
    }

    /**
     * Поиск цикла в "графе зависимостей" файлов
     * @param visitedPaths Список посещённых файлов
     * @return Список файлов, инструкции require которых создают циклический импорт
     */
    private List<Path> findCycle(Set<Path> visitedPaths) {
        Set<Path> visitedStack = new HashSet<>();
        List<Path> result = new ArrayList<>();

        for (var file : filesDependencies.keySet()) {
            if (visitedPaths.contains(file)) {
                continue;
            }

            if (GraphHelper.dfs(file, filesDependencies, visitedPaths, visitedStack, result)) {
                return result.reversed();
            }
        }

        return Collections.emptyList();
    }

    /**
     * Функция для конкатенации списка файлов
     * @param files Файлы, содержимое которых надо соединить
     * @return Строка, представляющее собой объединение содержимого всех переданных файлов
     */
    private String concatenateFiles(List<Path> files) {
        StringBuilder builder = new StringBuilder();
        for (Path file: files) {
            try {
                builder.append(
                    Files.readString(file, StandardCharsets.UTF_8)
                ).append("\n");
            } catch (IOException e) {
                System.err.println("Возникла непредвиденная ошибка во время объединения файлов: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return builder.toString();
    }
}
