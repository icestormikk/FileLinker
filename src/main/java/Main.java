import domain.FileLinker;

public class Main {
    public static void main(String[] args) {
        FileLinker linker = new FileLinker("test_folder");
        linker.link();
    }
}
