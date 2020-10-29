package com.husain.e_voting;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class SignUp extends AppCompatActivity {

    private EditText fname,mname,lname,aadhar,add,phone,age;
    private Spinner gender;
    private Button img_upload;
    private TextView login;
    private Firebase databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("User Registration");
        Firebase.setAndroidContext(this);
        fname=findViewById(R.id.editFName);
        mname=findViewById(R.id.editMName);
        lname=findViewById(R.id.editLName);
        phone=findViewById(R.id.editPhone);
        gender=findViewById(R.id.editGender);
        age=findViewById(R.id.editAge);
        aadhar=findViewById(R.id.editAadhar);
        add=findViewById(R.id.editAdd);
        login=findViewById(R.id.tv2);
        img_upload=findViewById(R.id.SignUp);
        databaseReference= new Firebase("https://fir-storage-7bf6d.firebaseio.com/Users");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(SignUp.this,MainActivity.class));
            }
        });

        img_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String FName = fname.getText().toString().trim();
                    String MName = mname.getText().toString().trim();
                    String LName = lname.getText().toString().trim();
                    String Name = FName + " " + MName + " " + LName;
                    String Aadhar = aadhar.getText().toString().trim();
                    String Address = add.getText().toString().trim();
                    String Phone = phone.getText().toString().trim();
                    String Gender = gender.getSelectedItem().toString().trim();
                    String AgeValue = age.getText().toString().trim();
                    Integer Age = Integer.parseInt(AgeValue);

                    if (TextUtils.isEmpty(FName) || TextUtils.isEmpty(LName) || TextUtils.isEmpty(MName) || TextUtils.isEmpty(Aadhar)
                            || TextUtils.isEmpty(Address) || TextUtils.isEmpty(Phone) || TextUtils.isEmpty(Gender) || TextUtils.isEmpty(AgeValue))
                    {
                        new AlertDialog.Builder(SignUp.this)
                                .setTitle("User Registration")
                                .setMessage("Please Enter All the ")
                                .setPositiveButton("Ok", null)
                                .setIcon(R.drawable.ic_warning)
                                .show();
                    } else {
                        if (Phone.length() == 10) {
                            if (Age >= 18) {
                                if (Aadhar.length() == 12) {
                                    String ID = databaseReference.push().getKey();
                                    User user = new User(ID,Name,Aadhar,Phone,Address,Gender,Age);
                                    databaseReference.child(ID).setValue(user);
                                    Intent i = new Intent(SignUp.this, DocUpload.class);
                                    i.putExtra("Name", Name);
                                    i.putExtra("Phone",Phone);
                                    startActivity(i);
                                }else {
                                    aadhar.setError("Aadhar Card No. should be of 12 digits");
                                    aadhar.requestFocus();
                                }
                            } else {
                                age.setError("Age must not be less than 18");
                                age.requestFocus();
                            }
                        } else {
                            phone.setError("Phone number should be of 10 digits");
                            phone.requestFocus();
                        }
                    }
                }catch(Exception e)
                {
                    new AlertDialog.Builder(SignUp.this)
                            .setTitle("User Registration")
                            .setMessage("Please Enter All the Information")
                            .setPositiveButton("Ok", null)
                            .setIcon(R.drawable.ic_warning)
                            .show();
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
