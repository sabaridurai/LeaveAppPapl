package com.example.leaveapplication;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MakeHarchyActivity extends AppCompatActivity implements Leademployeeadapter.OnNoteListener {

Leademployeeadapter empviewadapter;
int tempcount=0;
    ArrayList<Leademployeedata> list1;
    public  String UserId,Name1,Des,UMail, leadmail;
    private Spinner spinner;
    private Button addem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_harchy);

        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (Exception e){
            Log.d("ActionBer hide",e.getMessage());
        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");




        addem=findViewById(R.id.addempbtn);
        RecyclerView recyclerView = findViewById(R.id.leademployee);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(MakeHarchyActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        list1=new ArrayList<>();
        empviewadapter=new Leademployeeadapter(MakeHarchyActivity.this,list1,  this);
        recyclerView.setAdapter(empviewadapter);

        ProgressDialog progressDialog = new ProgressDialog(MakeHarchyActivity.this);
        progressDialog.setMessage("Loading...");


        spinner=findViewById(R.id.chooseLeadid);
        ArrayList<String> list=new ArrayList<>();
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(R.layout.spinnerbg);
        databaseReference.child("EmployeeCategory").child("Lead").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                list.clear();
                list.add("Choose lead");
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name  = String.valueOf(ds.child("Email").getValue());
                    list.add(name);
                }
                adapter.notifyDataSetChanged();
                spinner.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MakeHarchyActivity.this, "Lead database error", Toast.LENGTH_SHORT).show();

            }

        });


        //spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                if (!parent.getSelectedItem().toString().equals("Choose lead"))
                {




                    addem.setVisibility(View.VISIBLE);
//                    progressDialog.show();

                    String mailid=parent.getSelectedItem().toString();
                    mailid=mailid.replaceAll("\\.","");
                    mailid=mailid.replaceAll("@","");

//lead mail data
                    leadmail = mailid;
                 databaseReference.child("LeadWithEmployee").child(leadmail).addChildEventListener(new ChildEventListener() {
                     @Override
                     public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                         if (snapshot.exists()) {
                             for (DataSnapshot da : snapshot.getChildren()) {


                                 String key= (String) da.getValue();

                                 assert key != null;
                                 key=key.replaceAll("\\.","");
                                 key=key.replaceAll("@","");

                                    //empmail
                                 String empmailforuserdata = key;



                                 databaseReference.child("userdata").child(empmailforuserdata ).addValueEventListener(new ValueEventListener() {


                                     @SuppressLint("NotifyDataSetChanged")
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                         if (snapshot.exists()) {





                                                     if (snapshot.exists()) {
                                                         UserId = Objects.requireNonNull(snapshot.child("id").getValue()).toString();
                                                         Name1 = Objects.requireNonNull(snapshot.child("Name").getValue()).toString();
                                                         Des = Objects.requireNonNull(snapshot.child("Designation").getValue()).toString();
                                                         UMail = Objects.requireNonNull(snapshot.child("Email").getValue()).toString();


                                                         if (!(UserId.isEmpty() && Name1.isEmpty() && Des.isEmpty())) {
                                                             progressDialog.dismiss();
                                                             Leademployeedata employeeviewdata = new Leademployeedata(UserId, Name1, Des, UMail);
                                                             list1.add(employeeviewdata);
                                                             empviewadapter.notifyDataSetChanged();


                                                         } else {
                                                             progressDialog.dismiss();
                                                             Toast.makeText(MakeHarchyActivity.this, "Did Not get data from DB", Toast.LENGTH_SHORT).show();
                                                         }
                                                     }



                                         }
                                         else {
                                             Toast.makeText(MakeHarchyActivity.this, "Did not get User", Toast.LENGTH_SHORT).show();
                                         }

                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError error) {
                                         progressDialog.dismiss();
                                         Toast.makeText(MakeHarchyActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                                     }
                                 });


                             }
                         }
                         else {
                             Toast.makeText(MakeHarchyActivity.this, "No Data Found, Please Add", Toast.LENGTH_SHORT).show();
                         }
                     }

                     @Override
                     public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                         tempcount++;
                     }

                     @Override
                     public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                         tempcount--;
                     }

                     @Override
                     public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                         tempcount++;
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                         tempcount--;
                     }
                 });

                 addem.setOnClickListener(v -> {
                     Intent intent=new Intent(MakeHarchyActivity.this,addemtoleadActivity.class);
                     intent.putExtra("lead",parent.getSelectedItem().toString());
                     startActivity(intent);
                 });

                }
                list1.clear();
                empviewadapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });







    }

    @Override
    public void onNoteClick(int position) {


        Intent intent = new Intent(MakeHarchyActivity.this, deleteempactivity.class);
        Leademployeedata userData= list1.get(position);
        String intentMail=userData.getempEmail();
        finish();
        if(!(intentMail.isEmpty() && leadmail.isEmpty())) {
            intent.putExtra("EmployeeMailIdIntent", intentMail);
            intent.putExtra("leadmail",leadmail);
        }
        startActivity(intent);

//
    }
}