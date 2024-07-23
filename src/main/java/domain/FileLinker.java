package domain;

import domain.utils.FilesHelper;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public record FileLinker(String directoryPath) {
    private static final HashMap<Path, List<Path>> filesDependencies = new HashMap<>();

    public void link() {
        try (Stream<Path> pathStream = FilesHelper.getAllFilesInDirectory(directoryPath)) {
            pathStream.forEach((file) -> {
                List<Path> dependencies = getRequiredFiles(file.toFile());
                filesDependencies.put(file, dependencies);
            });

            filesDependencies.forEach((key, value) -> System.out.println(key + " " + value));
        }
    }

    private List<Path> getRequiredFiles(File file) {
        List<Path> fileDependencies = new ArrayList<>();

        try (FileReader fileReader = new FileReader(file); BufferedReader reader = new BufferedReader(fileReader)) {
            String currLine;
            while ((currLine = reader.readLine()) != null) {
                if (!currLine.matches("require\\s+.+")) {
                    continue;
                }

                Path requiredFilepath = Path.of(directoryPath, currLine.replaceFirst("require\\s+", ""));
                fileDependencies.add(requiredFilepath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileDependencies;
    }
}
