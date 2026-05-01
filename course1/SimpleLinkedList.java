package ru.vsu.cs.course1;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SimpleLinkedList<T> implements Iterable<T> {

    protected static class Node<T> {
        T value;
        Node<T> next;

        Node(T value) {
            this.value = value;
        }

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    protected Node<T> head;
    protected Node<T> tail;
    protected int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = tail = null;
        size = 0;
    }

    public void addFirst(T value) {
        Node<T> node = new Node<>(value, head);
        head = node;
        if (tail == null) {
            tail = node;
        }
        size++;
    }

    public void addLast(T value) {
        Node<T> node = new Node<>(value);
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public T getFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        return head.value;
    }

    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        T value = head.value;
        head = head.next;
        size--;
        if (size == 0) {
            tail = null;
        }
        return value;
    }

    public void insertBefore(Node<T> prev, T value) {
        if (prev == null) {
            addFirst(value);
            return;
        }
        Node<T> node = new Node<>(value, prev.next);
        prev.next = node;
        if (prev == tail) {
            tail = node;
        }
        size++;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                T value = current.value;
                current = current.next;
                return value;
            }
        };
    }
}