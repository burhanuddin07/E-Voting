package com.husain.e_voting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.flags.Flag;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Profile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String phone, username, aadhar;
    private Integer status;
    private Integer vote;
    private Button b;
    private TextView user, aad;
    private ImageView img;
    private DrawerLayout drawer;
    private Firebase ref, vref, uref, pref, aref;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Firebase.setAndroidContext(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        aad = findViewById(R.id.AAD);
        user = findViewById(R.id.Uname);
        img = findViewById(R.id.prof);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View v = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        b = findViewById(R.id.toVote);
        auth = FirebaseAuth.getInstance();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status == 1) {
                    new AlertDialog.Builder(Profile.this)
                            .setTitle("Voting")
                            .setMessage("Voting Time has Ended, you can't Vote now")
                            .setPositiveButton("OK", null)
                            .setIcon(R.drawable.ic_error)
                            .show();
                    return;
                } else {
                    if (!verifyVote(vote)) {
                        finish();
                        Intent intent = new Intent(Profile.this, Voting.class);
                        startActivity(intent);
                    } else {
                        new AlertDialog.Builder(Profile.this)
                                .setTitle("Voting")
                                .setMessage("You've already casted your vote \nYou cannot vote again.")
                                .setPositiveButton("OK", null)
                                .setIcon(R.drawable.ic_error)
                                .show();
                    }
                }
            }
        });
    }

    private Boolean verifyVote(Integer vote) {
        if (vote == 1) {
            //Toast.makeText(Profile.this, "Flag was set", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            //Toast.makeText(Profile.this, "Flag is not set", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser fuser = auth.getCurrentUser();
        if (fuser == null) {
            finish();
            startActivity(new Intent(Profile.this, MainActivity.class));
        }

        SharedPreferences sharedPreferences1 = getSharedPreferences("Number", MODE_PRIVATE);
        phone = sharedPreferences1.getString("PHN", "");

        vref = new Firebase("https://fir-storage-7bf6d.firebaseio.com/Voting/");
        vref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                status = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        uref = new Firebase("https://fir-storage-7bf6d.firebaseio.com/Verified Users/");
        uref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.child(phone).child("Name").getValue(String.class);
                user.setText("User :"+username);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        aref = new Firebase("https://fir-storage-7bf6d.firebaseio.com/Verified Users/");
        aref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                aadhar = dataSnapshot.child(phone).child("UID").getValue(String.class);
                aad.setText("Aadhar :"+aadhar);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        pref = new Firebase("https://fir-storage-7bf6d.firebaseio.com/Verified Users/");
        pref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.child(phone).child("Profile Image").getValue(String.class);
                if (url != null) {
                    Glide.with(getApplicationContext()).load(url).into(img);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        ref = new Firebase("https://fir-storage-7bf6d.firebaseio.com/Verified Users/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vote = dataSnapshot.child(phone).child("Vote").getValue(Integer.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
            }
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_result:
                finish();
                startActivity(new Intent(Profile.this, Result.class));
                break;
            case R.id.nav_update:
                finish();
                startActivity(new Intent(Profile.this, UpdateProfile.class));
                break;
            case R.id.nav_logout:
                auth.getInstance().signOut();
                Toast.makeText(Profile.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(Profile.this, MainActivity.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

