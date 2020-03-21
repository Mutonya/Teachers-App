package com.hudutech.kcpeteacherapp.repositories;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthRepository {
    private static AuthRepository instance;
    private Application mApplication;
    private FirebaseAuth mAuth;

    private MutableLiveData<String> successMsg = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public static synchronized AuthRepository getInstance(Application application) {
        if (instance == null) {
            instance = new AuthRepository();
            instance.mApplication = application;
            instance.mAuth = FirebaseAuth.getInstance();
        }

        return instance;
    }

    public void loginWithEmailPassword(String email, String password) {
        isLoading.postValue(true);
        errorMsg.postValue("");
        successMsg.postValue("");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    isLoading.postValue(false);
                    if (task.isSuccessful()) {
                        successMsg.postValue("Login Successful.");

                    } else {
                        String error = "Unable to Authenticate. please try again later";
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            error = "Invalid email/password";
                        }

                        errorMsg.postValue(error);

                    }
                });
    }

    public void loginWithFacebook(AccessToken accessToken){
        isLoading.postValue(true);
        errorMsg.postValue("");
        successMsg.postValue("");
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    isLoading.postValue(false);
                    if (task.isSuccessful()) {

                     successMsg.postValue("Login Successful");


                    } else {
                        String message = "Authentication Failed.";

                        errorMsg.postValue(message);

                    }


                });

    }


    public  void loginWithGoogle(GoogleSignInAccount account){
        isLoading.postValue(true);
        errorMsg.postValue("");
        successMsg.postValue("");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        isLoading.postValue(false);
                       successMsg.postValue("Login Successful");
                    } else {

                        String message = "Authentication Failed.";
                        errorMsg.postValue(message);
                    }
                });
    }


    public MutableLiveData<String> getSuccessMsg() {
        return successMsg;
    }

    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
