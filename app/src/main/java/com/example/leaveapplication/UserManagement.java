package com.example.leaveapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserManagement extends Fragment {



CardView empAddCard,empViewCard,makeHarchycard;
ImageButton empaddImg,empviewImg,makeHarchyimg;
    public UserManagement() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_user_manage, container, false);

empAddCard=(CardView) view.findViewById(R.id.addEmpCard);
empaddImg=(ImageButton) view.findViewById(R.id.addEmpImg);
empViewCard=(CardView) view.findViewById(R.id.viewEmpCard);
empviewImg=(ImageButton) view.findViewById(R.id.viewEmpImg);
makeHarchycard=(CardView)view.findViewById(R.id.harchycard);
makeHarchyimg=(ImageButton)view.findViewById(R.id.harchyimg);



makeHarchyimg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(getContext(),MakeHarchyActivity.class);
        startActivity(intent);
    }
});

makeHarchycard.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(getContext(),MakeHarchyActivity.class);
        startActivity(intent);
    }
});


empaddImg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(getContext(),Addemployee.class);
      intent.putExtra("from","Home");
        startActivity(intent);
    }
});


empAddCard.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(getContext(),Addemployee.class);
        intent.putExtra("from","Home");
        startActivity(intent);

    }
});

empviewImg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(getContext(),ViewEmployee.class);
        startActivity(intent);
    }
});

empViewCard.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(getContext(),ViewEmployee.class);
        startActivity(intent);
    }
});


    return view;

    }
}