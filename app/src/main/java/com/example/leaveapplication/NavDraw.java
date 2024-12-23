package com.example.leaveapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

public abstract class NavDraw extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView txtemail;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    String userEmail;
    private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         txtemail = findViewById(R.id.emailView);
         mAuth = FirebaseAuth.getInstance();
         mFirebaseUser = mAuth.getCurrentUser();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            userEmail = user.getEmail();
        }
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UserRef.child(userEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.hasChild("eemailid")){
                        String eemailid = snapshot.child("eemailid").getValue().toString();
                        txtemail.setText(eemailid);
                    }
                    else{
                        Toast.makeText(NavDraw.this,"Email id is not displayed",Toast.LENGTH_SHORT).show();
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//         txtemail.setText(mAuth.getCurrentUser().getEmail().toString());
//         if(mFirebaseUser != null){
//         }
//         mAuthListener = new FirebaseAuth.AuthStateListener() {
//             @Override
//             public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                 FirebaseUser t
//             }
//         }


    }
}

