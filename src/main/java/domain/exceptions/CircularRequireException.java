package domain.exceptions;

import java.nio.file.Path;
import java.util.List;

/**
 * Исключение для обозначения ситуаций, в которых возникло циклическое применение инструкции require
 */
public class CircularRequireException extends Throwable {
    public CircularRequireException(List<Path> cycle) {
        super("A cyclic dependence has been discovered: " + cycle);
    }
}
