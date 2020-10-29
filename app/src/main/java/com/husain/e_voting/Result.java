package com.husain.e_voting;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.internal.StringResourceValueReader;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.PieChartView;

public class Result extends AppCompatActivity {

    private PieChartView pieChartView;
    private Firebase ref;
    private TextView result;
    private float v1,v2,v3,v4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().setTitle("Results");
        result=findViewById(R.id.tap);
        pieChartView = findViewById(R.id.chart);
        pieChartView.setVisibility(View.INVISIBLE);

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText("Voting Results");
                displayChart(v1,v2,v3,v4);
            }
        });
    }

    private void displayChart(float v1,float v2,float v3,float v4) {

        pieChartView.setVisibility(View.VISIBLE);
        List pieData = new ArrayList<>();
        pieData.add(new SliceValue(v1, Color.BLUE).setLabel("Congress"));
        pieData.add(new SliceValue(v2, Color.GREEN).setLabel("BJP"));
        pieData.add(new SliceValue(v3, Color.RED).setLabel("BSP"));
        pieData.add(new SliceValue(v4, Color.MAGENTA).setLabel("AAP"));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(15);
        pieChartView.setPieChartData(pieChartData);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ref = new Firebase("https://fir-storage-7bf6d.firebaseio.com/Votes");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                v1 = dataSnapshot.child("Congress").getValue(float.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                v2=dataSnapshot.child("BJP").getValue(float.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                v3=dataSnapshot.child("BSP").getValue(float.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                v4=dataSnapshot.child("AAP").getValue(float.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(Result.this,Profile.class));
    }
}
