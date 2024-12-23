package com.example.leaveapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;

public class SplashScreen extends AppCompatActivity {
    FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch( NullPointerException e){
            Log.d("Action Bar hide",e.getMessage());
        }
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth != null){
            currentUser = firebaseAuth.getCurrentUser();
        }
       //firebaseAuth.signOut();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(() -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user == null){
                Intent intent = new Intent(SplashScreen.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent mainIntent = new Intent(SplashScreen.this,HomeActivity.class );
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        }, 4000);
    }
}

