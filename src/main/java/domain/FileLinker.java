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
        }

        return builder.toString();
    }
}
