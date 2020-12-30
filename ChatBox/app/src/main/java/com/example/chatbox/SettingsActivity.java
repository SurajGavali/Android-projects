package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button UpdateAccountSettings;
    private EditText username,userstatus;
    private CircleImageView UserProfileImage;

    private  static  final  int GalleryPick = 1;

    private Toolbar mToolbar;
    private String CurrentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;

    private StorageReference UserProfileImagesRef;

    private ProgressDialog Loadingbar;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        InitializationField();

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        Rootref = FirebaseDatabase.getInstance().getReference();


        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mToolbar = (Toolbar) findViewById(R.id.settings_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Setting");

        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfileSettings();
            }
        });



















       /* UserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick );

            }
        });*/
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){

            Uri imageuri = data.getData();

            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                Loadingbar.setTitle("Uploading profile image");
                Loadingbar.setMessage("Please wait..");
                Loadingbar.setCanceledOnTouchOutside(true);
                Loadingbar.show();

                Uri resultUri = result.getUri();

                StorageReference filepath = UserProfileImagesRef.child(CurrentUserID + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(SettingsActivity.this, "Profile image uploaded successfully...", Toast.LENGTH_SHORT).show();
                            final String DownLoadUrl = UserProfileImagesRef.getDownloadUrl().toString();

                            Rootref.child("Users").child(CurrentUserID).child("image")
                                    .setValue(DownLoadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                Toast.makeText(SettingsActivity.this, "Image stored in database successfully...", Toast.LENGTH_SHORT).show();
                                                Loadingbar.dismiss();
                                            }
                                            else{

                                                String message = task.getException().toString();
                                                Toast.makeText(SettingsActivity.this, "ERROR : "+message, Toast.LENGTH_SHORT).show();
                                                Loadingbar.dismiss();
                                            }
                                        }
                                    });
                        }
                        else{

                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "ERROR : "+message, Toast.LENGTH_SHORT).show();
                            Loadingbar.dismiss();
                        }
                    }
                });
            }
        }

    }*/























    private void InitializationField() {

        UpdateAccountSettings = (Button)findViewById(R.id.Update_Setting_button);
        username = (EditText)findViewById(R.id.set_user_name);
        userstatus = (EditText)findViewById(R.id.set_user_status);
        UserProfileImage = (CircleImageView)findViewById(R.id.profile_image);
        Loadingbar = new ProgressDialog(this);
    }

    private void UpdateProfileSettings() {

        String setusername  = username.getText().toString();
        String setuserstatus  = userstatus.getText().toString();

        if(TextUtils.isEmpty(setusername)){

            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(setuserstatus)){

            Toast.makeText(this, "Please enter status", Toast.LENGTH_SHORT).show();
        }
        else{

            HashMap<String,Object> profilemap = new HashMap<>();
            profilemap.put("uid",CurrentUserID);
            profilemap.put("name",setusername);
            profilemap.put("status",setuserstatus);

            Rootref.child("Users").child(CurrentUserID).updateChildren(profilemap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "profile updated successfully..", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String message  = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "ERROR : "+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void SendUserToMainActivity() {
        Intent mainintent = new Intent(SettingsActivity.this, MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
}