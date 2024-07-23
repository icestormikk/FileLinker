import domain.FileLinker;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Укажите путь к директории, в которой необходимо объединить файлы");
        }

        String directoryPath = args[0];
        FileLinker linker = new FileLinker(directoryPath);
        linker.link();
    }
}
