package com.example.leaveapplication;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static java.util.Objects.requireNonNull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class Attendance extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    Workbook workbook = new HSSFWorkbook();
    TextView chooseDate;
    ImageButton datecal;
    Button btnAtt;
    String currentmail,fromdate,fromMonth,fromYear,rfrom;
    File dir;

    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child("userdata");
    int rownum=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        try {
            requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            Log.d("Action Bar hide", e.getMessage());
        }
        // Request permission to write to external storage
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        //firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        chooseDate = findViewById(R.id.reportDate);
        datecal = findViewById(R.id.fromcal);
        btnAtt = findViewById(R.id.Attbtn);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Attendance");

        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        currentmail = user.getEmail();

        //Date picker for from date
        datecal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Attendance.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String monthtemp = String.format("%02d", month + 1);
                        String daytemp = String.format("%02d", dayOfMonth);
                        fromYear = String.valueOf(year);
                        fromdate = String.valueOf(daytemp);
                        fromMonth = String.valueOf(monthtemp);
                        chooseDate.setText(year + "-" + monthtemp + "-" + daytemp);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        btnAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rfrom = chooseDate.getText().toString();
                ProgressDialog progressDialog = new ProgressDialog(Attendance.this);
                progressDialog.setMessage("Processing..");
                progressDialog.show();
                if(!rfrom.isEmpty()){
                    dir = new File(getExternalFilesDir(DIRECTORY_DOWNLOADS), "Attendance Report");
                    String fileName = chooseDate.getText().toString() + ".xls";
                    File file1 = new File(dir, fileName);
                    Sheet sheet = workbook.createSheet("Attendance Report");
                    if(!dir.exists()){
                        dir.mkdirs();


                        if(!fromYear.isEmpty() && !fromMonth.isEmpty() && !fromdate.isEmpty()) {

                            reference.child(fromYear).child(fromMonth).child(fromdate).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {


                                        Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                                        int counter = 0;
                                        while (items.hasNext()) {
                                            DataSnapshot item = items.next();
                                            String empid = item.getKey();
                                            assert empid != null;
                                            empid = empid.replaceAll("@", " ");
                                            empid = empid.replaceAll("//", "");
                                            String finalempid = empid;


                                            reference1.child(finalempid).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        String name = snapshot.getValue().toString();
                                                        Log.d("Att", name);
                                                        reference.child(fromYear).child(fromMonth).child(fromdate).child(finalempid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                HashMap<String, String> map = (HashMap<String, String>) snapshot.getValue();
//
                                                                String checkinAdd = map.get("CheckInAddress").toString();
                                                                String checkinlat = map.get("CheckInLati").toString();
                                                                String checkinlong = map.get("CheckInLongi").toString();
                                                                String checkintime = map.get("CheckInTime").toString();
                                                                String checkoutAdd = map.get("CheckOutAddress").toString();
                                                                String checkoutlat = map.get("CheckOutLati").toString();
                                                                String checkoutlong = map.get("CheckOutLongi").toString();
                                                                String checkouttime = map.get("CheckOutTime").toString();

                                                                //save the excel file

                                                                //create the header row
                                                                Row row = sheet.createRow(0);


                                                                Cell cellname = row.createCell(1);
                                                                cellname.setCellValue("NAME");
                                                                Cell cell = row.createCell(2);
                                                                cell.setCellValue("CHECKIN_ADDRESS");
                                                                Cell cell1 = row.createCell(3);
                                                                cell1.setCellValue("CHECKIN_TIME");
                                                                Cell cell2 = row.createCell(4);
                                                                cell2.setCellValue("CHECKIN_LATITUDE");
                                                                Cell cell3 = row.createCell(5);
                                                                cell3.setCellValue("CHECKIN_LONGITUDE");
                                                                Cell cell4 = row.createCell(6);
                                                                cell4.setCellValue("CHECKOUT_ADDRESS");
                                                                Cell cell5 = row.createCell(7);
                                                                cell5.setCellValue("CHECKOUT_TIME");
                                                                Cell cell6 = row.createCell(8);
                                                                cell6.setCellValue("CHECKOUT_LATITUDE");
                                                                Cell cell7 = row.createCell(9);
                                                                cell7.setCellValue("CHECKOUT_LONGITUDE");


                                                                //for Values
                                                                Row dataRow = sheet.createRow(++rownum);
                                                                dataRow.createCell(1).setCellValue(name);
                                                                dataRow.createCell(2).setCellValue(checkinAdd);
                                                                dataRow.createCell(3).setCellValue(checkintime);
                                                                dataRow.createCell(4).setCellValue(checkinlat);
                                                                dataRow.createCell(5).setCellValue(checkinlong);
                                                                dataRow.createCell(6).setCellValue(checkoutAdd);
                                                                dataRow.createCell(7).setCellValue(checkouttime);
                                                                dataRow.createCell(8).setCellValue(checkoutlat);
                                                                dataRow.createCell(9).setCellValue(checkoutlong);
                                                                progressDialog.dismiss();
                                                                try {
                                                                    OutputStream outputStream1 = Files.newOutputStream(file1.toPath());
                                                                    workbook.write(outputStream1);
                                                                    outputStream1.flush();
                                                                    outputStream1.close();
                                                                    Toast.makeText(Attendance.this, "Please wait generating....", Toast.LENGTH_LONG).show();
                                                                } catch (
                                                                        IOException e) {
                                                                    e.printStackTrace();
                                                                    Log.d("error++", e.getMessage());
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        }


                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(Attendance.this,"No Data found",Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }


                    }
                    else{
                        if(!fromYear.isEmpty() && !fromMonth.isEmpty() && !fromdate.isEmpty()) {

                            reference.child(fromYear).child(fromMonth).child(fromdate).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {


                                            Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                                            int counter = 0;
                                            while (items.hasNext()) {
                                                DataSnapshot item = items.next();
                                                String empid = item.getKey();
                                                assert empid != null;
                                                empid = empid.replaceAll("@", " ");
                                                empid = empid.replaceAll("//", "");
                                                String finalempid = empid;


                                                reference1.child(finalempid).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            String name = snapshot.getValue().toString();
                                                            Log.d("Att", name);
                                                            reference.child(fromYear).child(fromMonth).child(fromdate).child(finalempid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    HashMap<String, String> map = (HashMap<String, String>) snapshot.getValue();
//
                                                                    String checkinAdd = map.get("CheckInAddress").toString();
                                                                    String checkinlat = map.get("CheckInLati").toString();
                                                                    String checkinlong = map.get("CheckInLongi").toString();
                                                                    String checkintime = map.get("CheckInTime").toString();
                                                                    String checkoutAdd = map.get("CheckOutAddress").toString();
                                                                    String checkoutlat = map.get("CheckOutLati").toString();
                                                                    String checkoutlong = map.get("CheckOutLongi").toString();
                                                                    String checkouttime = map.get("CheckOutTime").toString();

                                                                    //save the excel file

                                                                    //create the header row
                                                                    Row row = sheet.createRow(0);


                                                                    Cell cellname = row.createCell(1);
                                                                    cellname.setCellValue("NAME");
                                                                    Cell cell = row.createCell(2);
                                                                    cell.setCellValue("CHECKIN_ADDRESS");
                                                                    Cell cell1 = row.createCell(3);
                                                                    cell1.setCellValue("CHECKIN_TIME");
                                                                    Cell cell2 = row.createCell(4);
                                                                    cell2.setCellValue("CHECKIN_LATITUDE");
                                                                    Cell cell3 = row.createCell(5);
                                                                    cell3.setCellValue("CHECKIN_LONGITUDE");
                                                                    Cell cell4 = row.createCell(6);
                                                                    cell4.setCellValue("CHECKOUT_ADDRESS");
                                                                    Cell cell5 = row.createCell(7);
                                                                    cell5.setCellValue("CHECKOUT_TIME");
                                                                    Cell cell6 = row.createCell(8);
                                                                    cell6.setCellValue("CHECKOUT_LATITUDE");
                                                                    Cell cell7 = row.createCell(9);
                                                                    cell7.setCellValue("CHECKOUT_LONGITUDE");


                                                                    //for Values
                                                                    Row dataRow = sheet.createRow(++rownum);
                                                                    dataRow.createCell(1).setCellValue(name);
                                                                    dataRow.createCell(2).setCellValue(checkinAdd);
                                                                    dataRow.createCell(3).setCellValue(checkintime);
                                                                    dataRow.createCell(4).setCellValue(checkinlat);
                                                                    dataRow.createCell(5).setCellValue(checkinlong);
                                                                    dataRow.createCell(6).setCellValue(checkoutAdd);
                                                                    dataRow.createCell(7).setCellValue(checkouttime);
                                                                    dataRow.createCell(8).setCellValue(checkoutlat);
                                                                    dataRow.createCell(9).setCellValue(checkoutlong);
                                                                    progressDialog.dismiss();
                                                                    try {
                                                                        OutputStream outputStream1 = Files.newOutputStream(file1.toPath());
                                                                        workbook.write(outputStream1);
                                                                        outputStream1.flush();
                                                                        outputStream1.close();
                                                                        Toast.makeText(Attendance.this, "Please wait generating....", Toast.LENGTH_LONG).show();
                                                                    } catch (
                                                                            IOException e) {
                                                                        e.printStackTrace();
                                                                        Log.d("error++", e.getMessage());
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            }


                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(Attendance.this,"No Data found",Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                }
            }
        });

    }
}