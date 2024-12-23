package com.example.leaveapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity  {
//    public DrawerLayout drawerLayout;
//    public ActionBarDrawerToggle actionBarDrawerToggle;

FirebaseAuth firebaseAuth;
FirebaseDatabase firebaseDatabase;
    String mailstr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        try{
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch( NullPointerException e){
            Log.d("Action Bar hide",e.getMessage());
        }
        firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("Users").child("userdata");
        firebaseAuth=FirebaseAuth.getInstance();

        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null) {
            mailstr= user.getEmail();
            mailstr= Objects.requireNonNull(mailstr).replaceAll("@","");
            mailstr=mailstr.replaceAll("\\.","");

//            FirebaseMessaging.getInstance().getToken()
//                    .addOnCompleteListener(new OnCompleteListener<String>() {
//                        @Override
//                        public void onComplete(@NonNull Task<String> task) {
//                            if (!task.isSuccessful()) {
//                                Log.w("Token push to DB", "Fetching FCM registration token failed", task.getException());
//                                return;
//                            }
//
//                            // Get new FCM registration token
//                            String token = task.getResult();
//
//                            databaseReference.child(mailstr).addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    if (snapshot.exists()) {
//
//                                        databaseReference.child(mailstr).child("Token").setValue(token);
//                                        Toast.makeText(HomeActivity.this, "Token set", Toast.LENGTH_LONG).show();
//                                    }
//                                    else {
//                                        Toast.makeText(HomeActivity.this, "Your data did not exists !", Toast.LENGTH_LONG).show();
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//                                    Log.d("Tokenset","Database error");
//                                }
//                            });
//
//
//
//                        }
//                    });






        }



        NavigationView navigationView=findViewById(R.id.navVie);

        View hView =  navigationView.inflateHeaderView(R.layout.nav_header);
        ImageView imgvw = (ImageView)hView.findViewById(R.id.frag);
        imgvw .setImageResource(0);






//        navigationView.setSelectedItemId(R.id.nav_leave);
        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.replace(R.id.frgContent, new LeaveRequestfrg());
        fragmentTransaction1.commit();

//        navigationView.setOnItemSelectedListener(item -> {
navigationView.setNavigationItemSelectedListener(item -> {
    switch (item.getItemId()) {

        case R.id.nav_requests: {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frgContent, new LeaveRequestfrg());
            fragmentTransaction.commit();
            return true;

        }
        case R.id.nav_daybook: {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frgContent, new UserManagement());
            fragmentTransaction.commit();
            return true;
        }
        case R.id.nav_logout:{
            firebaseAuth.signOut();
            Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        default:
            return true;

    }
});
    }

}