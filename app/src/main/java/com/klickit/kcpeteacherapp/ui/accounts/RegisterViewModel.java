package com.klickit.kcpeteacherapp.ui.accounts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.klickit.kcpeteacherapp.interfaces.SignUpMethods;
import com.klickit.kcpeteacherapp.repositories.AuthRepository;

public class RegisterViewModel extends AndroidViewModel implements SignUpMethods {
    private AuthRepository repository;
    private MutableLiveData<String> successMsg;
    private MutableLiveData<String> errorMsg;
    private MutableLiveData<Boolean> isLoading;
    private LiveData<FirebaseUser> mCurrentUser;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        repository = AuthRepository.getInstance(application);
        successMsg = repository.getSuccessMsg();
        errorMsg = repository.getErrorMsg();
        isLoading = repository.getIsLoading();
        mCurrentUser = repository.getCurrentUser();
    }

    @Override
    public void signUpWithEmailPassword(String email, String password) {
        repository.signUpWithEmailPassword(email, password);
    }

    @Override
    public void signUpWithFacebook(AccessToken accessToken) {
        repository.signUpWithFacebook(accessToken);
    }

    @Override
    public void signUpWithGoogle(GoogleSignInAccount account) {
        repository.signUpWithGoogle(account);
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


    public void resetValues() {
        repository.resetValues();
    }

}
