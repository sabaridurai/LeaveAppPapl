package com.example.leaveapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ViewEmployee extends AppCompatActivity implements employeeViewAdapter.OnNoteListener {
RecyclerView recyclerView;

employeeViewAdapter empviewadapter;

    ArrayList<employeeViewData> list;
FirebaseDatabase firebaseDatabase;
FirebaseAuth firebaseAuth;
DatabaseReference databaseReference;
public  String UserId,Name1,Des,UMail;
private long pressedTime;
int times=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_employee);


        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException e)
        {
            Log.d("actionbarhide",e.getMessage());
        }

        ProgressDialog progressDialog = new ProgressDialog(ViewEmployee.this);
        progressDialog.setMessage("Loading...");

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users").child("userdata");

        recyclerView=findViewById(R.id.viewemployeerecycle);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(ViewEmployee.this);
        recyclerView.setLayoutManager(layoutManager);

        list=new ArrayList<>();
        empviewadapter=new employeeViewAdapter(ViewEmployee.this,list, this);


        recyclerView.setAdapter(empviewadapter);



        progressDialog.show();

        try {


            databaseReference.addValueEventListener(new ValueEventListener() {


                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (times != 1) {
                            Intent intent = new Intent(ViewEmployee.this, ViewEmployee.class);
                            startActivity(intent);
                            finish();
                        } else {
                            times++;
                            for (DataSnapshot datasnap : snapshot.getChildren()) {


                                UserId = datasnap.child("id").getValue().toString();
                                Name1 = datasnap.child("Name").getValue().toString();
                                Des = datasnap.child("Designation").getValue().toString();
                                UMail = datasnap.child("Email").getValue().toString();


                                if (!(UserId.isEmpty() && Name1.isEmpty() && Des.isEmpty())) {
                                    progressDialog.dismiss();
                                    employeeViewData employeeviewdata = new employeeViewData(UserId, Name1, Des, UMail);
                                    list.add(employeeviewdata);
                                    empviewadapter.notifyDataSetChanged();


                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(ViewEmployee.this, "Did Not get data from DB", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    } else {
                        Toast.makeText(ViewEmployee.this, "Did not get User", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(ViewEmployee.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }
    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        }
        else {
         Intent intent=new Intent(this,HomeActivity.class);
         startActivity(intent);
         finish();

        }
        pressedTime = System.currentTimeMillis();
    }




    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(ViewEmployee.this, Userprofile.class);
        employeeViewData userData= list.get(position);
       String intentMail=userData.getempEmail();
        intent.putExtra("MailIdIntent",intentMail);
        startActivity(intent);
//        Toast.makeText(this, position, Toast.LENGTH_SHORT).show();
    }
}