import domain.FileLinker;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Specify the path to the directory where you want to merge the files");
        }

        String directoryPath = args[0];
        FileLinker linker = new FileLinker(directoryPath);
        linker.link();
    }
}
