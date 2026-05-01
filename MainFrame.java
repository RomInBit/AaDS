package ru.vsu.cs.course1;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainFrame extends JFrame {

    private final DefaultTableModel inputModel;
    private final DefaultTableModel resultModel;

    private final JTable inputTable;
    private final JTable resultTable;

    public MainFrame() {
        setTitle("Моделирование работы сетевого принтера");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        inputModel = new DefaultTableModel(new Object[]{"t", "N", "P", "X"}, 0);
        inputTable = new JTable(inputModel);

        resultModel = new DefaultTableModel(new Object[]{"X", "Начало", "Конец"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(resultModel);

        JScrollPane inputScroll = new JScrollPane(inputTable);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Входные данные"));

        JScrollPane resultScroll = new JScrollPane(resultTable);
        resultScroll.setBorder(BorderFactory.createTitledBorder("Результат"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputScroll, resultScroll);
        splitPane.setDividerLocation(300);

        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Добавить строку");
        JButton removeButton = new JButton("Удалить строку");
        JButton loadButton = new JButton("Загрузить из файла");
        JButton solveCustomButton = new JButton("Решить (своя очередь)");
        JButton solveStandardButton = new JButton("Решить (стандартная очередь)");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(solveCustomButton);
        buttonPanel.add(solveStandardButton);

        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> inputModel.addRow(new Object[]{0, 1, 1, 0}));

        removeButton.addActionListener(e -> {
            int row = inputTable.getSelectedRow();
            if (row >= 0) {
                inputModel.removeRow(row);
            } else {
                JOptionPane.showMessageDialog(this, "Выберите строку для удаления.", "Внимание", JOptionPane.WARNING_MESSAGE);
            }
        });

        loadButton.addActionListener(e -> loadFromFile());

        solveCustomButton.addActionListener(e -> solve(TaskLogic.QueueMode.CUSTOM));
        solveStandardButton.addActionListener(e -> solve(TaskLogic.QueueMode.STANDARD));

        inputModel.addRow(new Object[]{0, 5, 2, 101});
        inputModel.addRow(new Object[]{1, 3, 5, 102});
        inputModel.addRow(new Object[]{2, 4, 5, 100});
    }

    private void loadFromFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Выберите файл с заданиями");
        chooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt", "csv"));

        int res = chooser.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        try {
            List<TaskLogic.PrintJob> jobs = readJobsFromFile(file);
            inputModel.setRowCount(0);
            for (TaskLogic.PrintJob job : jobs) {
                inputModel.addRow(new Object[]{
                        job.arrivalTime,
                        job.pages,
                        job.priority,
                        job.id
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Не удалось загрузить файл:\n" + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private List<TaskLogic.PrintJob> readJobsFromFile(File file) throws FileNotFoundException {
        List<TaskLogic.PrintJob> jobs = new ArrayList<>();

        try (Scanner sc = new Scanner(file, "UTF-8")) {
            if (!sc.hasNextInt()) {
                throw new IllegalArgumentException("В файле не найдено число заданий");
            }

            int n = sc.nextInt();
            for (int i = 0; i < n; i++) {
                if (!sc.hasNextInt()) throw new IllegalArgumentException("Недостаточно данных в строке " + (i + 1));
                int t = sc.nextInt();

                if (!sc.hasNextInt()) throw new IllegalArgumentException("Недостаточно данных в строке " + (i + 1));
                int pages = sc.nextInt();

                if (!sc.hasNextInt()) throw new IllegalArgumentException("Недостаточно данных в строке " + (i + 1));
                int priority = sc.nextInt();

                if (!sc.hasNextInt()) throw new IllegalArgumentException("Недостаточно данных в строке " + (i + 1));
                int id = sc.nextInt();

                jobs.add(new TaskLogic.PrintJob(t, pages, priority, id, i));
            }
        }

        return jobs;
    }

    private void solve(TaskLogic.QueueMode mode) {
        try {
            List<TaskLogic.PrintJob> jobs = new ArrayList<>();

            for (int i = 0; i < inputModel.getRowCount(); i++) {
                int t = parseInt(inputModel.getValueAt(i, 0), "t", i + 1);
                int n = parseInt(inputModel.getValueAt(i, 1), "N", i + 1);
                int p = parseInt(inputModel.getValueAt(i, 2), "P", i + 1);
                int x = parseInt(inputModel.getValueAt(i, 3), "X", i + 1);

                jobs.add(new TaskLogic.PrintJob(t, n, p, x, i));
            }

            TaskLogic.solve(jobs, mode);

            resultModel.setRowCount(0);
            for (TaskLogic.PrintJob job : jobs) {
                resultModel.addRow(new Object[]{
                        job.id,
                        job.startTime,
                        job.endTime
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ошибка при расчёте:\n" + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private int parseInt(Object value, String columnName, int rowNumber) {
        if (value == null) {
            throw new IllegalArgumentException("Пустое значение в столбце " + columnName + ", строка " + rowNumber);
        }
        try {
            return Integer.parseInt(value.toString().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное число в столбце " + columnName + ", строка " + rowNumber);
        }
    }
}