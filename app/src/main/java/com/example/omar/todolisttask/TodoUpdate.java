package com.example.omar.todolisttask;

/**
 * A class representing a to do update operation
 */
public class TodoUpdate {
    private Todo todo;
    public static final boolean ADD = true;
    public static final boolean UPDATE = false;
    private boolean operation;

    public TodoUpdate(Todo todo, boolean operation) {
        this.todo = todo;
        this.operation = operation;
    }

    public Todo getTodo() {
        return todo;
    }

    public boolean getOperation() {
        return operation;
    }
}
