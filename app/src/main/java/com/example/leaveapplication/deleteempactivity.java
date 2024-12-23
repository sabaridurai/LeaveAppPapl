package com.example.leaveapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class deleteempactivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    String empmailkey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleteempactivity);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (Exception e)
        {
            Log.d("Actionber",e.getMessage());
        }

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");



        Intent intent=getIntent();
        String mail=intent.getStringExtra("EmployeeMailIdIntent");
        String leadmail= intent.getStringExtra("leadmail");
        if(!(mail.isEmpty() && leadmail.isEmpty()))
        {
            empmailkey=mail.replaceAll("\\.","");
            empmailkey=empmailkey.replaceAll("@","");
            AlertDialog.Builder builder = new AlertDialog.Builder(deleteempactivity.this);


        builder.setMessage("Remove ");
        builder.setTitle("Are you Sure to remove !");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) ->

        {
            databaseReference.child("LeadWithEmployee").child(leadmail).child(empmailkey).removeValue(new DatabaseReference.CompletionListener() {


                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Map<String,String> mpchange=new HashMap<>();
                    mpchange.put("lead", String.valueOf(false));
                    mpchange.put("LeadID","None");
                    databaseReference.child("havealead").child(empmailkey).setValue(mpchange).addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(deleteempactivity.this,"Removed Successfull",Toast.LENGTH_SHORT);
                            Intent intenti=new Intent(deleteempactivity.this,MakeHarchyActivity.class);
                            startActivity(intenti);
                            finish();
                        }
                    });
                }
            });

        });

        builder.setNegativeButton("No", (dialog, which) -> {
            Intent intenti=new Intent(deleteempactivity.this,MakeHarchyActivity.class);
            startActivity(intenti);
            finish();
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


        }
    }
}