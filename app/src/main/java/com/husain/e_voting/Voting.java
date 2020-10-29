package com.husain.e_voting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Voting extends AppCompatActivity {

    private Button btnVote;
    private String phone;
    private TextView party;
    private RadioGroup radioGroup;
    private RadioButton actVote, cong, bjp, bsp, aap;
    private Firebase mref, ref1, ref2, ref3, ref4;
    private Integer OldVote1, OldVote2, OldVote3, OldVote4, flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
        getSupportActionBar().setTitle("Voting Portal");
        Firebase.setAndroidContext(this);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        btnVote = (Button) findViewById(R.id.vote);
        cong = findViewById(R.id.Congress);
        bjp = findViewById(R.id.BJP);
        bsp = findViewById(R.id.BSP);
        aap = findViewById(R.id.AAP);
        party = findViewById(R.id.party);
        final String path = "https://fir-storage-7bf6d.firebaseio.com/Votes/";

        btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                actVote = (RadioButton) findViewById(selectedId);
                ref1 = new Firebase(path + "Congress");
                ref2 = new Firebase(path + "BJP");
                ref3 = new Firebase(path + "BSP");
                ref4 = new Firebase(path + "AAP");
                if (actVote != null) {
                    new AlertDialog.Builder(Voting.this)
                            .setTitle("Voting")
                            .setMessage("Are you sure with Your Choice ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Voting(actVote);
                                    if (flag == 1) {
                                        mref.child(phone).child("Vote").setValue(1);
                                    }
                                }
                            })
                            .setNegativeButton("No", null)
                            .setIcon(R.drawable.ic_warning)
                            .show();
                } else {
                    new AlertDialog.Builder(Voting.this)
                            .setTitle("Voting")
                            .setMessage("Please select one Party to vote")
                            .setPositiveButton("OK", null)
                            .setIcon(R.drawable.ic_error)
                            .show();
                }
            }
        });
    }

    private void Voting(View v) {
        switch (v.getId()) {
            case R.id.Congress:
                if (flag == 0) {
                    ref1.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            OldVote1 = mutableData.getValue(Integer.class);
                            if (OldVote1 == null) {
                                mutableData.setValue(1);
                            } else {
                                mutableData.setValue(OldVote1 + 1);
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(FirebaseError firebaseError, boolean b, com.firebase.client.DataSnapshot dataSnapshot) {
                            //Toast.makeText(Voting.this,"You voted Congress",Toast.LENGTH_SHORT).show();
                        }
                    });
                    flag = 1;
                }
                break;

            case R.id.BJP:
                if (flag == 0) {
                    ref2.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            OldVote2 = mutableData.getValue(Integer.class);
                            if (OldVote2 == null) {
                                mutableData.setValue(1);
                            } else {
                                mutableData.setValue(OldVote2 + 1);
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(FirebaseError firebaseError, boolean b, com.firebase.client.DataSnapshot dataSnapshot) {
                            //Toast.makeText(Voting.this,"You voted BJP",Toast.LENGTH_SHORT).show();
                        }
                    });
                    flag = 1;
                }
                break;

            case R.id.BSP:
                if (flag == 0) {
                    ref3.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            OldVote3 = mutableData.getValue(Integer.class);
                            if (OldVote3 == null) {
                                mutableData.setValue(1);
                            } else {
                                mutableData.setValue(OldVote3 + 1);
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(FirebaseError firebaseError, boolean b, com.firebase.client.DataSnapshot dataSnapshot) {
                            //Toast.makeText(Voting.this,"You voted BSP",Toast.LENGTH_SHORT).show();
                        }
                    });
                    flag = 1;
                }
                break;

            case R.id.AAP:
                if (flag == 0) {
                    ref4.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            OldVote4 = mutableData.getValue(Integer.class);
                            if (OldVote4 == null) {
                                mutableData.setValue(1);
                            } else {
                                mutableData.setValue(OldVote4 + 1);
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(FirebaseError firebaseError, boolean b, com.firebase.client.DataSnapshot dataSnapshot) {
                            //Toast.makeText(Voting.this,"You voted AAP",Toast.LENGTH_SHORT).show();
                        }
                    });
                    flag = 1;
                }
                break;
        }
        new AlertDialog.Builder(Voting.this)
                .setTitle("Voting")
                .setMessage("Your vote has been successfully recorded")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(new Intent(Voting.this, Profile.class));
                    }
                })
                .setIcon(R.drawable.ic_error)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mref = new Firebase("https://fir-storage-7bf6d.firebaseio.com/Verified Users/");
        SharedPreferences sharedPreferences = getSharedPreferences("Number", MODE_PRIVATE);
        phone = sharedPreferences.getString("PHN", "");
        //Toast.makeText(Voting.this,phone,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(Voting.this, Profile.class).putExtra("Number", phone));
    }

    public void select(View view) {
        boolean check = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.Congress:
                if (check) {
                    party.setText("You're Voting : Congress");
                    party.setEnabled(true);
                } else {
                    party.setEnabled(false);
                }
                break;
            case R.id.BJP:
                if (check) {
                    party.setText("You're Voting : BJP");
                    party.setEnabled(true);
                } else {
                    party.setEnabled(false);
                }
                break;
            case R.id.BSP:
                if (check) {
                    party.setText("You're Voting : BSP");
                    party.setEnabled(true);
                } else {
                    party.setEnabled(false);
                }
                break;
            case R.id.AAP:
                if (check) {
                    party.setText("You're Voting : AAP");
                    party.setEnabled(true);
                } else {
                    party.setEnabled(false);
                }
                break;
        }
    }
}