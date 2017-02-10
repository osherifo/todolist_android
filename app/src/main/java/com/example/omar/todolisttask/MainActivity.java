package com.example.omar.todolisttask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }





    public void addItemButtonListener(View view){
        EditText todoTextView = (EditText) findViewById(R.id.add_todo_text);
        String todo= todoTextView.getText().toString();
        if(!todo.isEmpty()) {
            Log.i("TODO:", todo);
            todoTextView.setText("");
        }

    }
}
