package ru.vsu.cs.course1.hash.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {
    public List<String[]> readFile(String fileName) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
        String line;
            while((line = reader.readLine()) != null){
                String[] row = line.split(",");
                for (int i = 0; i < row.length; i++) {
                    row[i] = row[i].trim();
                }
                rows.add(row);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rows;
    }
}
