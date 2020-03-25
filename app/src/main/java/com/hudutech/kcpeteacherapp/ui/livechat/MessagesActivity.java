package com.hudutech.kcpeteacherapp.ui.livechat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mkungusi.kcperevision.R;
import com.mkungusi.kcperevision.adapters.ConversationAdapter;
import com.mkungusi.kcperevision.databinding.ActivityMessagesBinding;
import com.mkungusi.kcperevision.models.Conversation;
import com.mkungusi.kcperevision.ui.auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class MessagesActivity extends AppCompatActivity {
    private static final String TAG = "MessagesActivity";
    private ActivityMessagesBinding mBinding;
    private DatabaseReference mChatsRef;
    private DatabaseReference mMessagesRef;
    private FirebaseUser mCurrentUser;
    private CollectionReference mUsersRef;


    private ConversationAdapter mAdapter;
    private ValueEventListener chatsListener;
    private List<Conversation> conversationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_messages);
        setSupportActionBar(mBinding.toolbar);
        ActionBar ab = getSupportActionBar();
        conversationList = new ArrayList<>();

        if (ab != null) {
            ab.setTitle("Chats");
            ab.setDisplayHomeAsUpEnabled(true);
        }
        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance();
        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mChatsRef = firebaseDb.getReference("chats").child(mCurrentUser.getUid());
        mMessagesRef = firebaseDb.getReference("messages").child(mCurrentUser.getUid());
        mUsersRef = firestoreDb.collection("users");
        mAdapter = new ConversationAdapter(this, conversationList);
        //initialize the recyclerview
        setUpRecyclerView();
        loadChats();

    }

    private void loadChats() {
        if (mChatsRef.getKey() == null) {
            mBinding.progressBar.setVisibility(View.GONE);
            Toasty.info(this, "No messages yet", Toast.LENGTH_LONG).show();
            return;
        }
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mChatsRef.orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mBinding.progressBar.setVisibility(View.GONE);

                Conversation conversation = dataSnapshot.getValue(Conversation.class);
                if (conversation != null) {
                    if (!conversation.getDocKey().equals(mCurrentUser.getUid())) {
                        conversationList.add(conversation);
                    }
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mBinding.progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                Toasty.error(MessagesActivity.this, "Encountered an error while loading messages", Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser == null) {
            Toasty.warning(this, "Login Required.", Toast.LENGTH_SHORT, true).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (chatsListener != null) {
            mChatsRef.removeEventListener(chatsListener);
        }
    }


    private void setUpRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        // manager.setReverseLayout(true);
        // manager.setStackFromEnd(true);
        DividerItemDecoration decoration = new DividerItemDecoration(this, manager.getOrientation());
        RecyclerView chatsRecyclerView = mBinding.chats;
        chatsRecyclerView.setLayoutManager(manager);
        chatsRecyclerView.addItemDecoration(decoration);
        chatsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatsRecyclerView.setAdapter(mAdapter);
        chatsRecyclerView.setHasFixedSize(true);
    }


}
