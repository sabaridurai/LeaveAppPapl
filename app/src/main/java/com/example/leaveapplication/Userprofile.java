package com.example.leaveapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Userprofile extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextView Name;
    private TextView departmet;
    private TextView iD;
    private TextView designation;
    private TextView phoNo;
    private TextView joinDate;
    private TextView empType;
    private  TextView emailshow;
    private String finalMailid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);


        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException e)
        {
            Log.d("actionbarhide",e.getMessage());
        }

        Name=findViewById(R.id.Name);
        departmet=findViewById(R.id.dep);
        emailshow = findViewById(R.id.EmailShowonprofile);
        iD=findViewById(R.id.userid);
        designation=findViewById(R.id.Designation);
        phoNo=findViewById(R.id.Phnoid);
        joinDate=findViewById(R.id.Joindate);
        empType=findViewById(R.id.Etype);
        ImageButton editimg = findViewById(R.id.editimgbtn);
        ImageButton deleteimg = findViewById(R.id.deleteimgbtn);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference= firebaseDatabase.getReference("Users");

        try {
            Intent intent=this.getIntent();

            String mail = intent.getStringExtra("MailIdIntent");
                emailshow.setText(mail);
            String mailid= emailshow.getText().toString();
            mailid=mailid.replaceAll("\\.","");
            mailid=mailid.replaceAll("@","");


            finalMailid = mailid;


            databaseReference.child("userdata").child(finalMailid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        Name.setText(Objects.requireNonNull(snapshot.child("Name").getValue()).toString());
                        departmet.setText(Objects.requireNonNull(snapshot.child("Department").getValue()).toString());
                        empType.setText(Objects.requireNonNull(snapshot.child("EmployeeType").getValue()).toString());
                        joinDate.setText(Objects.requireNonNull(snapshot.child("JoiningDate").getValue()).toString());
                        phoNo.setText(Objects.requireNonNull(snapshot.child("Phone").getValue()).toString());
                        designation.setText(Objects.requireNonNull(snapshot.child("Designation").getValue()).toString());
                        iD.setText(Objects.requireNonNull(snapshot.child("id").getValue()).toString());
                    }
                    catch (Exception e)
                    {
                        Intent intenterror=new Intent(Userprofile.this,ViewEmployee.class);
                        startActivity(intenterror);
                        finish();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

deleteimg.setOnClickListener(v -> {
    if(Name !=null && finalMailid!=null && empType!=null) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Userprofile.this);
        builder.setMessage("Are You Conform to Disable !");
        builder.setTitle("Warning !");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) ->
        {
            databaseReference.child("userdata").child(finalMailid).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {


                    databaseReference.child("EmployeeCategory").child(empType.getText().toString()).child(finalMailid).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                            if (empType.equals("Admin") || empType.equals("Manager")) {
                                databaseReference.child("Admincanlogin").child(empType.getText().toString()).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {


                                        finish();
                                    }
                                });
                            }
                            else {
                                finish();
                            }
                        }
                    });



                }
            });

        });

        builder.setNegativeButton("No", (dialog, which) -> {

            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

});

        editimg.setOnClickListener(v -> {
            Intent intent=new Intent(this,Addemployee.class);

            intent.putExtra("from","Userprofile");
            intent.putExtra("Id",iD.getText().toString());
            intent.putExtra("Name",Name.getText().toString());
            intent.putExtra("Date",joinDate.getText().toString());
            intent.putExtra("Desin",designation.getText().toString());
            intent.putExtra("Depart",departmet.getText().toString());
            //intent.putExtra("Role",empType.getText().toString());
            intent.putExtra("Email",emailshow.getText().toString());
            intent.putExtra("Mob",phoNo.getText().toString());

            AlertDialog.Builder builder = new AlertDialog.Builder(Userprofile.this);
            builder.setMessage("Are you sure ?");
            builder.setTitle("Edit Profile!");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", (dialog, which) ->
            {



                        databaseReference.child("EmployeeCategory").child(empType.getText().toString()).child(finalMailid).removeValue(new DatabaseReference.CompletionListener() {

                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                finish();
                                startActivity(intent);
                            }
                        });





            });

            builder.setNegativeButton("No", (dialog, which) -> {

                dialog.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();











        });



    }
}