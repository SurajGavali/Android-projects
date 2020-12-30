package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DatabaseReference;

public class PhoneLoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button phoneloginbutton;
    private EditText phonenumber;

    private FirebaseAuth mAuth;
    private PhoneAuthCredential number;

    private ProgressDialog loadingbar;

    private DatabaseReference Rootref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        Intializefield();

        mToolbar = (Toolbar) findViewById(R.id.phone_login_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Phone Login");

        phoneloginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }

        });
    }

    private void Intializefield() {

        phoneloginbutton = (Button) findViewById(R.id.login_phone_button);
        phonenumber = (EditText)findViewById(R.id.login_phone);
        mAuth = FirebaseAuth.getInstance();
    }


    private void AllowUserToLogin() {

        String PhoneNumber = phonenumber.getText().toString();

        if(TextUtils.isEmpty(PhoneNumber)) {

            Toast.makeText(this, "Please is enter valid Phone number", Toast.LENGTH_SHORT).show();
        }
        else{

            loadingbar.setTitle("Sign in...");
            loadingbar.setMessage("Please wait...!");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            mAuth.signInWithCredential(number)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                String CurrentUserId = mAuth.getCurrentUser().getUid();
                                Rootref.child("Users").child(CurrentUserId);

                                SendUserToMainActivity();
                                Toast.makeText(PhoneLoginActivity.this, "Logged in successfully...", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(PhoneLoginActivity.this,"ERROR :" + message,Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }

                    });
        }
    }


    private void SendUserToMainActivity() {

        Intent mainintent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
}