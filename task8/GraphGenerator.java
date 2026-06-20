package ru.vsu.cs.course1.graph.demo;

import ru.vsu.cs.course1.graph.AdjMatrixDigraph;
import ru.vsu.cs.course1.graph.Graph;

import java.util.*;

/**
 * Генератор всех неизоморфных ориентированных графов без петель,
 * у каждой вершины минимум одна входящая и одна исходящая дуга.
 * Гарантирует правильные количества для N = 2..6.
 */
public class GraphGenerator {

    private static final int MAX_DISPLAY_GRAPHS = 10;

    public static List<Graph> generate(int n) {
        System.out.println("Генерация для N = " + n);
        Set<String> canonical = new HashSet<>();
        int totalMatrices = (int) Math.pow(2, n * (n - 1));
        System.out.println("Всего матриц: " + totalMatrices);

        long startTime = System.currentTimeMillis();
        int progressStep = Math.max(1, totalMatrices / 100);  // для вывода прогресса

        for (int mask = 0; mask < totalMatrices; mask++) {
            // Строим матрицу смежности
            int[][] matrix = new int[n][n];
            int bitPos = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        matrix[i][j] = (mask >> bitPos) & 1;
                        bitPos++;
                    }
                }
            }

            // Проверяем условие: у каждой вершины >= 1 входящей и >= 1 исходящей
            if (hasValidDegrees(matrix, n)) {
                // Приводим к каноническому виду
                String canon = canonicalForm(matrix, n);
                canonical.add(canon);
            }


            if (mask % progressStep == 0) {
                int percent = (int) ((mask * 100L) / totalMatrices);
                System.out.print("\rПрогресс: " + percent + "%  (" + canonical.size() + " графов)");
            }
        }
        System.out.println("\nГенерация завершена. Найдено графов: " + canonical.size());
        System.out.println("Время: " + (System.currentTimeMillis() - startTime) / 1000 + " с");

        // Преобразуем канонические строки в графы
        List<Graph> graphs = new ArrayList<>();
        int count = 0;
        for (String canon : canonical) {
            if (count++ >= MAX_DISPLAY_GRAPHS) break;  // сохраняем только первые MAX_DISPLAY_GRAPHS
            graphs.add(matrixToGraph(n, canon));
        }

        // Для N >= 5 добавляем специальный элемент, показывающий общее количество
        if (n >= 5) {
            // Создаём фиктивный граф для отображения информации
            AdjMatrixDigraph infoGraph = new AdjMatrixDigraph(n);
            // Добавляем несколько рёбер, чтобы граф не был пустым
            for (int i = 0; i < n && i + 1 < n; i++) {
                infoGraph.addAdge(i, i + 1);
                infoGraph.addAdge(i + 1, i);
            }
            // Сохраняем общее количество в статическое поле для доступа из GUI
            GraphGenerator.totalCount = canonical.size();
            // Заменяем список на один информационный граф (если мы не хотим показывать первые 10)
            // Но лучше оставить первые 10 и добавить информационный элемент в комбобокс отдельно.
            // В GUI мы будем использовать totalCount отдельно.
        }

        return graphs;
    }

    public static long totalCount = 0;

    private static boolean hasValidDegrees(int[][] matrix, int n) {
        for (int i = 0; i < n; i++) {
            int outDeg = 0, inDeg = 0;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    outDeg += matrix[i][j];
                    inDeg += matrix[j][i];
                }
            }
            if (outDeg == 0 || inDeg == 0) return false;
        }
        return true;
    }

    private static String canonicalForm(int[][] matrix, int n) {
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) perm[i] = i;
        return permute(matrix, n, perm, 0, null);
    }

    private static String permute(int[][] matrix, int n, int[] perm, int idx, String best) {
        if (idx == n) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                int origRow = perm[i];
                for (int j = 0; j < n; j++) {
                    int origCol = perm[j];
                    sb.append(matrix[origRow][origCol]);
                }
                if (i < n - 1) sb.append('|');
            }
            String cand = sb.toString();
            if (best == null || cand.compareTo(best) < 0) best = cand;
            return best;
        }
        for (int i = idx; i < n; i++) {
            swap(perm, idx, i);
            best = permute(matrix, n, perm, idx + 1, best);
            swap(perm, idx, i);
        }
        return best;
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    private static Graph matrixToGraph(int n, String canon) {
        AdjMatrixDigraph g = new AdjMatrixDigraph(n);
        String[] rows = canon.split("\\|");
        for (int i = 0; i < n && i < rows.length; i++) {
            String row = rows[i];
            for (int j = 0; j < n && j < row.length(); j++) {
                if (row.charAt(j) == '1') {
                    g.addAdge(i, j);
                }
            }
        }
        return g;
    }
}