package ru.vsu.cs.course1.hash.demo;

import ru.vsu.cs.course1.hash.SimpleHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentMarksSimpleHashMap implements StudentStorage {

    private SimpleHashMap<String,
            SimpleHashMap<String, String>> data =
            new SimpleHashMap<>(100);

    @Override
    public void loadFromCsv(String fileName) {

        CsvReader reader = new CsvReader();

        List<String[]> rows = reader.readFile(fileName);

        for (String[] row : rows) {

            String subject = row[0];
            String fio = row[1];
            String mark = row[2];

            if (!data.containsKey(fio)) {
                data.put(fio, new SimpleHashMap<>(20));
            }

            data.get(fio).put(subject, mark);
        }
    }

    @Override
    public Map<String, String> getStudentMarks(String fio) {

        SimpleHashMap<String, String> marks = data.get(fio);

        Map<String, String> result = new HashMap<>();

        if (marks != null) {

            for (Map.Entry<String, String> entry :
                    marks.entrySet()) {

                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    @Override
    public Iterable<String> getStudents() {

        java.util.List<String> list =
                new java.util.ArrayList<>();

        for (Map.Entry<String,
                SimpleHashMap<String, String>> entry :
                data.entrySet()) {

            list.add(entry.getKey());
        }

        return list;
    }
}