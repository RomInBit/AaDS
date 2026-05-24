package ru.vsu.cs.course1.hash.demo;

import javax.swing.*;

public class Program {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new MainWindow().setVisible(true);
        });
    }
}