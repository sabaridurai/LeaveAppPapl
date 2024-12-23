package com.example.leaveapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String mail, key;
    EditText email,pass;
    boolean text=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            Log.d("Action Bar Hide", e.getMessage());
        }

        setContentView(R.layout.activity_login);
        // login page code
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();

FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
DatabaseReference databaseReference=firebaseDatabase.getReference("Users");


        email = findViewById(R.id.emailId);
        pass = findViewById(R.id.loginpasstxt);
        TextView forgotpass = findViewById(R.id.forgotpass);
        TextView signupbtn = findViewById(R.id.register);


        signupbtn.setOnClickListener(view -> {
            Intent intent1 = new Intent(LoginActivity.this,Register.class);
            startActivity(intent1);
            finish();
        });
        forgotpass.setOnClickListener(view -> {
            mail = email.getText().toString();
            if (!mail.isEmpty()) {
                Task<Void> authResultTask = mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "Enter Mail Id", Toast.LENGTH_SHORT).show();
            }
        });
        Button loginbtn = findViewById(R.id.loginbtn);
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            loginbtn.setOnClickListener(view -> {
                mail = email.getText().toString();
                key = pass.getText().toString();

                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    if (!TextUtils.isEmpty(mail) && !TextUtils.isEmpty(key)) {
                        progressDialog.setMessage("Login...");
                        progressDialog.show();


                        String repl=mail.replaceAll(".","");
                        repl=repl.replaceAll("@","");
                        databaseReference.child("Admincanlogin").child(repl).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                                {
                                    String email= (String) dataSnapshot.child("Email").getValue();
                                    assert email != null;
                                    text= email.equals(mail);
                                    Log.d("test123", String.valueOf(text));

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


if(text) {


    Task<AuthResult> authResultTask = mAuth.signInWithEmailAndPassword(mail, key).addOnCompleteListener(task -> {
        if (task.isSuccessful()) {



                progressDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

                startActivity(intent);
                finish();

        } else {
            if (mAuth != null) {
                mAuth.signOut();
            }
            Toast.makeText(LoginActivity.this, task.toString(), Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();
    });

}else {
    Toast.makeText(LoginActivity.this, "Unauthorized User !", Toast.LENGTH_SHORT).show();
    progressDialog.dismiss();
}
                    } else {
                        Toast.makeText(LoginActivity.this, "Empty Value", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(LoginActivity.this, "Please Connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}










