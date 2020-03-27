package com.klickit.kcpeteacherapp.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.klickit.kcpeteacherapp.models.StarReview;

import java.util.ArrayList;
import java.util.List;

public class ReviewsRepository {
    private static final String TAG = "ReviewsRepository";
    private static ReviewsRepository instance;
    private final CollectionReference mReviewsRef = FirebaseFirestore.getInstance().collection("teacher_ratings");
    private Application mApplication;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public static ReviewsRepository getInstance(Application application) {
        if (instance == null) {
            instance = new ReviewsRepository();
            instance.mApplication = application;
        }
        return instance;
    }

    public MutableLiveData<List<StarReview>> fetchReviews(String userId) {
        final List<StarReview> reviews = new ArrayList<>();
        MutableLiveData<List<StarReview>> reviewsLiveData = new MutableLiveData<>();
        isLoading.postValue(true);
        mReviewsRef.whereEqualTo("reviewedUserId", userId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    isLoading.postValue(false);
                    if (e != null) {
                        Log.e(TAG, "fetchReviews: ", e);
                        return;
                    }
                    reviews.clear();
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            StarReview review = snapshot.toObject(StarReview.class);
                            if (review != null) {
                                reviews.add(review);

                            }
                        }

                        reviewsLiveData.postValue(reviews);

                    }

                });
        return reviewsLiveData;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
