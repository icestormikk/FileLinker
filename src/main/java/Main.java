import domain.FileLinker;

public class Main {
    public static void main(String[] args) {
        FileLinker linker = new FileLinker("test_folder");
        if (args.length < 1) {
            throw new IllegalArgumentException("Укажите путь к директории, в которой необходимо объединить файлы");
        }
        linker.link();
    }
}
