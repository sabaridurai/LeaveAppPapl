package com.example.leaveapplication;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static java.util.Objects.requireNonNull;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GenerateReport extends AppCompatActivity {


    private ImageButton frombtn, tobtn;
    private TextView fdate, tdate;
    private Button fullreport,halfrep,perrep;
    private Spinner spinner;
    private String rtype, rfrom, rto, currentmail, fromdate, fromMonth, fromYear, todate, toMonth, toYear;
    private FirebaseAuth mAuth;
    public Map<String, String> mqp3 = new HashMap<>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference, referenceuser;
    ArrayAdapter<CharSequence> adapter;
    File dir,dir1,dir2;
    int cellCount = 0;
    int startrow=1;
    int rownum=startrow;
    Workbook workbook = new XSSFWorkbook();
    Workbook workbook1 = new XSSFWorkbook();
    Workbook workbook2 = new XSSFWorkbook();
    List<String> empList=new ArrayList<String>();
    List<String> datesInRange=new ArrayList<>();
    HashSet<String> testcount=new HashSet<>();
    HashSet<String> testcountfullday=new HashSet<>();
    String fromchoose,tochoose,fileName;
    File file1,file2,file3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_report);


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

        //spinner = findViewById(R.id.reportType);
        fdate = findViewById(R.id.fromDate);
        tdate = findViewById(R.id.toDate);
        frombtn = findViewById(R.id.fromcal);
        tobtn = findViewById(R.id.tocal);
        fullreport = findViewById(R.id.btnReport);
        halfrep = findViewById(R.id.btnhalfday);
        perrep = findViewById(R.id.btnperission);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Leave");
        referenceuser = firebaseDatabase.getReference("Users");

        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        currentmail = user.getEmail();



    }

    @Override
    protected void onStart() {
        super.onStart();




        frombtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(GenerateReport.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String monthtemp = String.format("%02d", month + 1);
                        String daytemp = String.format("%02d", dayOfMonth);
                        fromYear = String.valueOf(year);
                        fromdate = String.valueOf(daytemp);
                        fromMonth = String.valueOf(monthtemp);
                        fromchoose=year + "-" + monthtemp + "-" + daytemp;
                        fdate.setText(year + "/" + monthtemp + "/" + daytemp);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        //Date picker for to date
        tobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(GenerateReport.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String monthtemp = String.format("%02d", month + 1);
                        String daytemp = String.format("%02d", dayOfMonth);
                        toYear = String.valueOf(year);
                        todate = String.valueOf(daytemp);
                        toMonth = String.valueOf(monthtemp);
                        tochoose=year + "-" + monthtemp + "-" + daytemp;
                        tdate.setText(year + "/" + monthtemp + "/" + daytemp);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis()));
                datePickerDialog.show();
            }
        });
        fullreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                empList.clear();
                datesInRange.clear();
                testcount.clear();
                testcountfullday.clear();
                rfrom = fdate.getText().toString();
                rto = tdate.getText().toString();
                fromchoose=rfrom.replaceAll("/","-");
                tochoose=rto.replaceAll("/","-");
                dir = new File(getExternalFilesDir(DIRECTORY_DOWNLOADS), "Leave_Report_Fullday");
                fileName = fdate.getText().toString().replaceAll("/","_") + ".xlsx";
                file1 = new File(dir, fileName);

                ProgressDialog progressDialog = new ProgressDialog(GenerateReport.this);
                progressDialog.setMessage("Processing..");
                progressDialog.setCancelable(false); // Prevent the dialog from being canceled by the back button
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();


                if(!rfrom.isEmpty() && !rto.isEmpty()) {
                    datesInRange = getDatesInRange(fromchoose, tochoose);



                    Sheet sheet = workbook.createSheet("FullDay");
                    if (!dir.exists())
                    {
                        dir.mkdirs();
                        fun_CollectData(progressDialog,file1,sheet,"fullday");
                    }
                    else {
                        fun_CollectData(progressDialog, file1, sheet,"fullday");
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(GenerateReport.this, "Select Date", Toast.LENGTH_SHORT).show();
                }

            }
        });
        halfrep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                empList.clear();
                datesInRange.clear();
                testcount.clear();
                testcountfullday.clear();
                rfrom = fdate.getText().toString();
                rto = tdate.getText().toString();
                fromchoose=rfrom.replaceAll("/","-");
                tochoose=rto.replaceAll("/","-");
                dir1 = new File(getExternalFilesDir(DIRECTORY_DOWNLOADS), "Leave_Report_Halfday");
                fileName = fdate.getText().toString().replaceAll("/","_") + ".xlsx";
                file2 = new File(dir1, fileName);

                ProgressDialog progressDialog = new ProgressDialog(GenerateReport.this);
                progressDialog.setMessage("Processing..");
                progressDialog.setCancelable(false); // Prevent the dialog from being canceled by the back button
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();


                if(!rfrom.isEmpty() && !rto.isEmpty()) {
                    datesInRange = getDatesInRange(fromchoose, tochoose);



                    Sheet sheet = workbook1.createSheet("Halfday");
                    if (!dir1.exists())
                    {
                        dir1.mkdirs();
                        fun_CollectData(progressDialog,file2,sheet,"halfday");
                    }
                    else {
                        fun_CollectData(progressDialog, file2, sheet,"halfday");
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(GenerateReport.this, "Select Date", Toast.LENGTH_SHORT).show();
                }

            }
        });
        perrep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                empList.clear();
                datesInRange.clear();
                testcount.clear();
                testcountfullday.clear();
                rfrom = fdate.getText().toString();
                rto = tdate.getText().toString();
                fromchoose=rfrom.replaceAll("/","-");
                tochoose=rto.replaceAll("/","-");
                dir2 = new File(getExternalFilesDir(DIRECTORY_DOWNLOADS), "Leave_Report_Permission");
                fileName = fdate.getText().toString().replaceAll("/","_") + ".xlsx";
                file3 = new File(dir2, fileName);

                ProgressDialog progressDialog = new ProgressDialog(GenerateReport.this);
                progressDialog.setMessage("Processing..");
                progressDialog.setCancelable(false); // Prevent the dialog from being canceled by the back button
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();


                if(!rfrom.isEmpty() && !rto.isEmpty()) {
                    datesInRange = getDatesInRange(fromchoose, tochoose);



                    Sheet sheet = workbook2.createSheet("Permission");
                    if (!dir2.exists())
                    {
                        dir2.mkdirs();
                        fun_CollectData(progressDialog,file3,sheet,"permission");
                    }
                    else {
                        fun_CollectData(progressDialog, file3, sheet,"permission");
                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(GenerateReport.this, "Select Date", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void fun_CollectData(ProgressDialog progressDialog, File file1, Sheet sheet,String calledfrom) {
        reference.child("Leaveforleadknown").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("Full",snapshot.getKey());
                for (DataSnapshot sna :snapshot.getChildren()) {
                    String leadid=(String) sna.getKey();
                    if(leadid !=null) {
                        reference.child("Leaveforleadknown").child(leadid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot snap:snapshot.getChildren()) {


                                    empList.add(snap.getRef().toString());


                                }
                                reference.child("LeaveforManagerknown").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot snap: snapshot.getChildren()) {

                                            empList.add(snap.getRef().toString());


                                        }
                                         Log.d("calledfrom",calledfrom);
                                        if(calledfrom.equalsIgnoreCase("fullday")) {
                                            fun_Report_Gen(empList, progressDialog, datesInRange, file1, sheet);
                                        }
                                        else if (calledfrom.equalsIgnoreCase("Halfday")) {
                                            fun_Report_Gen_half(empList, progressDialog, datesInRange, file1, sheet);

                                        }
                                        else {
                                            if (calledfrom.equalsIgnoreCase("Permission"))
                                            {
                                                fun_Report_Gen_permission(empList, progressDialog, datesInRange, file1, sheet);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



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

    private void fun_Report_Gen_permission(List<String> empList, ProgressDialog progressDialog, List<String> datesInRange, File file, Sheet sheet) {


        HashSet < String > uniqueSet = new HashSet<>();


        for (String element : empList) {
            uniqueSet.add(element);
        }

        // Convert the HashSet back to an ArrayList if needed
        List<String> uniqueList = new ArrayList<>(uniqueSet);
        Log.d("Permission", String.valueOf(uniqueSet.size()));

        for (String date:datesInRange) {


            for (String uniqueElement : uniqueList) {
//            Log.d("Full",uniqueElement+" List");

                date=date.replaceAll("-","/");
                Log.d("Permission",date+" "+uniqueElement);
                DatabaseReference referencereport=firebaseDatabase.getReferenceFromUrl(uniqueElement);
                referencereport.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            if (!testcount.contains(snapshot.getRef().toString())){
                                testcount.add(snapshot.getRef().toString());
                                Log.d("Rep1","Snap is Exists"+snapshot.getRef());
                                for (String path:testcount) {
                                    DatabaseReference referencereport1=firebaseDatabase.getReferenceFromUrl(path);
                                    referencereport1.child("Permission").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists())
                                            {

                                                                                                String mail = snapshot.child("Email").getValue().toString();
                                                                                                String from = snapshot.child("From Date").getValue().toString();
                                                                                                String to = snapshot.child("Time").getValue().toString();
                                                                                                String nodays = snapshot.child("Time of Permission").getValue().toString();
                                                                                                String cause = snapshot.child("Cause of Permission").getValue().toString();

                                                                                                Log.d("Value", mail +" "+ from + nodays +" "+ cause);

                                                                                                //save the excel file

                                                                                                //create the header row
                                                                                                Row row = sheet.createRow(0);
                                                                                                Cell cell = row.createCell(1);
                                                                                                cell.setCellValue("EMAIL");
                                                                                                Cell cell1 = row.createCell(2);
                                                                                                cell1.setCellValue("FROM DATE");
                                                                                                Cell cell2 = row.createCell(3);
                                                                                                cell2.setCellValue("TIME");
                                                                                                Cell cell3 = row.createCell(4);
                                                                                                cell3.setCellValue("TIME OF PERMISSION");
                                                                                                Cell cell4 = row.createCell(5);
                                                                                                cell4.setCellValue("CAUSE OF PERMISSION");

                                                                                                //for Values
                                                                                                Row dataRow = sheet.createRow(rownum++);
                                                                                                dataRow.createCell(1).setCellValue(mail);
                                                                                                dataRow.createCell(2).setCellValue(from);
                                                                                                dataRow.createCell(3).setCellValue(to);
                                                                                                dataRow.createCell(4).setCellValue(nodays);
                                                                                                dataRow.createCell(5).setCellValue(cause);
                                                                                                progressDialog.dismiss();
                                                                                                try {
                                                                                                    OutputStream outputStream1 = Files.newOutputStream(file.toPath());
                                                                                                    workbook2.write(outputStream1);
                                                                                                    outputStream1.flush();
                                                                                                    outputStream1.close();
                                                                                                    Toast.makeText(GenerateReport.this, "Generating... ", Toast.LENGTH_LONG).show();
                                                                                                }
                                                                                                catch (
                                                                                                        IOException e) {
                                                                                                    e.printStackTrace();
                                                                                                    Toast.makeText(GenerateReport.this,"Error",Toast.LENGTH_LONG).show();
                                                                                                    Log.d("error++", e.getMessage());
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

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });







            }
            Log.d("Full","++++++++++++++++++++++++++++++++\n"+uniqueList.size());


        }
    }

    private void fun_Report_Gen_half(List<String> empList, ProgressDialog progressDialog, List<String> datesInRange, File file, Sheet sheet) {
        HashSet < String > uniqueSet = new HashSet<>();


        for (String element : empList) {
            uniqueSet.add(element);
        }

        // Convert the HashSet back to an ArrayList if needed
        List<String> uniqueList = new ArrayList<>(uniqueSet);
        Log.d("Half", String.valueOf(uniqueSet.size()));

        for (String date:datesInRange) {


            for (String uniqueElement : uniqueList) {
//            Log.d("Full",uniqueElement+" List");

                date=date.replaceAll("-","/");
                Log.d("Half",date+" "+uniqueElement);
                DatabaseReference referencereport=firebaseDatabase.getReferenceFromUrl(uniqueElement);
                referencereport.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            if (!testcount.contains(snapshot.getRef().toString())){
                                testcount.add(snapshot.getRef().toString());
                                Log.d("Rep1","Snap is Exists"+snapshot.getRef());
                                for (String path:testcount) {
                                    DatabaseReference referencereport1=firebaseDatabase.getReferenceFromUrl(path);
                                    referencereport1.child("Halfday").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists())
                                            {
                                                if (!testcountfullday.contains(snapshot.getRef().toString())) {
                                                    testcountfullday.add(snapshot.getRef().toString());

                                                    for (String referenceforpath2:testcountfullday) {
                                                        Log.d("Rep2", "Snap is Exists" + snapshot.getRef());

                                                        DatabaseReference referencereport2=firebaseDatabase.getReferenceFromUrl(referenceforpath2);



                                                        referencereport2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()) {
                                                                    for (DataSnapshot NormalLopSnap : snapshot.getChildren()) {
                                                                        String leaveTypeforpath = NormalLopSnap.getKey();
                                                                        assert leaveTypeforpath != null;
                                                                        Log.d("=*1", leaveTypeforpath);
                                                                        referencereport2.child(leaveTypeforpath).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()) {
                                                                                    for (DataSnapshot NormalOrLop : snapshot.getChildren()) {
                                                                                        String NormalorLopPath = NormalOrLop.getKey();
                                                                                        assert NormalorLopPath != null;
                                                                                        Log.d("=-1", NormalorLopPath);
                                                                                        ArrayList<HashMap<String,String>>dataList = new ArrayList<>();
                                                                                        referencereport2.child(leaveTypeforpath).child(NormalorLopPath).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                                                                                String mail = snapshot.child("Email").getValue().toString();
                                                                                                String from = snapshot.child("From Date").getValue().toString();
                                                                                                String to = snapshot.child("Type of Leave").getValue().toString();
                                                                                                String nodays = snapshot.child("DayNoon").getValue().toString();
                                                                                                String cause = snapshot.child("Cause of Leave").getValue().toString();

                                                                                                Log.d("Value", mail +" "+ from + nodays +" "+ cause);

                                                                                                //save the excel file

                                                                                                //create the header row
                                                                                                Row row = sheet.createRow(0);
                                                                                                Cell cell = row.createCell(1);
                                                                                                cell.setCellValue("EMAIL");
                                                                                                Cell cell1 = row.createCell(2);
                                                                                                cell1.setCellValue("FROM DATE");
                                                                                                Cell cell2 = row.createCell(3);
                                                                                                cell2.setCellValue("LEAVE TYPE");
                                                                                                Cell cell3 = row.createCell(4);
                                                                                                cell3.setCellValue("DAYNOON");
                                                                                                Cell cell4 = row.createCell(5);
                                                                                                cell4.setCellValue("CAUSE OF LEAVE");

                                                                                                //for Values
                                                                                                Row dataRow = sheet.createRow(rownum++);
                                                                                                dataRow.createCell(1).setCellValue(mail);
                                                                                                dataRow.createCell(2).setCellValue(from);
                                                                                                dataRow.createCell(3).setCellValue(to);
                                                                                                dataRow.createCell(4).setCellValue(nodays);
                                                                                                dataRow.createCell(5).setCellValue(cause);
                                                                                                progressDialog.dismiss();
                                                                                                try {
                                                                                                    OutputStream outputStream1 = Files.newOutputStream(file.toPath());
                                                                                                    workbook1.write(outputStream1);
                                                                                                    outputStream1.flush();
                                                                                                    outputStream1.close();
                                                                                                    Toast.makeText(GenerateReport.this, "Generating... ", Toast.LENGTH_LONG).show();
                                                                                                } catch (
                                                                                                        IOException e) {
                                                                                                    e.printStackTrace();
                                                                                                    Toast.makeText(GenerateReport.this,"Error",Toast.LENGTH_LONG).show();

                                                                                                    Log.d("error++", e.getMessage());
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
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });

                                }


                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });







            }
            Log.d("Full","++++++++++++++++++++++++++++++++\n"+uniqueList.size());


        }
    }

    private void fun_Report_Gen(List<String> empList,ProgressDialog progressDialog,List<String > DateinRange,File file,Sheet sheet) {
        HashSet < String > uniqueSet = new HashSet<>();


        for (String element : empList) {
            uniqueSet.add(element);
        }

        // Convert the HashSet back to an ArrayList if needed
        List<String> uniqueList = new ArrayList<>(uniqueSet);
        Log.d("Full", String.valueOf(uniqueSet.size()));

        for (String date:DateinRange) {


            for (String uniqueElement : uniqueList) {
//            Log.d("Full",uniqueElement+" List");

                date=date.replaceAll("-","/");
                Log.d("Full",date+" "+uniqueElement);
                DatabaseReference referencereport=firebaseDatabase.getReferenceFromUrl(uniqueElement);
                referencereport.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            if (!testcount.contains(snapshot.getRef().toString())){
                                testcount.add(snapshot.getRef().toString());
                                Log.d("Rep1","Snap is Exists"+snapshot.getRef());
                                for (String path:testcount) {
                                    DatabaseReference referencereport1=firebaseDatabase.getReferenceFromUrl(path);
                                    referencereport1.child("Fullday").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists())
                                            {
                                                if (!testcountfullday.contains(snapshot.getRef().toString())) {
                                                    testcountfullday.add(snapshot.getRef().toString());

                                                    for (String referenceforpath2:testcountfullday) {
                                                        Log.d("Rep2", "Snap is Exists" + snapshot.getRef());

                                                        DatabaseReference referencereport2=firebaseDatabase.getReferenceFromUrl(referenceforpath2);



                                                        referencereport2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()) {
                                                                    for (DataSnapshot NormalLopSnap : snapshot.getChildren()) {
                                                                        String leaveTypeforpath = NormalLopSnap.getKey();
                                                                        assert leaveTypeforpath != null;
                                                                        Log.d("=*1", leaveTypeforpath);
                                                                        referencereport2.child(leaveTypeforpath).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()) {
                                                                                    for (DataSnapshot NormalOrLop : snapshot.getChildren()) {
                                                                                        String NormalorLopPath = NormalOrLop.getKey();
                                                                                        assert NormalorLopPath != null;
                                                                                        Log.d("=-1", NormalorLopPath);
                                                                                        ArrayList<HashMap<String,String>>dataList = new ArrayList<>();
                                                                                        referencereport2.child(leaveTypeforpath).child(NormalorLopPath).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                                                                                String mail = snapshot.child("Email").getValue().toString();
                                                                                                String from = snapshot.child("From Date").getValue().toString();
                                                                                                String to = snapshot.child("To Date").getValue().toString();
                                                                                                String nodays = snapshot.child("Noofday").getValue().toString();
                                                                                                String cause = snapshot.child("Cause of Leave").getValue().toString();

                                                                                                Log.d("Value", mail +" "+ from +" "+ to +" "+ nodays +" "+ cause);

                                                                                                //save the excel file

                                                                                                //create the header row
                                                                                                Row row = sheet.createRow(0);
                                                                                                Cell cell = row.createCell(1);
                                                                                                cell.setCellValue("EMAIL");
                                                                                                Cell cell1 = row.createCell(2);
                                                                                                cell1.setCellValue("FROM DATE");
                                                                                                Cell cell2 = row.createCell(3);
                                                                                                cell2.setCellValue("TO DATE");
                                                                                                Cell cell3 = row.createCell(4);
                                                                                                cell3.setCellValue("NO_DAYS");
                                                                                                Cell cell4 = row.createCell(5);
                                                                                                cell4.setCellValue("CAUSE");

                                                                                                //for Values
                                                                                                Row dataRow = sheet.createRow(rownum++);
                                                                                                dataRow.createCell(1).setCellValue(mail);
                                                                                                dataRow.createCell(2).setCellValue(from);
                                                                                                dataRow.createCell(3).setCellValue(to);
                                                                                                dataRow.createCell(4).setCellValue(nodays);
                                                                                                dataRow.createCell(5).setCellValue(cause);
                                                                                                progressDialog.dismiss();
                                                                                                try {
                                                                                                    OutputStream outputStream1 = Files.newOutputStream(file.toPath());
                                                                                                    workbook.write(outputStream1);
                                                                                                    outputStream1.flush();
                                                                                                    outputStream1.close();
                                                                                                    Toast.makeText(GenerateReport.this, "Generating... ", Toast.LENGTH_LONG).show();
                                                                                                } catch (
                                                                                                        IOException e) {
                                                                                                    e.printStackTrace();
                                                                                                    Toast.makeText(GenerateReport.this,"Error",Toast.LENGTH_LONG).show();

                                                                                                    Log.d("error++", e.getMessage());
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
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });

                                }


                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });







            }
            Log.d("Full","++++++++++++++++++++++++++++++++\n"+uniqueList.size());


        }

    }


    public static List<String> getDatesInRange(String startDate, String endDate) {
        List<String> datesInRange = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        LocalDate date = start;
        while (!date.isAfter(end)) {
            datesInRange.add(date.format(DateTimeFormatter.ISO_DATE));
            date = date.plusDays(1);
        }

        return datesInRange;
    }
}