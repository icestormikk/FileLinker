package utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Класс для осуществления операции, связанных с Графами
 */
public class GraphHelper {
    /**
     * Функция для поиска циклов в графе. Использует метод поиска в глубину (<b>Depth-first search</b>), чтобы обойти все
     * вершины графа и проверить наличие циклов в нём.
     * В нашей задаче этот метод используется для нахождения циклических импортов (инструкций require) в файлах.
     * @param node Текущая вершина, в которой работает фукнкция (текущий файл)
     * @param dependencies Вершины, доступные из данной вершины (файлы, которые требуются для импорта в текущем файле node)
     * @param visited Посещенные вершины (файлы, в которых алгоритм уже был)
     * @param stack Список пройденных вершин. Может уменьшаться (постоянно изменяется) во время работы алгоритма
     * @param cycle Вершины, которые предположительно составляют цикл (файлы, которые образуют циклический импорт)
     * @return true, если в графе был найден цикл, и false - иначе
     * @param <T> Тип вершины
     */
    public static <T> boolean dfs(T node, Map<T, List<T>> dependencies, Set<T> visited, Set<T> stack, List<T> cycle) {
        // если вершина присутствует в стеке пройденных вершин
        if (stack.contains(node)) {
            // то добавляем её в список вершин, образующих цикл, и возвращаем true
            cycle.add(node);
            return true;
        }

        // обрабатывать одну и ту же вершину не имеет смысла
        if (visited.contains(node)) return false;

        // добавляем вершину в стек и отмечаем её как "посещенную"
        stack.add(node);
        visited.add(node);
        // для каждой вершины-соседа
        for (T neighbor : dependencies.getOrDefault(node, Collections.emptyList())) {
            // вызываем рекурсивно нашу функцию и если текущая вершина является начальной точкой цикла, завершаем цикл
            if (dfs(neighbor, dependencies, visited, stack, cycle)) {
                if (!cycle.isEmpty() && cycle.getLast().equals(node)) {
                    return false;
                }
                // иначе добавляем вершину в список цикла
                cycle.add(node);
                // и говорим о том, что нашли цикл
                return true;
            }
        }
        // После обработки всех соседних вершин, удаляем текущую вершину из стека
        stack.remove(node);

        // цикл не был найден
        return false;
    }
}
