package com.husain.e_voting;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class AdminSide extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button authenticate;
    private DrawerLayout drawer;
    private EditText phone, aadhar, name;
    private Boolean flag = false;
    private String Phone, Aadhar, Name, SMS;
    private Firebase ref, vref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_side);
        Firebase.setAndroidContext(this);
        ref = new Firebase("https://fir-storage-7bf6d.firebaseio.com/Verified Users/");
        vref = new Firebase("https://fir-storage-7bf6d.firebaseio.com/Voting/");
        authenticate = (Button) findViewById(R.id.toAuthenticate);
        phone = (EditText) findViewById(R.id.phoneNo);
        aadhar = (EditText) findViewById(R.id.aadhar);
        name = (EditText) findViewById(R.id.Name);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_adminlayout);
        NavigationView navigationView = findViewById(R.id.nav_adminview);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        authenticate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMSMessage();
            }
        });
    }

    protected void sendSMSMessage() {
        Phone = phone.getText().toString();
        Aadhar = aadhar.getText().toString();
        Name = name.getText().toString();

        if (TextUtils.isEmpty(Name)) {
            name.setError("Please enter a Name");
            name.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Phone)) {
            phone.setError("Please enter a phone number");
            phone.requestFocus();
            return;
        }

        if (Phone.length() > 10 || Phone.length() < 10) {
            phone.setError("Phone number must be of 10 digits");
            phone.requestFocus();
            return;
        }

        Phone = "+91" + Phone;

        if (TextUtils.isEmpty(Aadhar)) {
            aadhar.setError("Please enter Aadhaar Card Number");
            aadhar.requestFocus();
            return;
        }

        if (Aadhar.length() > 12 || Aadhar.length() < 12) {
            aadhar.setError("Aadhaar Card number must be of 12 digits");
            aadhar.requestFocus();
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Phone, null, "Congratulations, " + Name + " you are now a Verified User with Phone Number " + Phone + " and Aadhaar Card No." + Aadhar, null, null);
            new AlertDialog.Builder(AdminSide.this)
                    .setTitle("Authentication")
                    .setMessage("User Authentication Successful")
                    .setPositiveButton("OK", null)
                    .setIcon(R.drawable.ic_error)
                    .show();
        } catch (Exception e) {
            new AlertDialog.Builder(AdminSide.this)
                    .setTitle("Authentication")
                    .setMessage("User Authentication Failed")
                    .setPositiveButton("OK", null)
                    .setIcon(R.drawable.ic_error)
                    .show();
        }

        ref.child(Phone).child("UID").setValue(Aadhar);
        ref.child(Phone).child("Vote").setValue(0);
        ref.child(Phone).child("Name").setValue(Name);
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                Logout();
            }
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to logout automatically", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void Logout() {
        finish();
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(AdminSide.this, "You have been logged out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AdminSide.this, AdminLogin.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_result:
                finish();
                startActivity(new Intent(AdminSide.this, AdminResult.class));
                break;
            case R.id.nav_logout:
                Logout();
                break;
            case R.id.nav_end:
                vref.setValue(1);
                new AlertDialog.Builder(AdminSide.this)
                        .setTitle("Admin")
                        .setMessage("Voting has Ended. Users can't Vote now")
                        .setPositiveButton("Ok", null)
                        .setIcon(R.drawable.ic_warning)
                        .show();
                break;
            case R.id.nav_begin:
                vref.setValue(0);
                new AlertDialog.Builder(AdminSide.this)
                        .setTitle("Admin")
                        .setMessage("Voting has Begun. Users can vote now")
                        .setPositiveButton("Ok", null)
                        .setIcon(R.drawable.ic_warning)
                        .show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

