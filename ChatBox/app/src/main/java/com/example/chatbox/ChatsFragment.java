package com.example.chatbox;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    private View Chatsview;
    private RecyclerView chatslist;

    private DatabaseReference chatsref,usersref;
    private FirebaseAuth mAuth;
    private String CurrentUserID;





    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Chatsview =  inflater.inflate(R.layout.fragment_chats, container, false);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        chatsref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(CurrentUserID);
        usersref = FirebaseDatabase.getInstance().getReference().child("Users");

        chatslist = (RecyclerView) Chatsview.findViewById(R.id.chats_list);
        chatslist.setLayoutManager(new LinearLayoutManager(getContext()));


        return Chatsview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts>options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatsref,Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts,ChatsViewHolder>adapter=
                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {

                        final  String usersIDs = getRef(position).getKey();

                        usersref.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {


                                    final String retusername = dataSnapshot.child("name").getValue().toString();
                                    final String retuserstatus = dataSnapshot.child("status").getValue().toString();

                                    holder.username.setText(retusername);
                                    //holder.userstatus.setText(retuserstatus);


                                    if(dataSnapshot.child("UserState").hasChild("state")){

                                        String state = dataSnapshot.child("UserState").child("state").getValue().toString();
                                        String time = dataSnapshot.child("UserState").child("time").getValue().toString();
                                        String date = dataSnapshot.child("UserState").child("date").getValue().toString();

                                        if(state.equals("Online")){

                                            holder.userstatus.setText("Online");
                                        }
                                        else if(state.equals("Offline")){

                                            holder.userstatus.setText("Last seen at: " + time + "\n" +"On :" + date);
                                        }
                                    }
                                    else {

                                        holder.userstatus.setText("Offline");
                                    }


                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Intent chatintent = new Intent(getContext(),ChatActivity.class);
                                            chatintent.putExtra("vist_user_id",usersIDs);
                                            chatintent.putExtra("vist_user_namm",retusername);
                                            startActivity(chatintent);
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display,viewGroup,false);
                        return  new ChatsViewHolder(view);
                    }
                };

        chatslist.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        TextView username,userstatus;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_profile_name);
            userstatus = itemView.findViewById(R.id.user_profile_status);
        }
    }

}