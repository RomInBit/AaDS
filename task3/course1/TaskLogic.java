package ru.vsu.cs.course1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class TaskLogic {

    public static class PrintJob {
        public int arrivalTime;
        public int pages;
        public int priority;
        public int id;

        public int startTime = -1;
        public int endTime = -1;

        public int inputOrder;

        public PrintJob(int arrivalTime, int pages, int priority, int id, int inputOrder) {
            this.arrivalTime = arrivalTime;
            this.pages = pages;
            this.priority = priority;
            this.id = id;
            this.inputOrder = inputOrder;
        }

        @Override
        public String toString() {
            return String.format("id=%d, t=%d, n=%d, p=%d, [%d, %d)",
                    id, arrivalTime, pages, priority, startTime, endTime);
        }
    }

    public enum QueueMode {
        CUSTOM, STANDARD
    }

    public static final Comparator<PrintJob> JOB_COMPARATOR = (a, b) -> {
        if (a.priority != b.priority) {
            return Integer.compare(b.priority, a.priority);
        }
        if (a.arrivalTime != b.arrivalTime) {
            return Integer.compare(a.arrivalTime, b.arrivalTime);
        }
        if (a.id != b.id) {
            return Integer.compare(a.id, b.id);
        }
        return Integer.compare(a.inputOrder, b.inputOrder);
    };

    public static List<PrintJob> solve(List<PrintJob> jobs, QueueMode mode) {
        if (jobs == null) {
            return new ArrayList<>();
        }

        if (jobs.isEmpty()) {
            return jobs;
        }

        List<PrintJob> sorted = new ArrayList<>(jobs);
        sorted.sort(Comparator.comparingInt(j -> j.arrivalTime));

        int time = 0;
        int idx = 0;
        int done = 0;
        int n = sorted.size();

        if (mode == QueueMode.CUSTOM) {
            SimplePriorityQueue<PrintJob> queue = new SimplePriorityQueue<>(JOB_COMPARATOR);

            while (done < n) {
                if (queue.isEmpty() && idx < n && time < sorted.get(idx).arrivalTime) {
                    time = sorted.get(idx).arrivalTime;
                }

                while (idx < n && sorted.get(idx).arrivalTime <= time) {
                    queue.add(sorted.get(idx));
                    idx++;
                }

                if (queue.isEmpty()) {
                    continue;
                }

                PrintJob current = queue.poll();
                current.startTime = time;
                current.endTime = time + current.pages;
                time = current.endTime;
                done++;
            }
        } else {
            PriorityQueue<PrintJob> queue = new PriorityQueue<>(JOB_COMPARATOR);

            while (done < n) {
                if (queue.isEmpty() && idx < n && time < sorted.get(idx).arrivalTime) {
                    time = sorted.get(idx).arrivalTime;
                }

                while (idx < n && sorted.get(idx).arrivalTime <= time) {
                    queue.add(sorted.get(idx));
                    idx++;
                }

                if (queue.isEmpty()) {
                    continue;
                }

                PrintJob current = queue.poll();
                current.startTime = time;
                current.endTime = time + current.pages;
                time = current.endTime;
                done++;
            }
        }

        jobs.sort(Comparator.comparingInt(j -> j.inputOrder));
        return jobs;
    }
}