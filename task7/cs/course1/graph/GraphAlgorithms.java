package ru.vsu.cs.course1.graph;

import java.util.*;
import java.util.function.Consumer;

public class GraphAlgorithms {

    /**
     * Поиск в глубину, реализованный рекурсивно
     * (начальная вершина также включена)
     * @param graph граф
     * @param from Вершина, с которой начинается поиск
     * @param visitor Посетитель
     */
    public static void dfsRecursion(Graph graph, int from, Consumer<Integer> visitor) {
        boolean[] visited = new boolean[graph.vertexCount()];

        class Inner {
            void visit(Integer curr) {
                visitor.accept(curr);
                visited[curr] = true;
                for (Integer v : graph.adges(curr)) {
                    if (!visited[v]) {
                        visit(v);
                    }
                }
            }
        }
        new Inner().visit(from);
    }

    /**
     * Поиск в глубину, реализованный с помощью стека
     * (не совсем "правильный"/классический, т.к. "в глубину" реализуется только "план" обхода, а не сам обход)
     * @param graph граф
     * @param from Вершина, с которой начинается поиск
     * @param visitor Посетитель
     */
    public static void dfs(Graph graph, int from, Consumer<Integer> visitor) {
        boolean[] visited = new boolean[graph.vertexCount()];
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(from);
        visited[from] = true;
        while (!stack.empty()) {
            Integer curr = stack.pop();
            visitor.accept(curr);
            for (Integer v : graph.adges(curr)) {
                if (!visited[v]) {
                    stack.push(v);
                    visited[v] = true;
                }
            }
        }
    }

    /**
     * Поиск в ширину, реализованный с помощью очереди
     * (начальная вершина также включена)
     * @param graph граф
     * @param from Вершина, с которой начинается поиск
     * @param visitor Посетитель
     */
    public static void bfs(Graph graph, int from, Consumer<Integer> visitor) {
        boolean[] visited = new boolean[graph.vertexCount()];
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(from);
        visited[from] = true;
        while (queue.size() > 0) {
            Integer curr = queue.remove();
            visitor.accept(curr);
            for (Integer v : graph.adges(curr)) {
                if (!visited[v]) {
                    queue.add(v);
                    visited[v] = true;
                }
            }
        }
    }

    /**
     * Поиск в глубину в виде итератора
     * (начальная вершина также включена)
     * @param graph граф
     * @param from Вершина, с которой начинается поиск
     * @return Итератор
     */
    public static Iterable<Integer> dfs(Graph graph, int from) {
        return new Iterable<Integer>() {
            private Stack<Integer> stack = null;
            private boolean[] visited = null;

            @Override
            public Iterator<Integer> iterator() {
                stack = new Stack<>();
                stack.push(from);
                visited = new boolean[graph.vertexCount()];
                visited[from] = true;

                return new Iterator<Integer>() {
                    @Override
                    public boolean hasNext() {
                        return ! stack.isEmpty();
                    }

                    @Override
                    public Integer next() {
                        Integer result = stack.pop();
                        for (Integer adj : graph.adges(result)) {
                            if (!visited[adj]) {
                                visited[adj] = true;
                                stack.add(adj);
                            }
                        }
                        return result;
                    }
                };
            }
        };
    }

    /**
     * Поиск в ширину в виде итератора
     * (начальная вершина также включена)
     * @param from Вершина, с которой начинается поиск
     * @return Итератор
     */
    public static Iterable<Integer> bfs(Graph graph, int from) {
        return new Iterable<Integer>() {
            private Queue<Integer> queue = null;
            private boolean[] visited = null;

            @Override
            public Iterator<Integer> iterator() {
                queue = new LinkedList<>();
                queue.add(from);
                visited = new boolean[graph.vertexCount()];
                visited[from] = true;

                return new Iterator<Integer>() {
                    @Override
                    public boolean hasNext() {
                        return ! queue.isEmpty();
                    }

                    @Override
                    public Integer next() {
                        Integer result = queue.remove();
                        for (Integer adj : graph.adges(result)) {
                            if (!visited[adj]) {
                                visited[adj] = true;
                                queue.add(adj);
                            }
                        }
                        return result;
                    }
                };
            }
        };
    }


    /**
     * Алгоритм Дейкстры
     * (простейшая реализация без приоритетной очереди за n^2)
     */
    public static class MinDistanceSearchResult {
        public double d[];
        public int from[];
    }

