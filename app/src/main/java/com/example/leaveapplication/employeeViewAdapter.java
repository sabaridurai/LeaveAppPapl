package com.example.leaveapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class employeeViewAdapter extends RecyclerView.Adapter<employeeViewAdapter.MyViewHolder> {

    Context context;

    ArrayList<employeeViewData> list;
    OnNoteListener mOnNoteListener;
        public employeeViewAdapter(Context context,ArrayList<employeeViewData> list,OnNoteListener onNoteListener) {
            this.context = context;
        this.mOnNoteListener=onNoteListener;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item,parent,false);

        return new MyViewHolder(view,mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        employeeViewData empdata=list.get(position);


        String empidstr=empdata.getEmpId();
        String empnamestr=empdata.getEmpName();
        String empdepstr=empdata.getEmpDep();

        holder.Id.setText(empidstr);
        holder.Name.setText(empnamestr);
        holder.Dep.setText(empdepstr);


    }

    @Override
    public int getItemCount() {


        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnNoteListener onNoteListener;

        private final TextView Id,Name,Dep;

        public MyViewHolder(@NonNull View itemView,OnNoteListener onNoteListener) {
            super(itemView);


            Id=itemView.findViewById(R.id.EmailShowonprofile);
            Name=itemView.findViewById(R.id.name);
            Dep=itemView.findViewById(R.id.dep);

            itemView.setOnClickListener(this);
            this.onNoteListener=onNoteListener;
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
