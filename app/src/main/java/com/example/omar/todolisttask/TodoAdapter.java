package com.example.omar.todolisttask;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * The class responsible for adapting data from the database to views representable in a list in the UI
 */

class TodoAdapter implements ListAdapter, Filterable {

    private ArrayList<String> todos;
    private ArrayList<String> ids;
    private ArrayList<Boolean> dones;

    private Context context;

    boolean filtering;
    ArrayList<Integer> showables;

    /**
     * The constructor for the class initializing empty sets of to do items,their done statuses, their ids,and whether
     * a variable representing whether filtering is active or not as well as an empty set that will represent the indices
     * of items to be visible if filtering is active
     * @param context The activity calling this adapter
     */

    public TodoAdapter(Context context) {
        todos = new ArrayList<String>();
        ids = new ArrayList<String>();
        dones = new ArrayList<Boolean>();

        filtering = false;
        this.context = context;
    }


    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    /**
     * Returns the count of views to be shown in the list
     * @return The count of the items to be shown
     */
    @Override
    public int getCount() {

        if (filtering)
            return showables.size();
        return todos.size();
    }

    @Override
    public Object getItem(int position) {
        return todos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     *The method responsible for creating a view at a specified index of the list view
     * ,inflates a predefined layout and adds a listener to the checkbox for taking appropriate action
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (filtering)
            position = showables.get(position);


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View item;
        TextView todo;
        CheckBox cb;

        if (convertView == null) {


            item = inflater.inflate(R.layout.todo_item, null);


        } else {
            item = convertView;
        }


        todo = (TextView) item.findViewById(R.id.todo_item_ctv);
        todo.setText(todos.get(position));
        cb = (CheckBox) item.findViewById(R.id.todo_checkbox);


        cb.setChecked(dones.get(position));
        cb.setTag(ids.get(position));

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ((MainActivity) context).updateTodoDoneDB(String.valueOf((buttonView).getTag()), isChecked);

            }
        });


        return item;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return todos.isEmpty();
    }

    /**
     *Adds an entry to be displayed in the list
     * @param todoUpdate An object containing the parameters for a single to do entry
     */
    public void add(TodoUpdate todoUpdate) {

        todos.add(todoUpdate.getTodo().getTodo());
        dones.add(todoUpdate.getTodo().isDone());
        ids.add(todoUpdate.getTodo().getKey());
        if (filtering)
            showables.add(todos.size() - 1);


    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    /**
     *Removes an entry from the list
     * @param todoUpdate An object containing the parameters for a single to do entry
     */
    public void remove(TodoUpdate todoUpdate) {
        int position = ids.indexOf(todoUpdate.getTodo().getKey());

        ids.remove(position);
        todos.remove(position);
        dones.remove(position);
        boolean found;
        if (filtering) {
            found = showables.remove(Integer.valueOf(position));
            for (int i = 0; i < showables.size(); i++)
                if (showables.get(i) > position)
                    showables.set(i, showables.get(i) - 1);
        }


    }

    /**
     *Creates a new filter that finds to dos containing the search text and adds their indices
     * to the list of items to be shown
     * @return The created filter
     */
    @Override
    public Filter getFilter() {
        return new Filter() {


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                showables = new ArrayList<Integer>();

                if (constraint != null && constraint.length() != 0)

                {
                    filtering = true;
                    for (int i = 0; i < todos.size(); i++)
                        if (todos.get(i).contains(constraint))
                            showables.add(i);

                } else {
                    filtering = false;
                    showables = new ArrayList<Integer>();
                }


                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {


            }
        };
    }

    /**
     *resets the parameters for filtering
     */
    public void stopFiltering() {
        filtering = false;
        showables = new ArrayList<Integer>();
    }
}
