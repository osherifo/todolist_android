package com.example.omar.todolisttask;


import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

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
        initObserver();

        todoadaptUndone = new TodoAdapter(this);
        uncheckedtodos = (ListView) findViewById(R.id.todos_list);
        uncheckedtodos.setAdapter(todoadaptUndone);

        todoadaptDone = new TodoAdapter(this);
        checkedtodos = (ListView) findViewById(R.id.finished_todos_list);
        checkedtodos.setAdapter(todoadaptDone);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem searchitem = menu.findItem(R.id.search_view);
        SearchView searchview = (SearchView) searchitem.getActionView();


        MenuItemCompat.setOnActionExpandListener(searchitem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                findViewById(R.id.add_items_container).setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                findViewById(R.id.add_items_container).setVisibility(View.VISIBLE);
                todoadaptDone.stopFiltering();
                todoadaptUndone.stopFiltering();
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
                todoadaptUndone.stopFiltering();
                todoadaptDone.stopFiltering();
                todoadaptDone.getFilter().filter(newText);
                todoadaptUndone.getFilter().filter(newText);
                checkedtodos.setAdapter(todoadaptDone);
                uncheckedtodos.setAdapter(todoadaptUndone);

                return false;
            }
        });
        return true;
    }

    /**
     * Initializes the Observer object and sets it's onNext method to update the 2 adapters
     * according to the update operation
     *
     */
    private void initObserver() {


        Observer<TodoUpdate> myObserver = new Observer<TodoUpdate>() {


            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(TodoUpdate todoUpdate) {
                if (todoUpdate.getOperation() == todoUpdate.ADD)

                    if (todoUpdate.getTodo().isDone())
                        todoadaptDone.add(todoUpdate);
                    else
                        todoadaptUndone.add(todoUpdate);
                else {
                    if (todoUpdate.getTodo().isDone()) {
                        todoadaptUndone.remove(todoUpdate);
                        todoadaptDone.add(todoUpdate);

                    } else {
                        todoadaptDone.remove(todoUpdate);
                        todoadaptUndone.add(todoUpdate);

                    }
                }
                checkedtodos.setAdapter(todoadaptDone);
                uncheckedtodos.setAdapter(todoadaptUndone);

            }

            @Override
            public void onCompleted() {

            }

        };


        subscribeToTodoUpdates();
        TodoUpdatesObservable.subscribe(myObserver);
    }

    /**
     * Initializes connection with database
     */
    private void initDatabaseConnection() {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("todoItems");


    }

    /**
     * Inserts a to do item into the database for the first time
     * with a done value of false
     * @param todo A string that represents a todo item
     */

    private void insertTodoDB(String todo) {


        DatabaseReference childRef = myRef.push();


        Todo td = new Todo(todo, false);

        childRef.child("todoi").setValue(td);


    }




    /**
     * Updates the value of done for a specific to do entry
     * @param ref A string representing the reference of the entry in the database
     * @param done A boolean representing the new value of done in the targeted to do
     */
    public void updateTodoDoneDB(String ref, boolean done) {
        myRef.child(ref).child("todoi").child("done").setValue(String.valueOf(done));
    }

    /**
     * Listens for clicks on the add item button and generates a new to do item and inserts it to the database
     * @param view The view associated with the listener
     */

    public void addItemButtonListener(View view) {
        EditText todoTextView = (EditText) findViewById(R.id.add_todo_text);
        String todo = todoTextView.getText().toString();
        if (!todo.isEmpty()) {
            todoTextView.setText("");
            insertTodoDB(todo);
        }

    }

    /**
     *Creates an observable stream of to do updates out of the database reference and notifies the observer of
     * any relevant change in the database
     * @return The Observable object created
     */

    public Observable<TodoUpdate> subscribeToTodoUpdates() {
        if (TodoUpdatesObservable == null) {
            TodoUpdatesObservable = Observable.create(new Observable.OnSubscribe<TodoUpdate>() {

                @Override
                public void call(final Subscriber<? super TodoUpdate> subscriber) {
                    TodoUpdatesListener = myRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            final Todo todo = convertDataSnapShotToTodo(dataSnapshot);
                            subscriber.onNext(new TodoUpdate(todo, TodoUpdate.ADD));
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            final Todo todo = convertDataSnapShotToTodo(dataSnapshot);
                            subscriber.onNext(new TodoUpdate(todo, TodoUpdate.UPDATE));

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

    /**
     *Converts a data object received from the database to a corresponding to do update object
     * @param data the data object received from the database
     * @return the corresponding to do update object
     */
    private Todo convertDataSnapShotToTodo(DataSnapshot data) {

        return new Todo(data.child("todoi").child("todo").getValue().toString()
                , Boolean.valueOf(data.child("todoi").child("done").getValue().toString()), data.getKey());

    }

}
