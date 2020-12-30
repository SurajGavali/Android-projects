package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String  messagerecievername,MessageSenderID,MessageRecieverID;
    private Button SendMessageButton;
    private EditText messageinput;
    private FirebaseAuth mAuth;

    private TextView UserLastSeen;
    private Toolbar ChatToolbar;

    private DatabaseReference Rootref;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private  MessageAdapter messageAdapter;

    private RecyclerView usermessagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        MessageSenderID = mAuth.getCurrentUser().getUid();

        Rootref = FirebaseDatabase.getInstance().getReference();



        MessageRecieverID = getIntent().getExtras().get("vist_user_id").toString();
        messagerecievername = getIntent().getExtras().get("vist_user_namm").toString();



        IntilizeField();
        DisplayLastSeen();

        //UserName.setText(messagerecievername);
        ChatToolbar = (Toolbar) findViewById(R.id.user_name_toolbar);
        setSupportActionBar(ChatToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(messagerecievername);

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

    }

    private void IntilizeField() {

        SendMessageButton = (Button)findViewById(R.id.send_message_button);
        messageinput = (EditText)findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        usermessagesList = (RecyclerView)findViewById(R.id.chat_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        usermessagesList.setLayoutManager(linearLayoutManager);
        usermessagesList.setAdapter(messageAdapter);


    }
    private void DisplayLastSeen(){

        Rootref.child("Users").child(MessageSenderID).child(MessageRecieverID)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                       /* if(dataSnapshot.exists()){

                            if(dataSnapshot.child("UserState").hasChild("state")){

                                String state = dataSnapshot.child("UserState").child("state").getValue().toString();
                                String time = dataSnapshot.child("UserState").child("time").getValue().toString();
                                String date = dataSnapshot.child("UserState").child("date").getValue().toString();

                                if(state.equals("Online")){

                                    UserLastSeen.setText("Online");
                                }
                                else if(state.equals("Offline")){

                                    UserLastSeen.setText("Last seen: " + date + " " + time);
                                }
                            }
                        }*/
                       /* else {

                            UserLastSeen.setText("Offline");
                        }*/
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Rootref.child("Messages").child(MessageSenderID).child(MessageRecieverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded( DataSnapshot dataSnapshot,  String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        usermessagesList.smoothScrollToPosition(usermessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged( @NonNull DataSnapshot dataSnapshot,  String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot,  String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SendMessage(){

        final String message = messageinput.getText().toString();

        if(TextUtils.isEmpty(message)){

            Toast.makeText(this, "Message field should not be empty", Toast.LENGTH_SHORT).show();
        }
        else {

            String messagesenderref = "Messages/"+ MessageSenderID +"/"+ MessageRecieverID;
            String messagerecieverref = "Messages/"+ MessageRecieverID +"/"+ MessageSenderID;

            DatabaseReference userMessageKeyref = Rootref.child("Messages")
                    .child(MessageSenderID).child(MessageRecieverID).push();

            String messagePushid = userMessageKeyref.getKey();

            Map<String, String> messageTextBody = new HashMap<>();
            messageTextBody.put("message",message);
            messageTextBody.put("type","text");
            messageTextBody.put("from",MessageSenderID);

            Map<String, Object> messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(messagesenderref + "/" + messagePushid, messageTextBody);
            messageBodyDetails.put(messagerecieverref + "/" + messagePushid, messageTextBody);

            Rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "message sent", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Toast.makeText(ChatActivity.this, "message is not sent", Toast.LENGTH_SHORT).show();
                    }

                    messageinput.setText("");
                }
            });


        }
    }

}