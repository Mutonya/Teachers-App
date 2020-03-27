package com.klickit.kcpeteacherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.klickit.kcpeteacherapp.R;
import com.klickit.kcpeteacherapp.databinding.ItemMessageReceivedBinding;
import com.klickit.kcpeteacherapp.databinding.ItemMessageSentBinding;
import com.klickit.kcpeteacherapp.interfaces.RecyclerItemClickListener;
import com.klickit.kcpeteacherapp.models.ChatMessage;
import com.klickit.kcpeteacherapp.models.MediaType;
import com.klickit.kcpeteacherapp.models.User;


public class ChatMessageAdapter extends ListAdapter<ChatMessage, RecyclerView.ViewHolder> implements RecyclerItemClickListener<ChatMessage> {
    private static final DiffUtil.ItemCallback<ChatMessage> DIFF_CALLBACK = new DiffUtil.ItemCallback<ChatMessage>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatMessage chatMessage, @NonNull ChatMessage t1) {
            return chatMessage.getFromUid().equals(t1.getFromUid()) && chatMessage.getToUid().equals(t1.getToUid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMessage chatMessage, @NonNull ChatMessage t1) {
            return chatMessage.toString().equals(t1.toString());
        }
    };
    private final static int SENT = 1;
    private final static int RECEIVED = 0;
    private Context mContext;
    private FirebaseUser mCurrentUser;
    private CollectionReference mUsersRef;


    public ChatMessageAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.mContext = context;
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUsersRef = FirebaseFirestore.getInstance().collection("users");

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == SENT) {
            ItemMessageSentBinding itemMessageSentBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_message_sent, viewGroup, false);
            return new ChatMessageAdapter.SentMessagesViewHolder(itemMessageSentBinding);
        } else {
            ItemMessageReceivedBinding messageReceivedBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_message_received, viewGroup, false);
            return new ChatMessageAdapter.ReceivedMessagesViewHolder(messageReceivedBinding);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getItemAt(position);
        if (message.getFromUid().equals(mCurrentUser.getUid())) {
            return SENT;
        } else {
            return RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ChatMessage message = getItemAt(position);
        if (message.getFromUid().equals(mCurrentUser.getUid())) {
            populateSentMessages(viewHolder, position);
        } else {
            populateReceivedMessages(viewHolder, position);
        }

    }

    private void populateSentMessages(RecyclerView.ViewHolder viewHolder, int position) {
        ChatMessage message = getItemAt(position);

        if (message.getMediaType().equals(MediaType.IMAGE.value)) {
            ((SentMessagesViewHolder) viewHolder).mBinding.imgMessageBody.setVisibility(View.VISIBLE);
            ((SentMessagesViewHolder) viewHolder).mBinding.textMessageBody.setVisibility(View.GONE);
        } else if (message.getMediaType().equals(MediaType.TEXT.value)) {
            ((SentMessagesViewHolder) viewHolder).mBinding.imgMessageBody.setVisibility(View.GONE);
            ((SentMessagesViewHolder) viewHolder).mBinding.textMessageBody.setVisibility(View.VISIBLE);
        }


        ((SentMessagesViewHolder) viewHolder).mBinding.setMessage(message);
    }

    private void populateReceivedMessages(RecyclerView.ViewHolder viewHolder, int position) {
        ChatMessage message = getItemAt(position);
        if (message.getMediaType().equals(MediaType.IMAGE.value)) {
            ((ReceivedMessagesViewHolder) viewHolder).mBinding.imgMessageBody.setVisibility(View.VISIBLE);
            ((ReceivedMessagesViewHolder) viewHolder).mBinding.textMessageBody.setVisibility(View.GONE);
        } else if (message.getMediaType().equals(MediaType.TEXT.value)) {
            ((ReceivedMessagesViewHolder) viewHolder).mBinding.imgMessageBody.setVisibility(View.GONE);
            ((ReceivedMessagesViewHolder) viewHolder).mBinding.textMessageBody.setVisibility(View.VISIBLE);
        }

        ((ReceivedMessagesViewHolder) viewHolder).mBinding.setMessage(message);
        mUsersRef.document(message.getFromUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);

                    ((ReceivedMessagesViewHolder) viewHolder).mBinding.setUser(user);
                });
    }

    @Override
    public void onItemClicked(ChatMessage message) {
        //@TODO update message FROM UN-READ TO READ.
        setMessageAsRead(message);
    }

    @Override
    public ChatMessage getItemAt(int position) {
        return getItem(position);
    }

    @Override
    public void restoreItem(ChatMessage item, int position) {

    }

    private void setMessageAsRead(ChatMessage chatMessage) {

    }

    public static class SentMessagesViewHolder extends RecyclerView.ViewHolder {
        ItemMessageSentBinding mBinding;

        SentMessagesViewHolder(ItemMessageSentBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    public static class ReceivedMessagesViewHolder extends RecyclerView.ViewHolder {
        ItemMessageReceivedBinding mBinding;

        ReceivedMessagesViewHolder(ItemMessageReceivedBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
