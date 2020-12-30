package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {


    private Button CreateAccountButton,RegisterPhoneButton; // object for  create account button
    private EditText UserEmail, UserPassword ,UserConfirmPassword;// object for useremail and user password
    private TextView AlreadyHaveAccount;// object for already have account

    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;

    private ProgressDialog Loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        Rootref = FirebaseDatabase.getInstance().getReference();

        //initilizes all fields
        Initilizefields();

        AlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });

        RegisterPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterPhoneActivity();
            }
        });

    }

    private void Initilizefields() {
        CreateAccountButton = (Button) findViewById(R.id.register_button);
        RegisterPhoneButton = (Button) findViewById(R.id.register_phone);
        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        UserConfirmPassword = (EditText)findViewById(R.id.register_confirm_password);
        AlreadyHaveAccount = (TextView)findViewById(R.id.already_have_an_account_link);
        Loadingbar =new ProgressDialog(this);
    }

    private void CreateNewAccount() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmpassword = UserConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this,"Please  Enter your email id" , Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this,"Please  Enter your password",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(confirmpassword)){
            Toast.makeText(RegisterActivity.this,"This should not be empty",Toast.LENGTH_SHORT).show();
        }
        else  if(!(TextUtils.equals(password,confirmpassword))){

            Toast.makeText(RegisterActivity.this, "Both password's should be same", Toast.LENGTH_SHORT).show();
        }
        else{

            Loadingbar.setTitle("Creating new account");
            Loadingbar.setMessage("Please wait....");
            Loadingbar.setCanceledOnTouchOutside(false);
            Loadingbar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                String DeviceToken = FirebaseInstanceId.getInstance().getToken();

                                SendUserToMainActivity();
                                String CurrentUserID = mAuth.getCurrentUser().getUid();
                                Rootref.child("Users").child(CurrentUserID).setValue("");

                                Rootref.child("Users").child(CurrentUserID).child("device_token")
                                        .setValue(DeviceToken);
                                Toast.makeText(RegisterActivity.this,"Account created successfully" ,Toast.LENGTH_SHORT).show();
                            }
                            else {

                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "ERROR : "+ message, Toast.LENGTH_SHORT).show();
                                Loadingbar.dismiss();
                            }
                        }
                    });
        }
    }


    private void SendUserToLoginActivity(){
        Intent loginintent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginintent);
        finish();
    }
    private void SendUserToMainActivity() {
        Intent mainintent = new Intent(RegisterActivity.this,MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(mainintent);
        finish();
    }
    private void SendUserToRegisterPhoneActivity() {

        Intent phoneregisterintent = new Intent(RegisterActivity.this,RegisterPhoneActivity.class);
        startActivity(phoneregisterintent);
    }
}