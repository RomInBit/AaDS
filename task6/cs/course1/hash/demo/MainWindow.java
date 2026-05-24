package ru.vsu.cs.course1.hash.demo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class MainWindow extends JFrame {

    private JComboBox<String> comboBox;
    private JTable table;

    private JRadioButton hashMapButton;
    private JRadioButton simpleHashMapButton;

    private StudentStorage storage;

    public MainWindow() {

        setTitle("Оценки студентов");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();

        loadStorage(new StudentMarksHashMap());
    }

    private void initComponents() {

        setLayout(new BorderLayout());

        // ==========================
        // Верхняя панель
        // ==========================

        JPanel topPanel = new JPanel();

        hashMapButton = new JRadioButton("HashMap");
        simpleHashMapButton =
                new JRadioButton("SimpleHashMap");

        ButtonGroup group = new ButtonGroup();

        group.add(hashMapButton);
        group.add(simpleHashMapButton);

        hashMapButton.setSelected(true);

        comboBox = new JComboBox<>();

        topPanel.add(hashMapButton);
        topPanel.add(simpleHashMapButton);
        topPanel.add(comboBox);

        add(topPanel, BorderLayout.NORTH);

        // ==========================
        // Таблица
        // ==========================

        table = new JTable();

        add(new JScrollPane(table),
                BorderLayout.CENTER);

        // ==========================
        // Обработчики
        // ==========================

        comboBox.addActionListener(e -> {
            updateTable();
        });

        hashMapButton.addActionListener(e -> {

            loadStorage(new StudentMarksHashMap());
        });

        simpleHashMapButton.addActionListener(e -> {

            loadStorage(
                    new StudentMarksSimpleHashMap()
            );
        });
    }

    private void loadStorage(StudentStorage storage) {

        this.storage = storage;

        storage.loadFromCsv("students.csv");

        comboBox.removeAllItems();

        for (String fio : storage.getStudents()) {

            comboBox.addItem(fio);
        }

        updateTable();
    }

    private void updateTable() {

        String fio =
                (String) comboBox.getSelectedItem();

        if (fio == null) {
            return;
        }

        Map<String, String> marks =
                storage.getStudentMarks(fio);

        DefaultTableModel model =
                new DefaultTableModel(
                        new Object[]{
                                "Предмет",
                                "Оценка"
                        },
                        0
                );

        if (marks != null) {

            for (Map.Entry<String, String> entry :
                    marks.entrySet()) {

                model.addRow(new Object[]{
                        entry.getKey(),
                        entry.getValue()
                });
            }
        }

        table.setModel(model);
    }
}