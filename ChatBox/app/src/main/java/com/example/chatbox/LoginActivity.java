package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private EditText UserEmail, UserPassword;//object for user email and user password
    private TextView CreateNewAccount, ForgetPassword; // object for createnewaccount and forget password
    private Button LoginButton,LoginPhone; ;// object for Login button and register phone number

    private FirebaseAuth mAuth;
    private DatabaseReference Rootref,usersref;

    private ProgressDialog Loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        Rootref = FirebaseDatabase.getInstance().getReference();
        usersref = FirebaseDatabase.getInstance().getReference().child("Users");

        //initialize all fields
        Initilizefields();

        CreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }


        });

        LoginPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPhoneLoginActivity();
            }
        });


    }

    private void Initilizefields() {
        LoginButton = (Button) findViewById(R.id.login_button);

        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        CreateNewAccount = (TextView)findViewById(R.id.dont_have_an_account_link);
        ForgetPassword = (TextView) findViewById(R.id.already_have_an_account_link);
        LoginPhone = (Button)findViewById(R.id.Login_phone);
        Loadingbar =new ProgressDialog(this);
    }

    private void AllowUserToLogin() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        // shows toast box when email box is empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this,"Please enter your email id  " , Toast.LENGTH_SHORT).show();
        }

        // shows toast box when password box is empty
        if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this,"Please enter your correct password",Toast.LENGTH_SHORT).show();
        }

        //when login button is clicked it will show toast box with sign in and please wait......
        else{
            Loadingbar.setTitle("Sign in...");
            Loadingbar.setMessage("Please wait...!");
            Loadingbar.setCanceledOnTouchOutside(false);
            Loadingbar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()) {


                                String CurrentUserId = mAuth.getCurrentUser().getUid();
                                String DeviceToken = FirebaseInstanceId.getInstance().getToken();

                                usersref.child(CurrentUserId).child("device_token")
                                        .setValue(DeviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    SendUserToMainActivity();
                                                    Toast.makeText(LoginActivity.this, "Logged in successfully...", Toast.LENGTH_SHORT).show();
                                                    Loadingbar.dismiss();

                                                }
                                            }
                                        });

                                Rootref.child("Users").child(CurrentUserId);


                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this,"ERROR :" + message,Toast.LENGTH_SHORT).show();
                                Loadingbar.dismiss();

                            }
                        }
                    });
        }
    }
    private void SendUserToMainActivity() {
        Intent mainintent = new Intent(LoginActivity.this, MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
    private void SendUserToRegisterActivity(){
        Intent registerintent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerintent);
        finish();
    }
    private void SendUserToPhoneLoginActivity() {
        Intent Phoneloginintent = new Intent(LoginActivity.this,PhoneLoginActivity.class);
        startActivity(Phoneloginintent);
    }

}