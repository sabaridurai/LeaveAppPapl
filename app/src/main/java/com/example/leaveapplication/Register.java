package com.example.leaveapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {
    public Map<String, String> mqp1 = new HashMap<>();
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    private EditText email, password;
    private Button regbtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            Log.d("Action Bar Hide", e.getMessage());
        }
        //firebase connection
        email = findViewById(R.id.regemail);
        password = findViewById(R.id.regpass);
        regbtn = findViewById(R.id.reg);
        //firebase
        mAuth = FirebaseAuth.getInstance();
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser();
            }

            private void registerUser() {
                String eemailid, epass;
                eemailid = email.getText().toString();
                epass = password.getText().toString();

//                mqp1.put("Email", eemailid);
//                mqp1.put("Password", epass);

                //validations of input
                if (TextUtils.isEmpty(eemailid)) {
                    Toast.makeText(getApplicationContext(), "Please enter your mail id", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(epass)) {
                    Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_LONG).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(eemailid, epass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Check Your Mail and verify", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(Register.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Log.e("Firebase", "Failed to send email verification: " + task.getException());
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
//                if (!(TextUtils.isEmpty(eemailid) && TextUtils.isEmpty(epass))) {
//                    firebaseDatabase = FirebaseDatabase.getInstance();
//                    firebaseAuth = FirebaseAuth.getInstance();
//                    DatabaseReference reference = firebaseDatabase.getReference("Users");
//                    reference.child("registerdata").child(Objects.requireNonNull(firebaseAuth.getUid())).setValue(mqp1).addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "Added successfully", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    Toast.makeText(getApplicationContext(), "Empty Value", Toast.LENGTH_SHORT).show();
//                }
            }


        });
    }
}
