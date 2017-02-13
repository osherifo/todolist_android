package com.example.omar.todolisttask;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by omar on 2/11/17.
 */

class TodoAdapter implements ListAdapter,Filterable {

    private ArrayList<String> todos;
    private ArrayList<String> ids;
    private ArrayList<Boolean> dones;

    private Context context;

    boolean filtering;
    ArrayList<Integer> showables;

    public TodoAdapter(Context context){
    todos=new ArrayList<String>();
     ids=new ArrayList<String>();
        dones=new ArrayList<Boolean>();

        filtering=false;
        this.context=context;
//        todos.add("lllll");
//        dones.add(true);
//        ids.add("fake key");
    }


    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {

        if(filtering)
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if(filtering)
            position=showables.get(position);

        Log.i("started from the:","BOTTOM");
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View item;
        TextView todo;
        CheckBox cb;

        if (convertView == null) {


        item = inflater.inflate(R.layout.todo_item,null);



        } else {
            item = convertView;
        }



        todo = (TextView) item.findViewById(R.id.todo_item_ctv);
        todo.setText(todos.get(position));
        cb =  (CheckBox) item.findViewById(R.id.todo_checkbox);



        cb.setChecked(dones.get(position));
        cb.setTag(ids.get(position));

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ((MainActivity) context).updateTodoDoneDB(String.valueOf((buttonView).getTag()),isChecked);

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


    public void add(TodoUpdate todoUpdate){

        todos.add(todoUpdate.getTodo().getTodo());
        dones.add(todoUpdate.getTodo().isDone());
        ids.add(todoUpdate.getTodo().getKey());

        Log.d("adapter","add");
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    //TODO: handle during filter
    public void remove(TodoUpdate todoUpdate) {
        int position= ids.indexOf(todoUpdate.getTodo().getKey());

        ids.remove(position);
        todos.remove(position);
        dones.remove(position);
//        boolean found=showables.remove(Integer.valueOf(position));



    }

    @Override
    public Filter getFilter() {
        return new Filter() {



            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results=new FilterResults();

                showables=new ArrayList<Integer>();

                if(constraint!=null&&constraint.length()!=0)

                {
                    filtering=true;
              for (int i=0;i<todos.size();i++)
                  if(todos.get(i).contains(constraint))
                      showables.add(i);

                }
                else{
                    filtering=false;
                    showables=new ArrayList<Integer>();
                }


                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {


            }
        };
    }

    public void stopFiltering(){
        filtering=false;
        showables=new ArrayList<Integer>();
    }
}
