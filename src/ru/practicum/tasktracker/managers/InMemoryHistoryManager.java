package ru.practicum.tasktracker.managers;

import ru.practicum.tasktracker.models.Task;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    private void linkLast(Task element) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        historyMap.put(element.getId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    private List<Task> getTasks() {
        return Stream.iterate(head, Objects::nonNull, node -> node.next)
                .map(node -> node.data)
                .collect(Collectors.toList());
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            Node<Task> next = node.next;
            Node<Task> prev = node.prev;
            node.data = null;
            if (head == node && tail == node) {
                head = null;
                tail = null;
            } else if (head == node) {
                head = next;
                if (head != null) {
                    head.prev = null;
                }
            } else if (tail == node) {
                tail = prev;
                if (tail != null) {
                    tail.next = null;
                }
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }
    }

    @Override
    public void updateHistory(Task task) {
        if (historyMap.containsKey(task.getId())) {
            historyMap.get(task.getId()).data = getCopyTask(task);
        }
    }

    private Task getCopyTask(Task task) {
        return task.createCopyTask(task);
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks().reversed();
    }

    private static class Node<T> {
        private T data;
        private Node<T> next;
        private Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}