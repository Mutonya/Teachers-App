package com.hudutech.kcpeteacherapp.repositories;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hudutech.kcpeteacherapp.interfaces.LoginMethods;
import com.hudutech.kcpeteacherapp.interfaces.SignUpMethods;
import com.hudutech.kcpeteacherapp.models.TeacherProfile;

import java.util.Objects;

public class AuthRepository implements LoginMethods, SignUpMethods {
    private static final String TAG = "AuthRepository";
    private static AuthRepository instance;
    private Application mApplication;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private MutableLiveData<String> successMsg = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private MutableLiveData<FirebaseUser> mCurrentUser = new MutableLiveData<>();


    public static synchronized AuthRepository getInstance(Application application) {
        if (instance == null) {
            instance = new AuthRepository();
            instance.mApplication = application;

        }

        return instance;
    }

    public void loginWithEmailPassword(String email, String password) {
        isLoading.postValue(true);
        errorMsg.postValue("");
        successMsg.postValue("");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        isLoading.postValue(false);
                        successMsg.postValue("Login Successful.");
                        mCurrentUser.postValue(mAuth.getCurrentUser());

                    } else {
                        String error = "Unable to Authenticate. please try again later";
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            error = "Invalid email/password";
                        }

                        errorMsg.postValue(error);

                    }
                });
    }

    public void loginWithFacebook(AccessToken accessToken) {
        isLoading.postValue(true);
        errorMsg.postValue("");
        successMsg.postValue("");
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        isLoading.postValue(false);
                        successMsg.postValue("Login Successful");
                        mCurrentUser.postValue(mAuth.getCurrentUser());


                    } else {
                        isLoading.postValue(false);
                        String message = "Authentication Failed.";

                        errorMsg.postValue(message);

                    }


                });

    }


    public void loginWithGoogle(GoogleSignInAccount account) {
        isLoading.postValue(true);
        errorMsg.postValue("");
        successMsg.postValue("");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        isLoading.postValue(false);
                        successMsg.postValue("Login Successful");
                        mCurrentUser.postValue(mAuth.getCurrentUser());

                    } else {
                        isLoading.postValue(false);
                        String message = "Authentication Failed.";
                        errorMsg.postValue(message);
                    }
                });
    }


    public MutableLiveData<TeacherProfile> getProfile(String userId) {
        MutableLiveData<TeacherProfile> profileLiveData = new MutableLiveData<>();

        DocumentReference mRef = firestore.collection("profiles").document(userId);
        mRef.get().addOnSuccessListener(documentSnapshot -> {
            TeacherProfile profile = documentSnapshot.toObject(TeacherProfile.class);
            profileLiveData.postValue(profile);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "checkProfile: ", e);
        });
        return profileLiveData;

    }


    @Override
    public void signUpWithEmailPassword(String email, String password) {
        isLoading.postValue(true);
        successMsg.postValue("");
        errorMsg.postValue("");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //pass the account instance here
                        isLoading.postValue(false);
                        successMsg.postValue("Account created successfully.");
                        mCurrentUser.postValue(mAuth.getCurrentUser());


                    } else {
                        String error;
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            error = "Account With this email already exists. Login Instead";
                        } else {
                            error = "Unable to Authenticate. please try again later";
                        }

                        isLoading.postValue(false);
                        errorMsg.postValue(error);

                    }


                });
    }

    @Override
    public void signUpWithFacebook(AccessToken accessToken) {
        isLoading.postValue(true);
        successMsg.postValue("");
        errorMsg.postValue("");
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        isLoading.postValue(false);
                        successMsg.postValue("Authentication Successful");
                        mCurrentUser.postValue(mAuth.getCurrentUser());

                    } else {
                        isLoading.postValue(false);

                        String message = "Authentication Failed.";
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            message = "Account already exists please login in instead.";
                        }

                        errorMsg.postValue(message);

                    }


                });
    }

    @Override
    public void signUpWithGoogle(GoogleSignInAccount account) {
        isLoading.postValue(true);
        successMsg.postValue("");
        errorMsg.postValue("");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        isLoading.postValue(false);
                        successMsg.postValue("Authentication Successful");
                        mCurrentUser.postValue(mAuth.getCurrentUser());

                    } else {
                        isLoading.postValue(false);
                        String message = "Authentication Failed.";
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            message = "Account already exists please login in instead.";
                        }

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



    public LiveData<FirebaseUser> getCurrentUser() {
        return mCurrentUser;
    }

    public void resetValues() {
        errorMsg.postValue("");
        successMsg.postValue("");
        isLoading.postValue(false);
    }
}
