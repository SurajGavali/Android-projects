package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity {


    private String RecieveUserID,CurrentState,selfID;
    private TextView userprofilename ,userprofilestatus;
    private Button SendMessageRequest,CancelMessageRequest;

    private FirebaseAuth mAuth;

    private DatabaseReference userref,chatrequestref,contactsref,notificationref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        chatrequestref = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsref = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationref = FirebaseDatabase.getInstance().getReference().child("Notifications");

        mAuth = FirebaseAuth.getInstance();

        selfID = mAuth.getCurrentUser().getUid();

        RecieveUserID = getIntent().getExtras().get("visit_user_id").toString();

        userprofilename = (TextView)findViewById(R.id.user_profile_user_name);
        userprofilestatus = (TextView)findViewById(R.id.user_profile_user_status);
        SendMessageRequest = (Button)findViewById(R.id.user_send_message_button);
        CancelMessageRequest = (Button)findViewById(R.id.user_cancel_message_button);

        CurrentState = "new";


        RetriveUserInfo();
    }

    private void RetriveUserInfo() {

        userref.child(RecieveUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {

                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    userprofilename.setText(userName);
                    userprofilestatus.setText(userStatus);


                    ManageChatRequest();

                } else {
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    userprofilestatus.setText(userStatus);

                    ManageChatRequest();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ManageChatRequest() {

        chatrequestref.child(selfID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        if(datasnapshot.hasChild(RecieveUserID)){

                            String request_type = datasnapshot.child(RecieveUserID).child("request_type").getValue().toString();
                            if(request_type.equals("sent")){

                                CurrentState = "request_sent";
                                SendMessageRequest.setText("Cancel Request");
                            }
                            else if(request_type.equals("recieved")){

                                CurrentState = "request_recieved";
                                SendMessageRequest.setText("Accept");

                                CancelMessageRequest.setVisibility(View.VISIBLE);
                                CancelMessageRequest.setEnabled(true);

                                CancelMessageRequest.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        CancelChatRequest();

                                    }
                                });


                            }
                        }
                        else {

                            contactsref.child(selfID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                                            if(datasnapshot.hasChild(RecieveUserID)){

                                                CurrentState = "friends";
                                                SendMessageRequest.setText("Remove");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if(!selfID.equals(RecieveUserID)){

            SendMessageRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMessageRequest.setEnabled(false);

                    if(CurrentState.equals("new")){

                        SendChatRequest();
                    }
                    if(CurrentState.equals("request_sent")){
                        CancelChatRequest();
                    }
                    if(CurrentState.equals("request_recieved")){
                        AcceptChatRequest();
                    }
                    if(CurrentState.equals("friends")){

                        RemoveSpecificContact();
                    }
                }


            });
        }
        else {

            SendMessageRequest.setVisibility(View.INVISIBLE);
        }

    }

    private void SendChatRequest() {

        chatrequestref.child(selfID).child(RecieveUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            chatrequestref.child(RecieveUserID).child(selfID)
                                    .child("request_type").setValue("recieved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    HashMap<String,String>chatnotificationMap = new HashMap<>();
                                                    chatnotificationMap.put("From",selfID);
                                                    chatnotificationMap.put("type","request");

                                                    notificationref.child(RecieveUserID).push()
                                                            .setValue(chatnotificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                SendMessageRequest.setEnabled(true);
                                                                CurrentState = "request_sent";
                                                                SendMessageRequest.setText("Cancel Request");

                                                            }
                                                        }
                                                    });


                                                }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelChatRequest() {

        chatrequestref.child(selfID).child(RecieveUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            chatrequestref.child(RecieveUserID).child(selfID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                SendMessageRequest.setEnabled(true);
                                                CurrentState = "new";
                                                SendMessageRequest.setText("Send Request");

                                                CancelMessageRequest.setVisibility(View.INVISIBLE);
                                                CancelMessageRequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void RemoveSpecificContact() {

        contactsref.child(selfID).child(RecieveUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            contactsref.child(RecieveUserID).child(selfID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                SendMessageRequest.setEnabled(true);
                                                CurrentState = "new";
                                                SendMessageRequest.setText("Send Request");

                                                CancelMessageRequest.setVisibility(View.INVISIBLE);
                                                CancelMessageRequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }



    private void AcceptChatRequest() {

        contactsref.child(selfID).child(RecieveUserID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactsref.child(RecieveUserID).child(selfID)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                chatrequestref.child(selfID).child(RecieveUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    chatrequestref.child(RecieveUserID).child(selfID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                   SendMessageRequest.setEnabled(true);
                                                                                   CurrentState = "friends";
                                                                                   SendMessageRequest.setText("Remove");

                                                                                   CancelMessageRequest.setVisibility(View.INVISIBLE);
                                                                                   CancelMessageRequest.setEnabled(false);
                                                                                }
                                                                            });

                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });

                        }
                    }
                });
    }

}