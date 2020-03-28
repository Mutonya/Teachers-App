package com.klickit.kcpeteacherapp.ui.accounts;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.klickit.kcpeteacherapp.models.TeacherProfile;
import com.klickit.kcpeteacherapp.repositories.UserRepository;

public class ProfileViewModel extends AndroidViewModel {
    private UserRepository repository;
    private MutableLiveData<String> successMsg;
    private MutableLiveData<String> errorMsg;
    private MutableLiveData<Boolean> isLoading;
    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = UserRepository.getInstance(application);
        successMsg = repository.getSuccessMsg();
        errorMsg = repository.getErrorMsg();
        isLoading = repository.getIsLoading();

    }

    public void saveProfile(TeacherProfile profile, Uri avatarUri) {
        repository.saveProfile(profile, avatarUri);
    }

    public LiveData<String> getSuccessMsg() {
        return successMsg;
    }

    public LiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void resetValues() {
        repository.resetValues();
    }

    public void clear() {
        repository.clear();
    }
}
