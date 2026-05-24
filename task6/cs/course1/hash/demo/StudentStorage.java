package ru.vsu.cs.course1.hash.demo;

import java.util.Map;

public interface StudentStorage {

    void loadFromCsv(String fileName);

    Map<String, String> getStudentMarks(String fio);

    Iterable<String> getStudents();
}