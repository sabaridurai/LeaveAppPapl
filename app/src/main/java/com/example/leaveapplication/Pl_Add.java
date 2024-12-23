package com.example.leaveapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Pl_Add extends AppCompatActivity {
   ImageButton pldays;
   FirebaseAuth mAuth;
   FirebaseDatabase database;
   DatabaseReference reference,reference1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pl_add);
        pldays = findViewById(R.id.addpl);
        pldays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             reference = FirebaseDatabase.getInstance().getReference("Leave");
             reference1 = FirebaseDatabase.getInstance().getReference("Users");
             readAndUpdateValue();
            }
        });
    }
    private void readAndUpdateValue() {
        reference.child("Leaveforcount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot sna : snapshot.getChildren()){
                        String leadid = sna.getKey();
                        leadid = leadid.replaceAll("@", " ");
                        leadid = leadid.replaceAll("//", "");
                        String finalLeadid = leadid;
                        Log.d("**",leadid);
                        reference1.child("userdata").child(finalLeadid).child("Designation").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String emptype = String.valueOf(snapshot.getValue());
                                    if(!emptype.equalsIgnoreCase("GET") && !emptype.equalsIgnoreCase("Det"))
                                    {
                                        reference.child("Leaveforcount").child(finalLeadid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    for(DataSnapshot dsn : snapshot.getChildren()){
                                                        String yeardb = dsn.getKey();
                                                        Log.d("##",yeardb);
                                                        assert yeardb != null;
                                                        reference.child("Leaveforcount").child(finalLeadid).child(yeardb).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if(snapshot.exists()){
                                                                    for(DataSnapshot mnt : snapshot.getChildren()){
                                                                        String typeofleave = mnt.getKey();
                                                                        Log.d("^^",typeofleave);
                                                                        assert typeofleave != null;
                                                                        reference.child("Leaveforcount").child(finalLeadid).child(yeardb).child(typeofleave).child("Privilege Leave").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if(snapshot.exists()){
                                                                                    for(DataSnapshot hdh : snapshot.getChildren()){
                                                                                        String plday = hdh.getKey();
                                                                                        Log.d("@@",plday);
                                                                                        reference.child("Leaveforcount").child(finalLeadid).child(yeardb).child(typeofleave).child("Privilege Leave").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                if(snapshot.exists()){
                                                                                                    GenericTypeIndicator<Map<String, Double>> typeIndicator = new GenericTypeIndicator<Map<String, Double>>() {};
                                                                                                    Map<String, Double> valueMap = snapshot.getValue(typeIndicator);
                                                                                                    if(valueMap !=null){
                                                                                                        Double normalValue = valueMap.get("Normal");
                                                                                                        if(normalValue != null){
                                                                                                            double normal = normalValue +1.25;
                                                                                                            HashMap<String, Double> map = new HashMap<>();
                                                                                                            map.put("Lop", 0d);
                                                                                                            map.put("Normal",(double)normal);
                                                                                                            reference.child("Leaveforcount").child(finalLeadid).child(yeardb).child(typeofleave).child("Privilege Leave").setValue(map);
                                                                                                        }else{
                                                                                                            Toast.makeText(Pl_Add.this, "Normal value is not in double", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                }else {
                                                                                                    HashMap<String,Long> map = new HashMap<>();
                                                                                                    map.put("Lop",0L);
                                                                                                    map.put("Normal", (long) 1.25);
                                                                                                    reference.child("Leaveforcount").child(finalLeadid).child(yeardb).child(typeofleave).child("Privilege Leave").setValue(map);
                                                                                                }
                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                                            }
                                                                                        });

                                                                                    }
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }