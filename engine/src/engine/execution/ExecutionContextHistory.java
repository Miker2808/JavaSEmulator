package engine.execution;

import java.util.ArrayDeque;
import java.util.Deque;

public class ExecutionContextHistory {
    private final Deque<ExecutionContext> history;
    private final int capacity;

    public ExecutionContextHistory(int capacity) {
        this.capacity = capacity;
        this.history = new ArrayDeque<>(capacity);
    }

    // Save a new snapshot
    public void push(ExecutionContext context) {
        if (history.size() == capacity) {
            history.removeFirst(); // drop oldest
        }
        ExecutionContext new_ctx = new ExecutionContext(context);

        history.addLast(new_ctx);
    }


    // Step back and discard it (true backtracking)
    public ExecutionContext popBack() {
        return history.pollLast();
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }

    public int size() {
        return history.size();
    }
}