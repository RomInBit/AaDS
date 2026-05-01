package ru.vsu.cs.course1;

import java.util.Comparator;
import java.util.NoSuchElementException;

public class SimplePriorityQueue<T> {

    private final Comparator<T> comparator;
    private final SimpleLinkedList<T> list = new SimpleLinkedList<>();

    public SimplePriorityQueue(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    public void add(T value) {
        if (list.isEmpty()) {
            list.addFirst(value);
            return;
        }

        SimpleLinkedList.Node<T> prev = null;
        SimpleLinkedList.Node<T> curr = list.head;

        while (curr != null && comparator.compare(curr.value, value) <= 0) {
            prev = curr;
            curr = curr.next;
        }

        if (prev == null) {
            list.addFirst(value);
        } else {
            SimpleLinkedList.Node<T> node = new SimpleLinkedList.Node<>(value, curr);
            prev.next = node;
            if (curr == null) {
                list.tail = node;
            }
            list.size++;
        }
    }

    public T poll() {
        if (list.isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return list.removeFirst();
    }

    public T peek() {
        if (list.isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return list.getFirst();
    }
}