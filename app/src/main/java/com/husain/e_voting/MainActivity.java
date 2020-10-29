package com.husain.e_voting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText editTextPhone, editTextCode, editUID;
    Button sendCode, verifyCode;
    FirebaseAuth mAuth;
    String codeSent;
    Boolean flg, flag = false;
    ProgressDialog progressDialog1, progressDialog2;
    String phone, user, uid;
    Firebase ref;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("User Login");
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("Number", MODE_PRIVATE);
        ref = new Firebase("https://fir-storage-7bf6d.firebaseio.com/Verified Users/");
        progressDialog1 = new ProgressDialog(this);
        progressDialog2 = new ProgressDialog(this);
        editTextCode = findViewById(R.id.editTextCode);
        editTextPhone = findViewById(R.id.editTextPhone);
        editUID = findViewById(R.id.editUID);
        sendCode = findViewById(R.id.buttonGetVerificationCode);
        verifyCode = findViewById(R.id.buttonSignIn);

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });

        findViewById(R.id.toAdmin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(MainActivity.this, AdminLogin.class));
            }
        });

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode();
            }
        });

        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog2.setMessage("Verifying");
                progressDialog2.show();
                verifySignInCode();
            }
        });
    }

    private void verifySignInCode() {
        if (flag) {
            if (verifyUID(uid)) {
                String code = editTextCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    progressDialog2.dismiss();
                    editTextCode.setError("Please Enter Verification Code");
                    editTextCode.requestFocus();
                    return;
                } else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
                    signInWithPhoneAuthCredential(credential);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Enter a Verified Phone Number", Toast.LENGTH_SHORT).show();
            }
        } else {
            progressDialog2.dismiss();
            Toast.makeText(MainActivity.this, "Verification Code not yet generated", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog2.dismiss();
                            Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i = new Intent(MainActivity.this, Profile.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                progressDialog2.dismiss();
                                Toast.makeText(MainActivity.this, "Incorrect Verification Code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void sendVerificationCode() {

        phone = editTextPhone.getText().toString().trim();
        user = editUID.getText().toString().trim();

        if (phone.isEmpty()) {
            editTextPhone.setError("Phone number is required");
            editTextPhone.requestFocus();
            return;
        }

        if (phone.length() < 10 || phone.length() > 10) {
            editTextPhone.setError("Phone Number must be of 10 digits");
            editTextPhone.requestFocus();
            return;
        }

        phone = "+91" + phone;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PHN", phone);
        editor.commit();

        if (user.isEmpty()) {
            editUID.setError("Please enter your Aadhaar Card No.");
            editUID.requestFocus();
            return;
        }

        if (user.length() != 12) {
            editUID.setError("Aadhaar Card number must be of 12 digits");
            editUID.requestFocus();
            return;
        } else {
            ref.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    uid = mutableData.child(phone).child("UID").getValue(String.class);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                    flg = false;
                    if (verifyUID(uid)) {
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phone,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                MainActivity.this,               // Activity (for callback binding)
                                mCallbacks);        // OnVerificationStateChangedCallbacks

                        progressDialog1.setMessage("Sending Verification Code");
                        progressDialog1.show();
                    } else {
                        editUID.requestFocus();
                        return;
                    }
                }
            });
        }
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            progressDialog1.dismiss();
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            progressDialog1.dismiss();
            Toast.makeText(getApplicationContext(), "Verification Code Sent", Toast.LENGTH_LONG).show();
            flag = true;
            codeSent = s;
        }
    };

    private Boolean verifyUID(String uid) {
        if (uid == null) {
            Toast.makeText(MainActivity.this, "Not a Verified phone number", Toast.LENGTH_SHORT).show();
            flg = true;
            return false;
        } else {
            if (user.equals(uid)) {
                return true;
            } else {
                editUID.setError("Invalid Aadhaar Card No.");
                return false;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(MainActivity.this, Profile.class));
        }
    }
}