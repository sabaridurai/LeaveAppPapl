package com.example.leaveapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Leademployeeadapter extends RecyclerView.Adapter<Leademployeeadapter.MyViewHolder>{
    Context context;

    ArrayList<Leademployeedata> list;
  OnNoteListener mOnNoteListener;
    public Leademployeeadapter (Context context, ArrayList<Leademployeedata> list, OnNoteListener onNoteListener) {

        this.context = context;
        this.mOnNoteListener = onNoteListener;
        this.list = list;
    }

    @NonNull


    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.item1,parent,false);

        return new MyViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Leademployeedata data=list.get(position);

        String empidstr=data.getEmpId();
        String empnamestr=data.getEmpName();
        String empdepstr=data.getEmpDep();

        holder.Id.setText(empidstr);
        holder.Name.setText(empnamestr);
        holder.Dep.setText(empdepstr);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

       OnNoteListener onNoteListener;
        private final TextView Id,Name,Dep;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            Id=itemView.findViewById(R.id.EmailShowonprofile1);
            Name=itemView.findViewById(R.id.name1);
            Dep=itemView.findViewById(R.id.dep1);

            itemView.setOnClickListener(this);
            this.onNoteListener= onNoteListener;
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());

        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
