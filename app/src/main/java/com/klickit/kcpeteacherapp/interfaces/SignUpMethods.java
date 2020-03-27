package com.klickit.kcpeteacherapp.interfaces;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface SignUpMethods {
    void signUpWithEmailPassword(String email, String password);
    void signUpWithFacebook(AccessToken accessToken);
    void signUpWithGoogle(GoogleSignInAccount account);
}
