package com.example.omar.todolisttask;

//import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.MenuItemHoverListener;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;


public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private TodoAdapter todoadaptUndone;
    private TodoAdapter todoadaptDone;
    private ListView uncheckedtodos;
    private ListView checkedtodos;

    private Observable<TodoUpdate> TodoUpdatesObservable;
    private ChildEventListener TodoUpdatesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDatabaseConnection();



        initDatabaseStreams();

        todoadaptUndone=new TodoAdapter(this);
        uncheckedtodos=(ListView) findViewById(R.id.todos_list);
        uncheckedtodos.setAdapter(todoadaptUndone);

        todoadaptDone=new TodoAdapter(this);
        checkedtodos=(ListView) findViewById(R.id.finished_todos_list);
        checkedtodos.setAdapter(todoadaptDone);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem searchitem=menu.findItem(R.id.search_view);
        SearchView searchview= (SearchView) searchitem.getActionView();


        MenuItemCompat.setOnActionExpandListener(searchitem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                findViewById(R.id.add_items_container).setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                findViewById(R.id.add_items_container).setVisibility(View.VISIBLE);
                return true;
            }
        });




        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
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

                Log.i("Rx add",String.valueOf(todoUpdate.getOperation()==todoUpdate.ADD));
                Log.i("Rx done",String.valueOf(todoUpdate.getTodo().isDone()));
                if(todoUpdate.getOperation()==todoUpdate.ADD)

                    if(todoUpdate.getTodo().isDone())
                        todoadaptDone.add(todoUpdate);
                else
                        todoadaptUndone.add(todoUpdate);
                else {
                    if(todoUpdate.getTodo().isDone())
                    {todoadaptUndone.remove(todoUpdate);
                    todoadaptDone.add(todoUpdate);
                        Log.i("MOVETO->","FINISHED");
                    }
                    else
                    {
                        todoadaptDone.remove(todoUpdate);
                        todoadaptUndone.add(todoUpdate);
                        Log.i("MOVETO->","UNFINISHED");
                    }
                }
                checkedtodos.setAdapter(todoadaptDone);
                uncheckedtodos.setAdapter(todoadaptUndone);

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



    }


    private void insertTodoDB(String todo){


        DatabaseReference childRef = myRef.push();


        Todo td=new Todo(todo,false);

        childRef.child("todoi").setValue(td);


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
                            Log.d("Stream","Child added");
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            final Todo todo = convertDataSnapShotToTodo( dataSnapshot);
                            subscriber.onNext(new TodoUpdate(todo,TodoUpdate.UPDATE));
                            Log.d("Stream","Child updated");
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

        return TodoUpdatesObservable;
    }


    private Todo convertDataSnapShotToTodo(DataSnapshot data) {
        Log.d("KEY:",data.getKey());

        return new Todo(data.child("todoi").child("todo").getValue().toString()
        ,Boolean.valueOf(data.child("todoi").child("done").getValue().toString()),data.getKey());

    }

}
