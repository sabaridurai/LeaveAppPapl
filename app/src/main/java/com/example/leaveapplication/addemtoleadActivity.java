package com.example.leaveapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class addemtoleadActivity extends AppCompatActivity {

    private TextView viewlead;
    private Button button;
    private  String lead,emp,finalMailid,empmail;
    Map<String,String> mp=new HashMap<>();
    DatabaseReference databaseReference,databaseReferenceforadd;
    FirebaseDatabase firebaseDatabase,firebaseDatabaseforadd;
    private Spinner spinner;
    private static   int j=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addemtolead);


        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (Exception e)
        {
            Log.e("actionbarerror",e.getMessage().toString());
        }

        button=findViewById(R.id.addemptolead);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
        spinner=findViewById(R.id.spinneraddemp);
        viewlead=findViewById(R.id.leadshowemp);
        Intent fromintent=getIntent();
        button.setVisibility(View.INVISIBLE);
        if (fromintent!=null)
        {
            viewlead.setText(fromintent.getStringExtra("lead"));
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("LeadWithEmployee").child(lead).child(empmail).setValue(mp).addOnCompleteListener(task -> {

                    databaseReference.child("userdata").child(lead).child("Email").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                            String leadmail= (String) snapshot.getValue();

                            mp.clear();
                            mp.put("lead", String.valueOf(true));
                            mp.put("LeadID",leadmail);

                            databaseReference.child("havealead").child(empmail).setValue(mp);
                            Toast.makeText(addemtoleadActivity.this,"Added Successfully",Toast.LENGTH_LONG).show();
                            button.setVisibility(View.INVISIBLE);
                            Intent intent=new Intent(addemtoleadActivity.this,addemtoleadActivity.class);
                            intent.putExtra("lead",viewlead.getText().toString());
                            startActivity(intent);
                            finish();}
                            else {
                                Toast.makeText(getApplicationContext(),"Error Occurred Retry",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(),"Error Occurred Retry",Toast.LENGTH_SHORT).show();

                        }
                    });


                });
            }
        });

        ArrayList<String> list=new ArrayList<>();
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(R.layout.spinnerbg);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getSelectedItem().toString().equals("Choose Employee") || (parent.getSelectedItem().toString().equals("All employees are assigned") )))
                {
                    lead=viewlead.getText().toString();
                    lead=lead.replaceAll("\\.","");
                    lead=lead.replaceAll("@","");


                  emp=parent.getSelectedItem().toString();
                    emp=emp.replaceAll("\\.","");
                    emp=emp.replaceAll("@","");
                    finalMailid = lead;
                  empmail=emp;

                    mp.clear();
                    mp.put("Email",parent.getSelectedItem().toString());

                    button.setVisibility(View.VISIBLE);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
         Log.d("addemployee to lead spinner", (String) parent.getSelectedItem());
            }
        });

        //Last change --->add value event to single event

        databaseReference.child("EmployeeCategory").child("Employee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                list.add("Choose Employee");

                for (DataSnapshot ds : snapshot.getChildren()) {


                    String name  = String.valueOf(ds.child("Email").getValue());
                    String mailchild=name;
                    mailchild=mailchild.replaceAll("\\.","");
                    mailchild=mailchild.replaceAll("@","");


                    databaseReference.child("havealead").child(mailchild).child("lead").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            int i=0;
                            if (snapshot.exists())
                            {
                                boolean havelead= Boolean.parseBoolean(snapshot.getValue().toString());
                                if (!(havelead))
                                {
                                    i++;
                                    list.add(name);
                                }
                                if (i==0 && j==0)
                                {
                                    j++;
                                    list.add("All employees are assigned");
                                }

                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Log.d("Havelead get",error.getMessage());
                        }
                    });

                }
                adapter.notifyDataSetChanged();
                spinner.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(addemtoleadActivity.this, "Lead database error", Toast.LENGTH_SHORT).show();

            }

        });
    }
}