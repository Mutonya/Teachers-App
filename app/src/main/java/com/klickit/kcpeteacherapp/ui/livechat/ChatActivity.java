package com.klickit.kcpeteacherapp.ui.livechat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.klickit.kcpeteacherapp.R;
import com.klickit.kcpeteacherapp.adapters.ChatMessageAdapter;
import com.klickit.kcpeteacherapp.databinding.ActivityChatBinding;
import com.klickit.kcpeteacherapp.models.ChatMessage;
import com.klickit.kcpeteacherapp.models.MediaType;
import com.klickit.kcpeteacherapp.models.User;
import com.klickit.kcpeteacherapp.ui.accounts.AccountsBaseActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;

import static com.klickit.kcpeteacherapp.utils.Utils.displayInfoMessage;


public class ChatActivity extends AppCompatActivity {
    //CHAT PAGINATION
    private final static int PAGE_ITEMS = 10;
    private static final String TAG = "ChatActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ActivityChatBinding mBinding;
    private ChatMessageAdapter mAdapter;
    private List<ChatMessage> messageList;
    private FirebaseUser mCurrentUser;
    private User recipient;
    private DatabaseReference mChatsRef;
    private DatabaseReference mRootRef;
    private ValueEventListener chatsListener;
    private int CURRENT_PAGE = 1;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    private SwipeRefreshLayout mSwipeLayout;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    //is typing and last seen
    private long delay = 1000; // 1 seconds after user stops typing
    private long last_text_edit = 0;
    private Runnable input_finish_checker;
    private Handler handler;
    private ActionBar ab;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private CollectionReference mRef;

    private String messageTemplate = "";

    //SEND IMAGE VARS
    private static final int GALLERY_PICK = 1;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        mSwipeLayout = mBinding.swipeToRefresh;
        mLayoutManager = new LinearLayoutManager(this);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mChatsRef = FirebaseDatabase.getInstance().getReference("chats").child(mCurrentUser.getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference("image_messages");

        setSupportActionBar(mBinding.toolbar);
        ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            Intent intent = getIntent();
            if (intent.hasExtra("bundle")) {

                Bundle bundle = intent.getBundleExtra("bundle");
                recipient = bundle.getParcelable("toUser");

                messageTemplate = bundle.getString("messageTemplate", "");
                ab.setTitle(recipient.getFullName());
                if (!messageTemplate.equals("")) {
                    mBinding.edittextChatbox.setText(String.format("%s\n", messageTemplate));
                }
                checkIsUserOnline();
            }

            ab.setDisplayHomeAsUpEnabled(false);
        }
        messageList = new ArrayList<>();
        mAdapter = new ChatMessageAdapter(this);
        loadMessages();
        initRecyclerView();
        initChats();
        isTypingFeature();

