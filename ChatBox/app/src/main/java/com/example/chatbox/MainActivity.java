package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;//for the toolbar
    private ViewPager myViewpager;//for the viewpager
    private TabLayout mytablayout;//for tablayout
    private TabAccessor mytabaccesssor;//tab accessment


    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;
    private String CurrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Rootref = FirebaseDatabase.getInstance().getReference();

        //for the toolbar and app name
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatBox");

        //for the chats and contacts tabs ..........
        myViewpager = (ViewPager)findViewById(R.id.main_tabs_pager);
        mytabaccesssor = new TabAccessor(getSupportFragmentManager());
        myViewpager.setAdapter(mytabaccesssor);

        //setting up viewpager........
        mytablayout = (TabLayout)findViewById(R.id.main_tabs);
        mytablayout.setupWithViewPager(myViewpager);
    }

    /*whenever application starts first this function will be exicuted*/
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentuser = mAuth.getCurrentUser();
        if (currentuser == null) {
            SendUserToLoginActivity();
        }
        else {
            updateUserStatus("Online");
            VerifyUserExistence();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentuser = mAuth.getCurrentUser();
        if(currentuser!=null){

            updateUserStatus("Offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseUser currentuser = mAuth.getCurrentUser();
        /*if(currentuser!=null){

            updateUserStatus("Offline");
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.Search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search here...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.menu_logout){

            updateUserStatus("Offline");
            mAuth.signOut();

            SendUserToLoginActivity();
            Toast.makeText(this, "Logged out successfully...", Toast.LENGTH_SHORT).show();
        }
        else if(item.getItemId()== R.id.menu_profile){
            SendUserToProfileActivity();
        }

        else if(item.getItemId() == R.id.menu_find_friends){
            SendUserToFindFriendsActivity();
        }

        return true;
    }



    private void VerifyUserExistence() {

        String CurrentUserID = mAuth.getCurrentUser().getUid();
        Rootref.child("Users").child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){

                    //Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else{
                    SendUserToSettingsActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void SendUserToLoginActivity() {
        Intent loginintent = new Intent(MainActivity.this, LoginActivity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();
    }

    private void SendUserToProfileActivity() {
        Intent profileintent = new Intent(MainActivity.this, ProfileActivity.class);
        //profileintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileintent);
    }

    private void SendUserToSettingsActivity() {
        Intent settingintent = new Intent(MainActivity.this, SettingsActivity.class);
        //settingintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingintent);
    }

    private void SendUserToFindFriendsActivity() {
        Intent findfriendsintent = new Intent(MainActivity.this, FindFriendsActivity.class);
        //findfriendsintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(findfriendsintent);
    }

    private void updateUserStatus(String state){

        String SaveCurrentTime,SaveCurrentDate;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat CurrentDate = new SimpleDateFormat("dd.MM.yyyy");
        SaveCurrentDate = CurrentDate.format(calendar.getTime());

        SimpleDateFormat CurrentTime = new SimpleDateFormat("hh:mm a");
        SaveCurrentTime = CurrentTime.format(calendar.getTime());

        HashMap<String,Object>OnlineStateMap = new HashMap<>();
        OnlineStateMap.put("time",SaveCurrentTime);
        OnlineStateMap.put("date",SaveCurrentDate);
        OnlineStateMap.put("state",state);

        CurrentUserID = mAuth.getCurrentUser().getUid();
        Rootref.child("Users").child(CurrentUserID).child("UserState")
                .updateChildren(OnlineStateMap);
    }

}