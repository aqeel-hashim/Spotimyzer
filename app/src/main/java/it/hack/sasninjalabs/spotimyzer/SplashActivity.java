package it.hack.sasninjalabs.spotimyzer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.hack.sasninjalabs.spotimyzer.model.User;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Intent mainIntent;

        FirebaseApp.initializeApp(SplashActivity.this);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            mainIntent = new Intent(SplashActivity.this, PriorLoginActivity.class);
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                        /* Create an Intent that will start the Menu-Activity. */

                    startActivity(mainIntent);
                    finish();
                }
            }, 2000);
        }
        else {
            mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            SharedPreferences editor = getSharedPreferences(
                    "SpotymizerAppStorage", Context.MODE_PRIVATE);
            mainIntent.putExtra("IS_RENTER",editor.getBoolean("IS_RENTER", false));
            if(editor.getBoolean("IS_RENTER", false)){
                FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        mainIntent.putExtra("CURRENT_USER", user);
                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                        /* Create an Intent that will start the Menu-Activity. */

                                startActivity(mainIntent);
                                finish();
                            }
                        }, 2000);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{
                FirebaseDatabase.getInstance().getReference("owner").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        mainIntent.putExtra("CURRENT_USER", user);
                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                        /* Create an Intent that will start the Menu-Activity. */

                                startActivity(mainIntent);
                                finish();
                            }
                        }, 2000);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}
