package com.example.omar.todolisttask;

/**
 * Created by omar on 2/10/17.
 */
public class TodoUpdate {
    private Todo todo;
    public TodoUpdate(Todo todo) {
    this.todo=todo;
    }
    public Todo getTodo(){
        return todo;
    }
}
