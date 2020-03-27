package com.klickit.kcpeteacherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.klickit.kcpeteacherapp.R;
import com.klickit.kcpeteacherapp.databinding.ReviewLayoutBinding;
import com.klickit.kcpeteacherapp.models.StarReview;
import com.klickit.kcpeteacherapp.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private Context mContext;
    private List<StarReview> reviews = new ArrayList<>();
    private CollectionReference mUsersRef;


    public ReviewsAdapter(Context mContext) {
        this.mContext = mContext;
        mUsersRef = FirebaseFirestore.getInstance().collection("users");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ReviewLayoutBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.review_layout, parent, false);
        return new ViewHolder(mBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        StarReview review = reviews.get(position);
        holder.mBinding.setReview(reviews.get(position));
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.mBinding.tvRatingDate.setText(df.format(review.getTimestamp()));


        mUsersRef.document(reviews.get(position).getReviewByUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null) {
                        User user = documentSnapshot.toObject(User.class);
                        holder.mBinding.setUser(user);
                    }
                });


    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        ReviewLayoutBinding mBinding;


        ViewHolder(ReviewLayoutBinding reviewLayoutBinding) {
            super(reviewLayoutBinding.getRoot());
            mBinding = reviewLayoutBinding;


        }
    }
    public void setData(List<StarReview> reviewList) {
        reviews = reviewList;
        notifyDataSetChanged();
    }

}
