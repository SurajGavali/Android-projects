package com.example.chatbox;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersref;

    public MessageAdapter(List<Messages> userMessagesList){

        this.userMessagesList = userMessagesList;

    }

    public  class  MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText,recieverMessageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = (TextView)itemView.findViewById(R.id.sender_message);
            recieverMessageText = (TextView)itemView.findViewById(R.id.reciever_message);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_message_lauout,viewGroup,false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {

        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages msg = userMessagesList.get(i);

        String fromuserid = msg.getFrom();
        String fromMessagetype = msg.getType();

        usersref  = FirebaseDatabase.getInstance().getReference().child("Users").child(fromuserid);
      /* usersref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

       if(fromMessagetype.equals("text")){

           messageViewHolder.recieverMessageText.setVisibility(View.INVISIBLE);
           messageViewHolder.senderMessageText.setVisibility(View.INVISIBLE);

           if(fromuserid.equals(messageSenderID)){
               messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);

               messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_message_layout);
               messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
               messageViewHolder.senderMessageText.setText(msg.getMessage());
           }
           else {



               messageViewHolder.recieverMessageText.setVisibility(View.VISIBLE);

               messageViewHolder.recieverMessageText.setBackgroundResource(R.drawable.reciever_message_layout);
               messageViewHolder.recieverMessageText.setTextColor(Color.BLACK);
               messageViewHolder.recieverMessageText.setText(msg.getMessage());


           }
       }
    }
    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }
}
