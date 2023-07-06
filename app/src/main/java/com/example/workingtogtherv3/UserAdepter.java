package com.example.workingtogtherv3;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class UserAdepter extends ArrayAdapter<String> {
    private ArrayList<Boolean> checked;
    private ArrayList<String> names;
   // private Context context;
/*
    public UserAdepter(Context context)
    {
        super(context,);
        checked = new ArrayList<Boolean>();
        names = new ArrayList<String>();
    }
    */

    public UserAdepter(@NonNull Context context, int resource, ArrayList<Boolean> checked, ArrayList<String> names) {
        super(context, resource);
        this.checked = checked;
        this.names = names;
    }

    public void add(String name)
    {
        names.add(name);
        checked.add(false);
    }
    public boolean get(int i)
    {
        return checked.get(i);
    }
    public  void set(int i,boolean check )
    {
        checked.set(i,check);
    }

    @Override
    public View getView(int positon, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_cell,parent,false);
        TextView textView = convertView.findViewById(R.id.tvFirstName);
        CheckBox checkView = convertView.findViewById(R.id.checkbox);
        textView.setText(names.get(positon));
        checkView.setChecked(checked.get(positon));
        checkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checked.set(positon,!checked.get(positon));
            }
        });
        return convertView;
    }

}
