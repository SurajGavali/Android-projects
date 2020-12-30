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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import javax.sql.StatementEvent;

public class RegisterPhoneActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private Button sendverificationcode,verify;
    private EditText Enterphonenumber,OTP;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);

        Initializefield();

        mToolbar = (Toolbar) findViewById(R.id.phone_register_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Phone registration");

        sendverificationcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phonenumber = "+91" + Enterphonenumber.getText().toString();

                if(TextUtils.isEmpty(phonenumber)){

                    Toast.makeText(RegisterPhoneActivity.this, "Please enter phone number first!!!", Toast.LENGTH_SHORT).show();
                }
                else{


                    loadingbar.setTitle("Phone Verification");
                    loadingbar.setMessage("Please wait");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phonenumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            RegisterPhoneActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendverificationcode.setVisibility(View.INVISIBLE);
                Enterphonenumber.setVisibility(View.INVISIBLE);

                String verificationcode = sendverificationcode.getText().toString();

                if(TextUtils.isEmpty(verificationcode)){

                    Toast.makeText(RegisterPhoneActivity.this, "please enter verification code...", Toast.LENGTH_SHORT).show();
                }
                else{

                    loadingbar.setTitle("Code Verification");
                    loadingbar.setMessage("Please wait");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                loadingbar.dismiss();

                Toast.makeText(RegisterPhoneActivity.this, "Please Enter correct phone number..", Toast.LENGTH_SHORT).show();

                sendverificationcode.setVisibility(View.VISIBLE);
                Enterphonenumber.setVisibility(View.VISIBLE);

                verify.setVisibility(View.INVISIBLE);
                OTP.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loadingbar.dismiss();

                Toast.makeText(RegisterPhoneActivity.this, "Code sent to your phone number...", Toast.LENGTH_SHORT).show();

                sendverificationcode.setVisibility(View.INVISIBLE);
                Enterphonenumber.setVisibility(View.INVISIBLE);

                verify.setVisibility(View.VISIBLE);
                OTP.setVisibility(View.VISIBLE);

            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingbar.dismiss();
                            Toast.makeText(RegisterPhoneActivity.this, "Registered and Logged in successfully!!", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        }
                        else{
                            String message = task.getException().toString();
                            Toast.makeText(RegisterPhoneActivity.this, "ERROR : "+ message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void Initializefield() {

        sendverificationcode = (Button)findViewById(R.id.register_phone_button);
        verify = (Button)findViewById(R.id.verify_otp_button);
        Enterphonenumber = (EditText)findViewById(R.id.register_phone);
        OTP = (EditText)findViewById(R.id.phone_otp);
        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);
    }

    private void SendUserToMainActivity() {
        Intent mainintent = new Intent(RegisterPhoneActivity.this, MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
}