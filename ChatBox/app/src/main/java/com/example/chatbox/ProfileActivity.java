package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private String CurrentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;

    //private CircleImageView UserProfileImage;

    private TextView username , userstatus;
    private Button EditProfileButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        IntitilizeField();

        mToolbar = (Toolbar) findViewById(R.id.profile_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile");

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        Rootref = FirebaseDatabase.getInstance().getReference();

        DisplayUserInfo();

        EditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToSettingsActivity();
            }
        });
    }

    private void IntitilizeField() {

        username = (TextView)findViewById(R.id.profile_user_name);
        userstatus = (TextView)findViewById(R.id.profile_user_status);
        EditProfileButton = (Button)findViewById(R.id.edit_profile_button);
        //UserProfileImage = (CircleImageView)findViewById(R.id.profile_image);
    }

    private void DisplayUserInfo() {

        Rootref.child("Users").child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if((dataSnapshot.exists())  && (dataSnapshot.hasChild("name")  &&  (dataSnapshot.hasChild("status")))){

                            String retrieveusername = dataSnapshot.child("name").getValue().toString();
                            String retrieveuserstatus = dataSnapshot.child("status").getValue().toString();
                           // String retrieveuserprofileimage = dataSnapshot.child("image").getValue().toString();

                            username.setText(retrieveusername);
                            userstatus.setText(retrieveuserstatus);
                           // Picasso.get().load(retrieveuserprofileimage).into(UserProfileImage);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void SendUserToSettingsActivity() {

        Intent profileintent = new Intent(ProfileActivity.this, SettingsActivity.class);
        startActivity(profileintent);
    }
}