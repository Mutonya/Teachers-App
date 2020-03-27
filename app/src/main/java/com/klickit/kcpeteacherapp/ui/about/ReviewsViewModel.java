package com.klickit.kcpeteacherapp.ui.about;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.klickit.kcpeteacherapp.models.StarReview;
import com.klickit.kcpeteacherapp.repositories.ReviewsRepository;

import java.util.List;

public class ReviewsViewModel extends AndroidViewModel {
    private ReviewsRepository repository;
    private MutableLiveData<Boolean> isLoading;
    public ReviewsViewModel(@NonNull Application application) {
        super(application);
        repository = ReviewsRepository.getInstance(application);
        isLoading = repository.getIsLoading();
    }

    LiveData<List<StarReview>> getReviews(String userId) {
        return repository.fetchReviews(userId);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

}
