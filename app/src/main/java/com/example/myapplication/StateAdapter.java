package com.example.myapplication;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class StateAdapter extends ArrayAdapter<TraksList>{
    private LayoutInflater inflater;
    private int layot;
    private List<TraksList> Traks;
    private ImageView ex;
    public StateAdapter(Context context, int Resource, List<TraksList> traks){
        super(context, Resource, traks);
        this.Traks = traks;
        this.layot = Resource;
        this.inflater=LayoutInflater.from(context);
    }
    public View getView(int Position,View convertView, ViewGroup parent){
        View view=inflater.inflate(this.layot, parent, false);
        TextView nameView = view.findViewById(R.id.name);
        ex=view.findViewById(R.id.imageView2);
        ex.setImageResource(R.drawable.a3);
        TraksList traks = Traks.get(Position);
        nameView.setText(traks.getName());
        return view;
    }

    }



