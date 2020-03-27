package com.klickit.kcpeteacherapp.ui.accounts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.klickit.kcpeteacherapp.interfaces.LoginMethods;
import com.klickit.kcpeteacherapp.models.TeacherProfile;
import com.klickit.kcpeteacherapp.repositories.AuthRepository;

public class LoginViewModel extends AndroidViewModel implements LoginMethods {
    private AuthRepository repository;
    private MutableLiveData<String> successMsg;
    private MutableLiveData<String> errorMsg;
    private MutableLiveData<Boolean> isLoading;
    private LiveData<FirebaseUser> mCurrentUser;


    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = AuthRepository.getInstance(application);
        successMsg = repository.getSuccessMsg();
        errorMsg = repository.getErrorMsg();
        isLoading = repository.getIsLoading();
        mCurrentUser = repository.getCurrentUser();

    }

    public LiveData<TeacherProfile> getProfile(String userId) {
        return repository.getProfile(userId);
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



    public void resetValues() {
        repository.resetValues();
    }
}
