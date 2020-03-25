package com.hudutech.kcpeteacherapp.repositories;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LiveChatRepository {
    private static LiveChatRepository instance;
    private DatabaseReference mChatsRef;
    private DatabaseReference mRootRef;
    private Application mApplication;

    public static LiveChatRepository getInstance(Application application) {
        if (instance == null) {
            FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            instance = new LiveChatRepository();
            instance.mApplication = application;
            instance.mRootRef = FirebaseDatabase.getInstance().getReference();
            instance.mChatsRef = FirebaseDatabase.getInstance().getReference("chats").child(mCurrentUser.getUid());
        }
        return instance;
    }

    public void initChats(final String userId, final String recipientUserId) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(recipientUserId)) {


                    Map<String, Object> chatMap1 = new HashMap<>();
                    chatMap1.put("seen", false);
                    chatMap1.put("timestamp", ServerValue.TIMESTAMP);
                    chatMap1.put("docKey", recipientUserId);

                    Map<String, Object> chatMap2 = new HashMap<>();
                    chatMap2.put("seen", false);
                    chatMap2.put("timestamp", ServerValue.TIMESTAMP);
                    chatMap2.put("docKey", userId);


                    Map<String, Object> chatUserMap = new HashMap<>();
                    chatUserMap.put("chats/" +userId + "/" + recipientUserId, chatMap1);
                    chatUserMap.put("chats/" + recipientUserId + "/" + userId, chatMap2);
                    mRootRef.updateChildren(chatUserMap, (databaseError, databaseReference) -> {
                        if (databaseError != null) {
                            Log.d("CHAT_LOG", databaseError.getMessage());
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mChatsRef.addValueEventListener(listener);
    }
}
