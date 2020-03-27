package com.klickit.kcpeteacherapp.ui.about;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.klickit.kcpeteacherapp.models.TeacherProfile;
import com.klickit.kcpeteacherapp.repositories.UserRepository;

public class AboutViewModel extends AndroidViewModel {
    private UserRepository repository;
    private MutableLiveData<TeacherProfile> profile;
    private MutableLiveData<Boolean> isLoading;

    public AboutViewModel(@NonNull Application application) {
        super(application);
        repository = UserRepository.getInstance(application);
        isLoading = repository.getIsLoading();
    }

    public LiveData<TeacherProfile> getProfile(String userId) {
        return repository.getProfile(userId);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public void resetValues() {
        repository.resetValues();
    }
}
