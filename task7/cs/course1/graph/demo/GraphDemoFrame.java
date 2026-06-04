package ru.vsu.cs.course1.graph.demo;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.*;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;
import ru.vsu.cs.course1.graph.Graph;
import ru.vsu.cs.course1.graph.GraphAlgorithms;
import ru.vsu.cs.course1.graph.GraphUtils;
import ru.vsu.cs.course1.graph.Digraph;
import ru.vsu.cs.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphDemoFrame extends JFrame {

    private Graph graph = null;

    // Компоненты
    private JTextArea textAreaGraphFile;
    private JComboBox<String> comboBoxGraphType;
    private JTextArea textAreaSystemOut;
    private SvgPanel panelGraphPainter;
    private JSpinner spinnerFromVertex;

    // Кнопки обходов
    private JButton buttonDfsIterator;
    private JButton buttonDfsStack;
    private JButton buttonDfsRecursion;
    private JButton buttonBfsIterator;
    private JButton buttonBfsQueue;

    // Новые кнопки для раскраски
    private JButton buttonColorize;
    private JButton buttonChromaticNumber;

    // Кнопки для работы с файлами и Graphviz
    private JButton buttonLoadGraphFromFile;
    private JButton buttonSaveGraphToFile;
    private JButton buttonCreateGraph;
    private JButton buttonSaveGraphSvgToFile;

    private JTextArea textAreaDotFile;
    private JButton buttonLoadDotFile;
    private JButton buttonSaveDotFile;
    private JButton buttonDotPaint;
    private JButton buttonSaveDotSvgToFile;
    private JComboBox<String> comboBoxExample;
    private JButton buttonExampleExec;
    private SvgPanel panelGraphvizPainter;

    // File choosers
    private JFileChooser fileChooserTxtOpen;
    private JFileChooser fileChooserDotOpen;
    private JFileChooser fileChooserTxtSave;
    private JFileChooser fileChooserDotSave;
    private JFileChooser fileChooserImgSave;

    // =========================================================================
    // Внутренний класс для отображения SVG
    // =========================================================================
    private static class SvgPanel extends JPanel {
        private String svg = null;
        private GraphicsNode svgGraphicsNode = null;

        public void paint(String svg) throws IOException {
            String xmlParser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory df = new SAXSVGDocumentFactory(xmlParser);
            SVGDocument doc = df.createSVGDocument(null, new StringReader(svg));
            UserAgent userAgent = new UserAgentAdapter();
            DocumentLoader loader = new DocumentLoader(userAgent);
            BridgeContext ctx = new BridgeContext(userAgent, loader);
            ctx.setDynamicState(BridgeContext.DYNAMIC);
            GVTBuilder builder = new GVTBuilder();
            svgGraphicsNode = builder.build(ctx, doc);

            this.svg = svg;
            repaint();
        }

        @Override
        public void paintComponent(Graphics gr) {
            super.paintComponent(gr);
            if (svgGraphicsNode == null) return;

            double scaleX = this.getWidth() / svgGraphicsNode.getPrimitiveBounds().getWidth();
            double scaleY = this.getHeight() / svgGraphicsNode.getPrimitiveBounds().getHeight();
            double scale = Math.min(scaleX, scaleY);
            AffineTransform transform = new AffineTransform(scale, 0, 0, scale, 0, 0);
            svgGraphicsNode.setTransform(transform);
            Graphics2D g2d = (Graphics2D) gr;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            svgGraphicsNode.paint(g2d);
        }
    }

    // =========================================================================
    // Конструктор
    // =========================================================================
    public GraphDemoFrame() {
        super("Раскраска графа минимальным числом цветов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        initFileChoosers();
        initComponents();
        layoutComponents();
        bindActions();
        pack();
        setLocationRelativeTo(null);
    }

    private void initFileChoosers() {
        fileChooserTxtOpen = new JFileChooser(new File("./files/input"));
        fileChooserDotOpen = new JFileChooser(new File("./files/input"));
        fileChooserTxtSave = new JFileChooser(new File("./files/input"));
        fileChooserDotSave = new JFileChooser(new File("./files/input"));
        fileChooserImgSave = new JFileChooser(new File("./files/output"));

        FileFilter txtFilter = new FileNameExtensionFilter("Text files (*.txt)", "txt");
        FileFilter dotFilter = new FileNameExtensionFilter("DOT files (*.dot)", "dot");
        FileFilter svgFilter = new FileNameExtensionFilter("SVG images (*.svg)", "svg");

        fileChooserTxtOpen.addChoosableFileFilter(txtFilter);
        fileChooserDotOpen.addChoosableFileFilter(dotFilter);
        fileChooserTxtSave.addChoosableFileFilter(txtFilter);
        fileChooserDotSave.addChoosableFileFilter(dotFilter);
        fileChooserImgSave.addChoosableFileFilter(svgFilter);

        fileChooserTxtSave.setAcceptAllFileFilterUsed(false);
        fileChooserTxtSave.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserDotSave.setAcceptAllFileFilterUsed(false);
        fileChooserDotSave.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserImgSave.setAcceptAllFileFilterUsed(false);
        fileChooserImgSave.setDialogType(JFileChooser.SAVE_DIALOG);
    }

    private void initComponents() {
        // --- Панель графа ---
        textAreaGraphFile = new JTextArea(10, 30);
        textAreaGraphFile.setText("13\n13\n0 5\n4 3\n0 1\n9 12\n6 4\n5 4\n0 2\n11 12\n9 10\n0 6\n7 8\n9 11\n5 3\n");
        textAreaGraphFile.setFont(new Font("Monospaced", Font.PLAIN, 12));

        comboBoxGraphType = new JComboBox<>(new String[]{
                "Н-граф (AdjMatrixGraph)",
                "Н-граф (AdjListsGraph)",
                "Орграф (AdjMatrixDigraph)",
                "Орграф (AdjListsDigraph)"
        });

        buttonLoadGraphFromFile = new JButton("Загрузить из файла");
        buttonSaveGraphToFile = new JButton("Сохранить в файл");
        buttonCreateGraph = new JButton("Построить граф");

        panelGraphPainter = new SvgPanel();
        buttonSaveGraphSvgToFile = new JButton("Сохранить SVG");

        // --- Панель обходов и раскраски ---
        spinnerFromVertex = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));

        buttonDfsIterator = new JButton("dfs (итератор)");
        buttonDfsStack = new JButton("dfs (стек)");
        buttonDfsRecursion = new JButton("dfs (рекурсия)");
        buttonBfsIterator = new JButton("bfs (итератор)");
        buttonBfsQueue = new JButton("bfs (очередь)");

        // Новые кнопки
        buttonColorize = new JButton("Раскрасить граф");
        buttonChromaticNumber = new JButton("Хроматическое число");

        textAreaSystemOut = new JTextArea(8, 40);
        textAreaSystemOut.setEditable(false);
        textAreaSystemOut.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // --- Панель Graphviz ---
        textAreaDotFile = new JTextArea(10, 30);
        textAreaDotFile.setText("graph {\n    2 -- { 3 -- 4 }\n    3 -- 6 -- { 2 4 }\n    7 -- 8 -- 7\n    9\n}\n");
        textAreaDotFile.setFont(new Font("Monospaced", Font.PLAIN, 12));

        buttonLoadDotFile = new JButton("Загрузить DOT");
        buttonSaveDotFile = new JButton("Сохранить DOT");
        buttonDotPaint = new JButton("Отобразить");
        buttonSaveDotSvgToFile = new JButton("Сохранить SVG");

        comboBoxExample = new JComboBox<>();
        Method[] methods = GraphvizExamples.class.getMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers()) && method.getReturnType() == String.class && method.getParameterCount() == 0) {
                comboBoxExample.addItem(method.getName() + "()");
            }
        }
        buttonExampleExec = new JButton("Выполнить");

        panelGraphvizPainter = new SvgPanel();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        JTabbedPane tabbedPane = new JTabbedPane();

        // ---- Вкладка "Граф" ----
        JPanel graphTab = new JPanel(new BorderLayout(10, 10));
        graphTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Левая часть: ввод текста и управление
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        // Верхняя панель: текстовое поле + кнопки файлов
        JPanel filePanel = new JPanel(new BorderLayout(5, 5));
        filePanel.add(new JScrollPane(textAreaGraphFile), BorderLayout.CENTER);

        JPanel fileButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileButtons.add(buttonLoadGraphFromFile);
        fileButtons.add(buttonSaveGraphToFile);
        filePanel.add(fileButtons, BorderLayout.SOUTH);

        leftPanel.add(filePanel, BorderLayout.CENTER);

        // Нижняя часть левой панели: тип графа и кнопка построить
        JPanel buildPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buildPanel.add(new JLabel("Тип графа:"));
        buildPanel.add(comboBoxGraphType);
        buildPanel.add(buttonCreateGraph);
        leftPanel.add(buildPanel, BorderLayout.SOUTH);

        // Правая часть: отображение графа
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Визуализация графа"));
        rightPanel.add(new JScrollPane(panelGraphPainter), BorderLayout.CENTER);
        JPanel svgSavePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        svgSavePanel.add(buttonSaveGraphSvgToFile);
        rightPanel.add(svgSavePanel, BorderLayout.SOUTH);

        JSplitPane graphSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        graphSplit.setResizeWeight(0.4);
        graphTab.add(graphSplit, BorderLayout.CENTER);

        // Нижняя часть вкладки Граф: обходы и вывод
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Обходы и раскраска"));

        JPanel traversePanel = new JPanel(new GridLayout(2, 3, 5, 5));
        traversePanel.setBorder(BorderFactory.createTitledBorder("Обходы"));
        traversePanel.add(buttonDfsIterator);
        traversePanel.add(buttonDfsStack);
        traversePanel.add(buttonDfsRecursion);
        traversePanel.add(buttonBfsIterator);
        traversePanel.add(buttonBfsQueue);

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.setBorder(BorderFactory.createTitledBorder("Раскраска"));
        colorPanel.add(new JLabel("Начальная вершина:"));
        colorPanel.add(spinnerFromVertex);
        colorPanel.add(buttonColorize);
        colorPanel.add(buttonChromaticNumber);

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Результат"));
        outputPanel.add(new JScrollPane(textAreaSystemOut), BorderLayout.CENTER);

        JPanel traverseColorPanel = new JPanel(new BorderLayout());
        traverseColorPanel.add(traversePanel, BorderLayout.NORTH);
        traverseColorPanel.add(colorPanel, BorderLayout.CENTER);

        bottomPanel.add(traverseColorPanel, BorderLayout.WEST);
        bottomPanel.add(outputPanel, BorderLayout.CENTER);

        graphTab.add(bottomPanel, BorderLayout.SOUTH);

        // ---- Вкладка "Graphviz" ----
        JPanel vizTab = new JPanel(new BorderLayout(10, 10));
        vizTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel vizLeft = new JPanel(new BorderLayout(5, 5));
        vizLeft.add(new JScrollPane(textAreaDotFile), BorderLayout.CENTER);

        JPanel vizButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        vizButtons.add(buttonLoadDotFile);
        vizButtons.add(buttonSaveDotFile);
        vizButtons.add(buttonDotPaint);
        vizLeft.add(vizButtons, BorderLayout.SOUTH);

        JPanel vizRight = new JPanel(new BorderLayout(5, 5));
        vizRight.setBorder(BorderFactory.createTitledBorder("Результат Graphviz"));
        vizRight.add(new JScrollPane(panelGraphvizPainter), BorderLayout.CENTER);
        JPanel vizSavePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        vizSavePanel.add(buttonSaveDotSvgToFile);
        vizRight.add(vizSavePanel, BorderLayout.SOUTH);

        JPanel vizExample = new JPanel(new FlowLayout(FlowLayout.LEFT));
        vizExample.add(new JLabel("Пример:"));
        vizExample.add(comboBoxExample);
        vizExample.add(buttonExampleExec);
        vizLeft.add(vizExample, BorderLayout.NORTH);

        JSplitPane vizSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, vizLeft, vizRight);
        vizSplit.setResizeWeight(0.4);
        vizTab.add(vizSplit, BorderLayout.CENTER);

        tabbedPane.addTab("Граф", graphTab);
        tabbedPane.addTab("Graphviz", vizTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void bindActions() {
        // Работа с файлами графа
        buttonLoadGraphFromFile.addActionListener(e -> {
            if (fileChooserTxtOpen.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (Scanner sc = new Scanner(fileChooserTxtOpen.getSelectedFile())) {
                    sc.useDelimiter("\\Z");
                    textAreaGraphFile.setText(sc.next());
                } catch (Exception ex) {
                    SwingUtils.showErrorMessageBox(ex);
                }
            }
        });

        buttonSaveGraphToFile.addActionListener(e -> {
            if (fileChooserTxtSave.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooserTxtSave.getSelectedFile().getPath();
                if (!filename.toLowerCase().endsWith(".txt")) filename += ".txt";
                try (FileWriter wr = new FileWriter(filename)) {
                    wr.write(textAreaGraphFile.getText());
                } catch (Exception ex) {
                    SwingUtils.showErrorMessageBox(ex);
                }
            }
        });

        buttonCreateGraph.addActionListener(e -> {
            try {
                String name = comboBoxGraphType.getSelectedItem().toString();
                Matcher matcher = Pattern.compile(".*\\W(\\w+)\\s*\\)\\s*$").matcher(name);
                matcher.find();
                String className = matcher.group(1);
                Class<?> clz = Class.forName("ru.vsu.cs.course1.graph." + className);
                graph = GraphUtils.fromStr(textAreaGraphFile.getText(), clz);
                panelGraphPainter.paint(dotToSvg(GraphUtils.toDot(graph)));
            } catch (Exception ex) {
                SwingUtils.showErrorMessageBox(ex);
            }
        });

        buttonSaveGraphSvgToFile.addActionListener(e -> saveSvg(panelGraphPainter, fileChooserImgSave));

        // Обходы
        buttonDfsIterator.addActionListener(e -> runTraversal(() -> {
            int from = (int) spinnerFromVertex.getValue();
            boolean first = true;
            for (Integer v : GraphAlgorithms.dfs(graph, from)) {
                System.out.print(first ? "" : ", ");
                first = false;
                System.out.print(v);
            }
            System.out.println();
        }));

        buttonDfsStack.addActionListener(e -> runTraversal(() -> {
            int from = (int) spinnerFromVertex.getValue();
            boolean[] first = {true};
            GraphAlgorithms.dfs(graph, from, v -> {
                System.out.print(first[0] ? "" : ", ");
                first[0] = false;
                System.out.print(v);
            });
        }));

        buttonDfsRecursion.addActionListener(e -> runTraversal(() -> {
            int from = (int) spinnerFromVertex.getValue();
            boolean[] first = {true};
            GraphAlgorithms.dfsRecursion(graph, from, v -> {
                System.out.print(first[0] ? "" : ", ");
                first[0] = false;
                System.out.print(v);
            });
        }));

        buttonBfsIterator.addActionListener(e -> runTraversal(() -> {
            int from = (int) spinnerFromVertex.getValue();
            boolean first = true;
            for (Integer v : GraphAlgorithms.bfs(graph, from)) {
                System.out.print(first ? "" : ", ");
                first = false;
                System.out.print(v);
            }
            System.out.println();
        }));

        buttonBfsQueue.addActionListener(e -> runTraversal(() -> {
            int from = (int) spinnerFromVertex.getValue();
            boolean[] first = {true};
            GraphAlgorithms.bfs(graph, from, v -> {
                System.out.print(first[0] ? "" : ", ");
                first[0] = false;
                System.out.print(v);
            });
        }));

        // Раскраска
        buttonColorize.addActionListener(e -> colorizeAndDisplay());
        buttonChromaticNumber.addActionListener(e -> showChromaticNumber());

        // Работа с DOT
        buttonLoadDotFile.addActionListener(e -> {
            if (fileChooserDotOpen.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (Scanner sc = new Scanner(fileChooserDotOpen.getSelectedFile())) {
                    sc.useDelimiter("\\Z");
                    textAreaDotFile.setText(sc.next());
                } catch (Exception ex) {
                    SwingUtils.showErrorMessageBox(ex);
                }
            }
        });

        buttonSaveDotFile.addActionListener(e -> {
            if (fileChooserDotSave.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooserDotSave.getSelectedFile().getPath();
                if (!filename.toLowerCase().endsWith(".dot")) filename += ".dot";
                try (FileWriter wr = new FileWriter(filename)) {
                    wr.write(textAreaDotFile.getText());
                } catch (Exception ex) {
                    SwingUtils.showErrorMessageBox(ex);
                }
            }
        });

        buttonDotPaint.addActionListener(e -> {
            try {
                panelGraphvizPainter.paint(dotToSvg(textAreaDotFile.getText()));
            } catch (Exception ex) {
                SwingUtils.showErrorMessageBox(ex);
            }
        });

        buttonSaveDotSvgToFile.addActionListener(e -> saveSvg(panelGraphvizPainter, fileChooserImgSave));

        buttonExampleExec.addActionListener(e -> {
            try {
                String name = comboBoxExample.getSelectedItem().toString();
                if (name.endsWith("()")) name = name.substring(0, name.length() - 2);
                Method method = GraphvizExamples.class.getMethod(name);
                String svg = (String) method.invoke(null);
                panelGraphvizPainter.paint(svg);
            } catch (Exception ex) {
                SwingUtils.showErrorMessageBox(ex);
            }
        });
    }

    // -------------------------------------------------------------------------
    // Вспомогательные методы
    // -------------------------------------------------------------------------
    private String dotToSvg(String dotSrc) throws IOException {
        MutableGraph g = new Parser().read(dotSrc);
        return Graphviz.fromGraph(g).render(Format.SVG).toString();
    }

    private void runTraversal(Runnable action) {
        if (graph == null) {
            SwingUtils.showInfoMessageBox("Граф не создан. Сначала постройте граф.");
            return;
        }
        showSystemOut(action);
    }

    private void showSystemOut(Runnable action) {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(baos, true, "UTF-8"));
            action.run();
            textAreaSystemOut.setText(baos.toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            SwingUtils.showErrorMessageBox(e);
        } finally {
            System.setOut(oldOut);
        }
    }

    private void saveSvg(SvgPanel panel, JFileChooser chooser) {
        if (panel.svg == null) {
            SwingUtils.showInfoMessageBox("Нет изображения для сохранения.");
            return;
        }
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filename = chooser.getSelectedFile().getPath();
            if (!filename.toLowerCase().endsWith(".svg")) filename += ".svg";
            try (FileWriter wr = new FileWriter(filename)) {
                wr.write(panel.svg);
            } catch (Exception ex) {
                SwingUtils.showErrorMessageBox(ex);
            }
        }
    }

    private void colorizeAndDisplay() {
        if (graph == null) {
            SwingUtils.showInfoMessageBox("Граф не создан. Сначала постройте граф.");
            return;
        }
        try {
            int[] colors = GraphAlgorithms.graphColoring(graph);
            int chromaticNumber = GraphAlgorithms.chromaticNumber(graph);
            showSystemOut(() -> {
                System.out.println("Раскраска графа (минимальное число цветов = " + chromaticNumber + "):");
                for (int i = 0; i < colors.length; i++) {
                    System.out.printf("Вершина %d -> цвет %d%n", i, colors[i]);
                }
            });
            String dotWithColors = buildDotWithColors(graph, colors);
            String svg = dotToSvg(dotWithColors);
            panelGraphPainter.paint(svg);
        } catch (Exception ex) {
            SwingUtils.showErrorMessageBox("Ошибка при раскраске графа", ex);
        }
    }

    private void showChromaticNumber() {
        if (graph == null) {
            SwingUtils.showInfoMessageBox("Граф не создан.");
            return;
        }
        try {
            int num = GraphAlgorithms.chromaticNumber(graph);
            showSystemOut(() -> System.out.println("Хроматическое число графа = " + num));
        } catch (Exception ex) {
            SwingUtils.showErrorMessageBox("Ошибка при вычислении хроматического числа", ex);
        }
    }

    private String buildDotWithColors(Graph graph, int[] colors) {
        boolean isDigraph = graph instanceof Digraph;
        StringBuilder sb = new StringBuilder();
        String nl = System.lineSeparator();
        sb.append(isDigraph ? "digraph" : "strict graph").append(" {").append(nl);
        sb.append("  node [style=filled];").append(nl);

        int maxColor = 0;
        for (int c : colors) if (c > maxColor) maxColor = c;
        String[] colorPalette = new String[maxColor + 1];
        for (int i = 0; i <= maxColor; i++) {
            java.awt.Color awtColor = java.awt.Color.getHSBColor(i / (float) (maxColor + 1), 0.8f, 0.9f);
            colorPalette[i] = String.format("#%02x%02x%02x", awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
        }

        for (int v = 0; v < graph.vertexCount(); v++) {
            sb.append(String.format("  %d [fillcolor=\"%s\"];%n", v, colorPalette[colors[v]]));
        }
        for (int v1 = 0; v1 < graph.vertexCount(); v1++) {
            for (Integer v2 : graph.adges(v1)) {
                if (isDigraph || v1 <= v2) {
                    sb.append(String.format("  %d %s %d%n", v1, isDigraph ? "->" : "--", v2));
                }
            }
        }
        sb.append("}").append(nl);
        return sb.toString();
    }
}