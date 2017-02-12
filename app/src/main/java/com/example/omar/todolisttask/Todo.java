package com.example.omar.todolisttask;

/**
 * Created by omar on 2/10/17.
 */
public class Todo {

    private String todo;
    private boolean done;
    private String key;

    public Todo(String todo,boolean done,String key){
        this.todo=todo;
        this.done=done;
        this.key=key;
    }

    public Todo(String todo,boolean done){
        this.todo=todo;
        this.done=done;

    }
    public String getTodo(){
        return todo;
    }
    public boolean isDone(){
        return done;
    }
    public String getKey(){
        return key;
    }

}
