package ru.vsu.cs.course1.hash.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentMarksHashMap implements StudentStorage {

    private Map<String, Map<String, String>> data =
            new HashMap<>();

    @Override
    public void loadFromCsv(String fileName) {

        CsvReader reader = new CsvReader();

        List<String[]> rows = reader.readFile(fileName);

        for (String[] row : rows) {

            String subject = row[0];
            String fio = row[1];
            String mark = row[2];

            data.putIfAbsent(fio, new HashMap<>());

            data.get(fio).put(subject, mark);
        }
    }

    @Override
    public Map<String, String> getStudentMarks(String fio) {
        return data.get(fio);
    }

    @Override
    public Iterable<String> getStudents() {
        return data.keySet();
    }
}