package com.hudutech.kcpeteacherapp.interfaces;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface LoginMethods {
    void loginWithEmailPassword(String email, String password);
    void loginWithFacebook(AccessToken accessToken);
    void loginWithGoogle(GoogleSignInAccount account);
}