    public static MinDistanceSearchResult dijkstra(WeightedGraph graph, int source, int target) {
        int n = graph.vertexCount();

        double[] d = new double[n];
        int[] from = new int[n];
        boolean[] found = new boolean[n];

        Arrays.fill(d, Double.MAX_VALUE);
        d[source] = 0;
        int prev = -1;
        for (int i = 0; i < n; i++) {
            // ищем "ненайденную" вершину с минимальным d[i]
            // (в общем случае обращение к приоритетной очереди)
            int curr = -1;
            for (int j = 0; j < n; j++) {
                if (!found[j] && (curr < 0 || d[j] < d[curr])) {
                    curr = j;
                }
            }

            found[curr] = true;
            from[curr] = prev;
            if (curr == target) {
                break;
            }
            for (WeightedGraph.WeightedEdgeTo v : graph.adgesWithWeights(curr)) {
                if (d[curr] + v.weight() < d[v.to()]) {
                    d[v.to()] = d[curr] + v.weight();
                    // в общем случае надо было изменить в приоритетной очереди приоритет для v.to()
                }
            }
        }

        // возвращение результата
        MinDistanceSearchResult result = new MinDistanceSearchResult();
        result.d = d;
        result.from = from;
        return result;
    }

    /**
     * Алгоритм Белмана-Форда
     * O(n*m)
     */
    public static MinDistanceSearchResult belmanFord(WeightedGraph graph, int source) {
        int n = graph.vertexCount();

        double[] d = new double[n];
        int[] from = new int[n];

        Arrays.fill(d, Double.MAX_VALUE);
        d[source] = 0;
        for (int i = 0; i < n - 1; i++) {
            boolean changed = false;
            // обход всех связей (в данной реализации - цикл в цикле)
            for (int j = 0; j < n; j++) {
                for (WeightedGraph.WeightedEdgeTo v : graph.adgesWithWeights(j)) {
                    if (d[v.to()] > d[j] + v.weight()) {
                        d[v.to()] = d[j] + v.weight();
                        from[v.to()] = j;
                        changed = true;
                    }
                }
            }
            if (!changed) {
                break;
            }
        }

        // возвращение результата
        MinDistanceSearchResult result = new MinDistanceSearchResult();
        result.d = d;
        result.from = from;
        return result;
    }
    /**
     * Раскраска графа минимальным количеством цветов (хроматическое число)
     * @param graph граф
     * @return массив цветов для каждой вершины (цвета - целые числа от 0)
     */
    public static int[] graphColoring(Graph graph) {
        int n = graph.vertexCount();
        if (n == 0) return new int[0];

        List<Integer> vertices = new ArrayList<>();
        for (int i = 0; i < n; i++) vertices.add(i);
        vertices.sort((a, b) -> Integer.compare(degree(graph, b), degree(graph, a)));

        int[] posOfVertex = new int[n];
        for (int i = 0; i < n; i++) posOfVertex[vertices.get(i)] = i;

        int[] colors = new int[n];
        Arrays.fill(colors, -1);

        int maxColors = 1;
        while (!tryColor(graph, vertices, posOfVertex, colors, 0, maxColors)) {
            maxColors++;
            Arrays.fill(colors, -1);
        }


        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[vertices.get(i)] = colors[i];
        }
        return result;
    }

    /**
     * Возвращает хроматическое число графа
     */
    public static int chromaticNumber(Graph graph) {
        int[] colors = graphColoring(graph);
        if (colors.length == 0) return 0;
        int max = 0;
        for (int c : colors) if (c > max) max = c;
        return max + 1;
    }

    private static int degree(Graph graph, int v) {
        int cnt = 0;
        for (Integer adj : graph.adges(v)) cnt++;
        return cnt;
    }

    private static boolean tryColor(Graph graph, List<Integer> vertices, int[] posOfVertex,
                                    int[] colors, int pos, int maxColors) {
        if (pos == vertices.size()) return true;

        int v = vertices.get(pos);
        for (int c = 0; c < maxColors; c++) {
            if (isSafe(graph, posOfVertex, colors, v, c)) {
                colors[pos] = c;
                if (tryColor(graph, vertices, posOfVertex, colors, pos + 1, maxColors)) {
                    return true;
                }
                colors[pos] = -1;
            }
        }
        return false;
    }

    private static boolean isSafe(Graph graph, int[] posOfVertex, int[] colors, int v, int c) {

        for (Integer adj : graph.adges(v)) {
            int adjPos = posOfVertex[adj];
            if (colors[adjPos] == c) return false;
        }

        if (graph instanceof Digraph) {
            for (int i = 0; i < colors.length; i++) {
                if (colors[i] != -1) {
                    int vertex = -1;
                    for (int vv = 0; vv < posOfVertex.length; vv++) {
                        if (posOfVertex[vv] == i) { vertex = vv; break; }
                    }
                    if (vertex != -1 && graph.isAdj(vertex, v) && colors[i] == c) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
