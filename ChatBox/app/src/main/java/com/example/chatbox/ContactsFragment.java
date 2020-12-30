package com.example.chatbox;

import android.app.DownloadManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    private View ContactsView;
    private RecyclerView myContactList;

    private DatabaseReference Contactsref,usersref;
    private FirebaseAuth mAuth;
    private String CurrentUserID;
    private EditText SearchText;

    private Button SearchButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
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
        ContactsView =  inflater.inflate(R.layout.fragment_contacts, container, false);
        //SearchBarView = inflater.inflate(R.layout.fragment_contacts,container,false);

        myContactList = (RecyclerView)ContactsView.findViewById(R.id.contacts_list);
        myContactList.setLayoutManager(new LinearLayoutManager(getContext()));

        SearchButton = (Button)ContactsView.findViewById(R.id.Search_contact_button);
        SearchText = (EditText)ContactsView.findViewById(R.id.search_contact_name);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

        Contactsref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(CurrentUserID);
        usersref = FirebaseDatabase.getInstance().getReference().child("Users");



        return ContactsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        DisplayUser();
    }

    private void DisplayUser() {
        //String searchtext = SearchBar.getText().toString();
        //Query query = usersref.orderByChild("name").startAt(searchtext).endAt(searchtext + "\uf8ff");

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(Contactsref,Contacts.class)
                        //.setQuery(query,Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {

                final String usersID = getRef(position).getKey();

                usersref.child(usersID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        /*if(datasnapshot.hasChild("image")){

                            String userimage = datasnapshot.child("image").getValue().toString();
                            String profilename = datasnapshot.child("name").getValue().toString();
                            String profilestatus = datasnapshot.child("status").getValue().toString();

                            holder.username.setText(profilename);
                            holder.userstatus.setText(profilestatus);
                            Picasso.get().load(userimage).into(holder.profileimage);
                        }*/

                        if((dataSnapshot.exists())) {

                            String profilename = dataSnapshot.child("name").getValue().toString();
                            String profilestatus = dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(profilename);
                            holder.userstatus.setText(profilestatus);

                            if(dataSnapshot.child("UserState").hasChild("state")){

                                String state = dataSnapshot.child("UserState").child("state").getValue().toString();
                                String time = dataSnapshot.child("UserState").child("time").getValue().toString();
                                String date = dataSnapshot.child("UserState").child("date").getValue().toString();

                                if(state.equals("Online")){

                                    holder.onlineIcon.setVisibility(View.VISIBLE);
                                }
                                else if(state.equals("Offline")){

                                    holder.onlineIcon.setVisibility(View.INVISIBLE);
                                }
                            }
                            else {

                                holder.onlineIcon.setVisibility(View.INVISIBLE);
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display, viewGroup,false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;

            }
        };

        myContactList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView username,userstatus;
        ImageView onlineIcon;
        //CircleImageView profileimage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_profile_name);
            userstatus = itemView.findViewById(R.id.user_profile_status);
            onlineIcon = (ImageView)itemView.findViewById(R.id.user_online_status);

        }
    }
}