        //send message here
        mBinding.buttonChatboxSend.setOnClickListener(view -> sendMessage());
        mBinding.btnAttachFile.setOnClickListener(v -> {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ChatActivity.this);


        });

        mSwipeLayout.setOnRefreshListener(() -> {
            CURRENT_PAGE++;
            itemPos = 0;
            loadMoreMessages();

        });

        //load messages here


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try {

            if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .setMinCropWindowSize(500, 500)
                        .start(ChatActivity.this);
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();


                    File thumbnailFilePath = new File(resultUri.getPath());

                    Bitmap thumbnailBitmap = new Compressor(ChatActivity.this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumbnailFilePath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumbnailBytes = baos.toByteArray();
                    final String fileName = String.format("%s_%s.jpg", mCurrentUser.getUid(),new Date().getTime());


                    UploadTask uploadTask = mStorageRef.child(fileName).putBytes(thumbnailBytes);
                    uploadTask.addOnCompleteListener(thumbnailTask -> {

                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("image_messages/" + fileName);
                        mStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            sendImage(imageUrl);
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "onActivityResult: ", e);
                            Toasty.error(getApplicationContext(), "Error occurred while sending image", Toast.LENGTH_SHORT).show();
                        });

                    });


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Log.e(TAG, "onActivityResult: ", error);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initChats() {
        //create chat head if it does not exist.
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(recipient.getUserId())) {


                    Map<String, Object> chatMap1 = new HashMap<>();
                    chatMap1.put("seen", false);
                    chatMap1.put("timestamp", ServerValue.TIMESTAMP);
                    chatMap1.put("docKey", recipient.getUserId());

                    Map<String, Object> chatMap2 = new HashMap<>();
                    chatMap2.put("seen", false);
                    chatMap2.put("timestamp", ServerValue.TIMESTAMP);
                    chatMap2.put("docKey", mCurrentUser.getUid());


                    Map<String, Object> chatUserMap = new HashMap<>();
                    chatUserMap.put("chats/" + mCurrentUser.getUid() + "/" + recipient.getUserId(), chatMap1);
                    chatUserMap.put("chats/" + recipient.getUserId() + "/" + mCurrentUser.getUid(), chatMap2);
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
        chatsListener = listener;
    }

    private void sendMessage() {
        //add new space to message template

        String message = mBinding.edittextChatbox.getText().toString();

        if (!TextUtils.isEmpty(message)) {
            //define the key for chatmessage map
            String current_user_ref = "messages/" + mCurrentUser.getUid() + "/" + recipient.getUserId();
            String chat_user_ref = "messages/" + recipient.getUserId() + "/" + mCurrentUser.getUid();

            //get push key for message item
            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUser.getUid())
                    .child(recipient.getUserId()).push();
            String message_push_id = user_message_push.getKey();


            ChatMessage chatMessage = new ChatMessage(
                    mCurrentUser.getUid(),
                    recipient.getUserId(),
                    message,
                    MediaType.TEXT.value,
                    false,
                    new Date().getTime()
            );

            DatabaseReference newNotificationRef = mRootRef.child("notifications").push();
            String notificationId = newNotificationRef.getKey();

            HashMap<String, String> notificationData = new HashMap<>();
            notificationData.put("from", mCurrentUser.getUid());
            notificationData.put("message_key", message_push_id);
            notificationData.put("type", "message_sent");

            //Create another map to add data to each users chat thread
            Map<String, Object> chatUserMessageMap = new HashMap<>();
            chatUserMessageMap.put(current_user_ref + "/" + message_push_id, chatMessage);
            chatUserMessageMap.put(chat_user_ref + "/" + message_push_id, chatMessage);
            chatUserMessageMap.put("notifications/" + recipient.getUserId() + "/" + notificationId, notificationData);
            mBinding.edittextChatbox.setText("");

            mRootRef.updateChildren(chatUserMessageMap, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Log.d("SEND_MSG_CHAT_LOG", databaseError.getMessage());
                }
            });


        }

    }

    private void sendImage(String thumbnailUrl) {
        //define the key for chatmessage map
        String current_user_ref = "messages/" + mCurrentUser.getUid() + "/" + recipient.getUserId();
        String chat_user_ref = "messages/" + recipient.getUserId() + "/" + mCurrentUser.getUid();

        //get push key for message item
        DatabaseReference user_message_push = mRootRef.child("messages")
                .child(mCurrentUser.getUid())
                .child(recipient.getUserId()).push();
        String message_push_id = user_message_push.getKey();


        ChatMessage chatMessage = new ChatMessage(
                mCurrentUser.getUid(),
                recipient.getUserId(),
                thumbnailUrl,
                MediaType.IMAGE.value,
                false,
                new Date().getTime()
        );

        DatabaseReference newNotificationRef = mRootRef.child("notifications").push();
        String notificationId = newNotificationRef.getKey();

        HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("from", mCurrentUser.getUid());
        notificationData.put("message_key", message_push_id);
        notificationData.put("type", "message_sent");

        //Create another map to add data to each users chat thread
        Map<String, Object> chatUserMessageMap = new HashMap<>();
        chatUserMessageMap.put(current_user_ref + "/" + message_push_id, chatMessage);
        chatUserMessageMap.put(chat_user_ref + "/" + message_push_id, chatMessage);
        chatUserMessageMap.put("notifications/" + recipient.getUserId() + "/" + notificationId, notificationData);
        mBinding.edittextChatbox.setText("");

        mRootRef.updateChildren(chatUserMessageMap, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                Log.d("SEND_MSG_CHAT_LOG", databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (chatsListener != null) {
            mChatsRef.removeEventListener(chatsListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void initRecyclerView() {
        recyclerView = mBinding.reyclerviewMessageList;
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.hasFixedSize();
    }

    private void loadMessages() {


        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUser.getUid()).child(recipient.getUserId());
        messageRef.keepSynced(true);

        Query messageQuery = messageRef.limitToLast(CURRENT_PAGE * PAGE_ITEMS);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                mBinding.progressBar.setVisibility(View.GONE);
                if (!dataSnapshot.exists()){
                    Toasty.info(getApplicationContext(), "No Messages found", Toast.LENGTH_SHORT).show();
                    return;
                }
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);


                itemPos++;

                if (itemPos == 1) {

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }

                messageList.add(message);
                mAdapter.submitList(messageList);

                recyclerView.scrollToPosition(messageList.size() - 1);

                mSwipeLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                mBinding.progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                mBinding.progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                mBinding.progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mBinding.progressBar.setVisibility(View.GONE);
                Toasty.error(ChatActivity.this, "Encountered and error while loading messages", Toast.LENGTH_SHORT, true).show();

            }
        });

    }

    private void loadMoreMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUser.getUid()).child(recipient.getUserId());
        messageRef.keepSynced(true);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(PAGE_ITEMS);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                String messageKey = dataSnapshot.getKey();

                if (!mPrevKey.equals(messageKey)) {

                    messageList.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }


                if (itemPos == 1) {

                    mLastKey = messageKey;

                }


                mAdapter.submitList(messageList);

                mSwipeLayout.setRefreshing(false);

                mLayoutManager.scrollToPositionWithOffset(10, 0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mBinding.progressBar.setVisibility(View.GONE);

            }
        });

    }

    private void isTypingFeature() {
        // is typing feature


        handler = new Handler();

        input_finish_checker = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {

                mRootRef.child("messages").child(mCurrentUser.getUid()).child("typing").setValue(false);
            }
        };

        mBinding.edittextChatbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    //set typing true

                    mRootRef.child("messages").child(mCurrentUser.getUid()).child("typing").setValue(true);

                } else {
                    //set typing false
                    mRootRef.child("messages").child(mCurrentUser.getUid()).child("typing").setValue(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!TextUtils.isEmpty(charSequence)) {
                    //set typing true

                    mRootRef.child("messages").child(mCurrentUser.getUid()).child("typing").setValue(true);

                } else {
                    //set typing false
                    mRootRef.child("messages").child(mCurrentUser.getUid()).child("typing").setValue(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                }
            }
        });

        //typing feature
        mRootRef.child("messages").child(recipient.getUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("typing")) {
                            String typing = Objects.requireNonNull(dataSnapshot.child("typing").getValue()).toString();
                            if (typing.equals("true")) {
                                ab.setSubtitle("typing...");
                            } else {
                                ab.setSubtitle("online");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //mLastSeen.setText(getResources().getString(R.string.online));
                    }
                });
        //end of is typing feature

    }

    private void checkIsUserOnline() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("presence")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(recipient.getUserId())) {
                            boolean isOnline = dataSnapshot.child(recipient.getUserId())
                                    .getValue(Boolean.class);
                            if (isOnline) {
                                ab.setSubtitle("online");
                            } else {
                                ab.setSubtitle("away");
                            }
                        } else {
                            ab.setSubtitle("away.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        ab.setSubtitle("");
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser == null) {
            displayInfoMessage(this, "Login required");
            FirebaseAuth.getInstance().signOut();

            startActivity(new Intent(ChatActivity.this, AccountsBaseActivity.class));
            finish();
        }
    }
}
