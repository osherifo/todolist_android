package com.example.omar.todolisttask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;


public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private TodoAdapter todoadapt;
    private ListView uncheckedtodos;
    private ListView checkedtodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDatabaseConnection();



        initDatabaseStreams();

        todoadapt=new TodoAdapter(this);
        uncheckedtodos=(ListView) findViewById(R.id.todos_list);
        uncheckedtodos.setAdapter(todoadapt);

    }

    private void initDatabaseStreams() {





       // Observable.fromCallable(database.)

        Observer<TodoUpdate> myObserver = new Observer<TodoUpdate>() {


            @Override
            public void onError(Throwable e) {
                // Called when the observable encounters an error
            }

            @Override
            public void onNext(TodoUpdate todoUpdate) {
                Log.i("RXXXXXXX",todoUpdate.getTodo().getTodo() );
                if(todoUpdate.getOperation()==todoUpdate.ADD)
                todoadapt.add(todoUpdate);
                else
                    todoadapt.update(todoUpdate);
                uncheckedtodos.setAdapter(todoadapt);

            }

            @Override
            public void onCompleted() {

            }



//            @Override
//            public void onNext(String s) {
//                // Called each time the observable emits data
//                Log.i("TODO:", s);
//            }
        };


        subscribeToTodoUpdates();
        TodoUpdatesObservable.subscribe(myObserver);
    }

    private void initDatabaseConnection() {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("todoItems");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("ADDED",dataSnapshot.toString());
                Log.i("tododatass",dataSnapshot.child("todoi").child("todo").getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void insertTodoDB(String todo){


        DatabaseReference childRef = myRef.push();


        Todo td=new Todo(todo,false);

        childRef.child("todoi").setValue(td);

//        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
//        String date = dateFormat.format(new Date());
//
//        childRef.child("timestamp").setValue(date);
    }

    public void updateTodoDoneDB(String ref,boolean done){
        myRef.child(ref).child("todoi").child("done").setValue(String.valueOf(done));
    }


    private void updateTable(){

    }


    public void addItemButtonListener(View view){
        EditText todoTextView = (EditText) findViewById(R.id.add_todo_text);
        String todo= todoTextView.getText().toString();
        if(!todo.isEmpty()) {
            Log.i("TODO:", todo);
            todoTextView.setText("");
            insertTodoDB(todo);
        }

    }

    //trial

    private Observable<TodoUpdate> TodoUpdatesObservable;
    private ChildEventListener TodoUpdatesListener;
    private int TodoUpdatesSubscriptionsCount;


    public Observable<TodoUpdate> subscribeToTodoUpdates() {
        if (TodoUpdatesObservable == null) {
            TodoUpdatesObservable = Observable.create(new Observable.OnSubscribe<TodoUpdate>() {

                @Override
                public void call(final Subscriber<? super TodoUpdate> subscriber) {
                    TodoUpdatesListener = myRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            final Todo todo = convertDataSnapShotToTodo( dataSnapshot);
                            subscriber.onNext(new TodoUpdate(todo,TodoUpdate.ADD));
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            final Todo todo = convertDataSnapShotToTodo( dataSnapshot);
                            subscriber.onNext(new TodoUpdate(todo,TodoUpdate.UPDATE));
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }


                    });
                }
            });
        }
       // leisureUpdatesSubscriptionsCount++;
        return TodoUpdatesObservable;
    }

    //TODO:implement
    private Todo convertDataSnapShotToTodo(DataSnapshot data) {
        Log.d("KEY:",data.getKey());

        return new Todo(data.child("todoi").child("todo").getValue().toString()
        ,Boolean.valueOf(data.child("todoi").child("todo").getValue().toString()),data.getKey());

    }

}
