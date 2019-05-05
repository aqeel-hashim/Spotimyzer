package it.hack.sasninjalabs.spotimyzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PriorLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prior_login);
    }

    public void renterLogin(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("IS_RENTER", true);
        startActivity(i);
    }


    public void renteeLogin(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("IS_RENTER", false);
        startActivity(i);
    }
}
