package com.husain.e_voting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminLogin extends AppCompatActivity {

    private Button login;
    private EditText editEmail, editPass;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        getSupportActionBar().setTitle("Admin Login");
        editEmail = findViewById(R.id.loginEmail);
        editPass = findViewById(R.id.loginPassword);
        login = findViewById(R.id.Login);
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
}

    private void LoginUser() {
        String email = editEmail.getText().toString();
        String pass = editPass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Please enter Email");
            editEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            editPass.setError("Please enter Password");
            editPass.requestFocus();
            return;
        }

        progressDialog.setMessage("Logging In");
        progressDialog.show();

        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            //Toast.makeText(getApplicationContext(), "Log In Successful", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(AdminLogin.this,AdminSide.class));
                        }
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}
