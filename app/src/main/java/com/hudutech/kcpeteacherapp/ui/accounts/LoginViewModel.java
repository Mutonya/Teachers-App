package com.hudutech.kcpeteacherapp.ui.accounts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.hudutech.kcpeteacherapp.interfaces.LoginMethods;
import com.hudutech.kcpeteacherapp.repositories.AuthRepository;

public class LoginViewModel extends AndroidViewModel implements LoginMethods {
    private AuthRepository repository;
    private MutableLiveData<String> successMsg;
    private MutableLiveData<String> errorMsg;
    private MutableLiveData<Boolean> isLoading;
    private LiveData<FirebaseUser> mCurrentUser;
    private LiveData<Boolean> hasPendingProfile;
    private LiveData<Boolean> hasActiveProfile;
    private LiveData<Boolean> hasNoProfile;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = AuthRepository.getInstance(application);
        successMsg = repository.getSuccessMsg();
        errorMsg = repository.getErrorMsg();
        isLoading = repository.getIsLoading();
        mCurrentUser = repository.getCurrentUser();
        hasActiveProfile = repository.hasActiveProfile();
        hasPendingProfile = repository.hasPendingProfile();
        hasNoProfile = repository.hasNoProfile();

    }


    @Override
    public void loginWithEmailPassword(String email, String password) {
        repository.loginWithEmailPassword(email, password);
    }

    @Override
    public void loginWithFacebook(AccessToken accessToken) {
        repository.loginWithFacebook(accessToken);
    }

    @Override
    public void loginWithGoogle(GoogleSignInAccount account) {
        repository.loginWithGoogle(account);
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

    public LiveData<FirebaseUser> getCurrentUser() {
        return mCurrentUser;
    }

    public LiveData<Boolean> getHasNoProfile() {
        return hasNoProfile;
    }

    public LiveData<Boolean> getHasPendingProfile() {
        return hasPendingProfile;
    }

    public LiveData<Boolean> getHasActiveProfile() {
        return hasActiveProfile;
    }

    public void resetValues() {
        repository.resetValues();
    }
}
