package com.example.leaveapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Addemployee extends AppCompatActivity {

    ImageView imageView;
    FloatingActionButton button;
private EditText datetext,Editid,Editname,Editdesignation,Editdepartment,Editmail,Editmobile;
    private FirebaseDatabase firebaseDatabase;
    private final Map<String,String> mqp1=new HashMap<>();
private String EmployeeType;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addemployee);

        imageView = findViewById(R.id.frag);
        button = findViewById(R.id.floatingActionButton2);

        button.setOnClickListener(v -> ImagePicker.with(Addemployee.this)
                .crop()
                .compress(1024)
                .maxResultSize(1080,1080)
                .start());

        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            Log.d("Action Bar Hide", e.getMessage());
        }
        ProgressDialog progressDialog = new ProgressDialog(Addemployee.this);

       // ImageView profile = findViewById(R.id.profileimageView);
        datetext=findViewById(R.id.date);
        Button submitbtn = findViewById(R.id.employeeDataSubmit);
        Editid=findViewById(R.id.emailId);
        Editname=findViewById(R.id.Unametxt);
        Editdesignation=findViewById(R.id.Udesignationtxt);
        Editdepartment=findViewById(R.id.Udepartmenttxt);
        Editmail=findViewById(R.id.emailidtxt);
        Editmobile=findViewById(R.id.mobilenumbertxt);

        firebaseDatabase = FirebaseDatabase.getInstance();

        Spinner spinner = findViewById(R.id.employeeType);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(Addemployee.this,R.array.EmployeeRole,android.R.layout.simple_spinner_dropdown_item );
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                       @Override
                                       public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                           EmployeeType=parent.getSelectedItem().toString();
                                       }

                                       @Override
                                       public void onNothingSelected(AdapterView<?> parent) {

                                       }
                                   });

        Intent getint=this.getIntent();
        if (getint!=null)
        {
            String from=getint.getStringExtra("from");
            if(from.equals("Userprofile"))
            {
//                Toast.makeText(this,"from user",Toast.LENGTH_SHORT).show();
                datetext.setText(getint.getStringExtra("Date"));

                //spinner.setText(getint.getStringExtra("Role"));
                Editname.setText(getint.getStringExtra("Name"));
                Editmobile.setText(getint.getStringExtra("Mob"));
                Editdesignation.setText(getint.getStringExtra("Desin"));
                Editdepartment.setText(getint.getStringExtra("Depart"));
                Editmail.setText(getint.getStringExtra("Email"));
                Editid.setText(getint.getStringExtra("Id"));




            }
        }









        submitbtn.setOnClickListener(v -> registerNewUser(progressDialog));








        datetext.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(Addemployee.this, (datePicker, i, i1, i2) -> {
               // datetext.setText(i+"-"+(i1+1)+"-"+i2);
                datetext.setText(   i2+"-"+(i1+1)+"-"+i);
            },day,month,year);
            datePickerDialog.show();
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        Uri uri = data.getData();
        imageView.setImageURI(uri);
    }

    private void registerNewUser(ProgressDialog progressDialog)
    {
        String Eid,Ename,Dataofjoin,Designation,Department,Email,Mobile;
        Ename= Editname.getText().toString();
        Eid = Editid.getText().toString();
        Email = Editmail.getText().toString();
        Department = Editdepartment.getText().toString();
        Designation = Editdesignation.getText().toString();
        Mobile =Editmobile.getText().toString();
        Dataofjoin = datetext.getText().toString();




        //validations of all strings


        String regex = "^[A-Za-z\\d+_.-]+@(.+)$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(Email);
        if(TextUtils.isEmpty(Email) || !matcher.matches()){
            Toast.makeText(getApplicationContext(),"Please Enter Valid Email !",Toast.LENGTH_LONG).show();
        }


        if(TextUtils.isEmpty(Mobile) || Mobile.length()!=10 ){
            Toast.makeText(getApplicationContext(),"Please Enter Valid Mobile No.!",Toast.LENGTH_LONG).show();
        }


        if((TextUtils.isEmpty(Ename) && TextUtils.isEmpty(Eid) && TextUtils.isEmpty(Department) && TextUtils.isEmpty(Designation) && TextUtils.isEmpty(Dataofjoin)
        && TextUtils.isEmpty(Dataofjoin) &&(Email.isEmpty()) &&(Mobile.isEmpty()) && (EmployeeType.isEmpty()))|| (TextUtils.isEmpty(Ename) || TextUtils.isEmpty(Eid) || TextUtils.isEmpty(Department)
        || TextUtils.isEmpty(Designation) || TextUtils.isEmpty(Dataofjoin) || TextUtils.isEmpty(Dataofjoin) || (EmployeeType.isEmpty()) || EmployeeType.equals("Choose Employee Role")))
        {
            Toast.makeText(getApplicationContext(),"Please Enter All Fields!!",Toast.LENGTH_LONG).show();


        }
        else {
            mqp1.put("Name",Ename);
            mqp1.put("id",Eid);
            mqp1.put("Email",Email);
            mqp1.put("Department",Department);
            mqp1.put("Designation",Designation);
            mqp1.put("JoiningDate",Dataofjoin);
            mqp1.put("Phone","+91 "+ Mobile);
            mqp1.put("EmployeeType",EmployeeType);



             progressDialog.setMessage("Please wait...");
                progressDialog.show();

                DatabaseReference reference = firebaseDatabase.getReference("Users");

                String mailid=Editmail.getText().toString();
                mailid=mailid.replaceAll("\\.","");
                mailid=mailid.replaceAll("@","");


                String finalMailid = mailid;
                reference.child("userdata").child(mailid).setValue(mqp1).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        mqp1.clear();
                        mqp1.put("Email",Email);
                        mqp1.put("Token","None");
                       reference.child("EmployeeCategory").child(EmployeeType).child(finalMailid).setValue(mqp1).addOnCompleteListener(task1 -> {
                           if (EmployeeType.equals("Employee"))
                           {
                               mqp1.clear();
                               mqp1.put("lead", String.valueOf(false));
                                   mqp1.put("LeadID","None");
                               reference.child("havealead").child(finalMailid).setValue(mqp1).addOnCompleteListener(task22 ->
                                       Toast.makeText(getApplicationContext(), "Update Successfully", Toast.LENGTH_SHORT).show());
                           }
                           else if (EmployeeType.equals("Admin")) {
                               mqp1.clear();
                               mqp1.put("Email",Email);

                               String mailtemp=Email.replaceAll("@","");
                               mailtemp=mailtemp.replaceAll("\\.","");
                               reference.child("Admincanlogin").child(mailtemp).setValue(mqp1).addOnCompleteListener(task2 ->
                                       Toast.makeText(getApplicationContext(), "Update Successfully", Toast.LENGTH_SHORT).show());

                               
                           }
                           else {
                               Toast.makeText(getApplicationContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
                           }
                       });
                        progressDialog.dismiss();
                        Editname.getText().clear();
                        Editid.getText().clear();
                        Editmail.getText().clear();
                        Editdepartment.getText().clear();
                        Editdesignation.getText().clear();
                        Editmobile.getText().clear();
                        datetext.getText().clear();

                    }
                    else {
                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        }


    }
}