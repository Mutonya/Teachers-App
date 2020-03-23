package com.hudutech.kcpeteacherapp.ui.accounts;

import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.hudutech.kcpeteacherapp.R;
import com.hudutech.kcpeteacherapp.databinding.RegisterFragmentBinding;

import java.util.Arrays;

import static com.hudutech.kcpeteacherapp.utils.Utils.displayErrorMessage;
import static com.hudutech.kcpeteacherapp.utils.Utils.displayInfoMessage;
import static com.hudutech.kcpeteacherapp.utils.Utils.displaySuccessMessage;
import static com.hudutech.kcpeteacherapp.utils.Utils.isConnected;
import static com.hudutech.kcpeteacherapp.utils.Utils.isEmailValid;

public class RegisterFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "RegisterFragment";
    private RegisterViewModel mViewModel;
    private NavController navController;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1000;
    private LoginManager loginManager;
    private ProgressDialog mProgress;
    private RegisterFragmentBinding mBinding;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.register_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mProgress = new ProgressDialog(requireContext());
        loginManager = LoginManager.getInstance();
        //Google Sign-in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        initFacebookSignIn();

        mBinding.btnCreateAccount.setOnClickListener(this);
        mBinding.btnFacebook.setOnClickListener(this);
        mBinding.btnGoogle.setOnClickListener(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding.tvTerms.setText(HtmlCompat.fromHtml(getString(R.string.terms_and_conditions), HtmlCompat.FROM_HTML_MODE_COMPACT));
        } else {
            mBinding.tvTerms.setText(HtmlCompat.fromHtml(getString(R.string.terms_and_conditions), HtmlCompat.FROM_HTML_MODE_COMPACT));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);

        mViewModel.getSuccessMsg().observe(getViewLifecycleOwner(), s -> {
            if (!s.isEmpty()){
                displaySuccessMessage(requireContext(), s);
            }
        });

        mViewModel.getErrorMsg().observe(getViewLifecycleOwner(), s -> {
            if (!s.isEmpty()){
                displayErrorMessage(requireContext(), s);
            }
        });

        mViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                showProgress("Creating account please wait...");
            } else {
                hideProgress();
            }
        });

        mViewModel.getCurrentUser().observe(getViewLifecycleOwner(), mCurrentUser->{
            if (mCurrentUser != null){
                mViewModel.resetValues();
                navController.navigate(R.id.action_nav_register_to_profileFragment);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //check which provider initiated sign-in
        if (requestCode == RC_SIGN_IN) {
            hideProgress();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                mViewModel.signUpWithGoogle(account);
            } catch (ApiException e) {
                hideProgress();
                displayErrorMessage(requireContext(), "Google sign in failed");
                Log.w(TAG, "Google sign in failed", e);

            }
        } else {
            //pass data to facebook sdk
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initFacebookSignIn() {
        mCallbackManager = CallbackManager.Factory.create();

        loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                mViewModel.signUpWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

                Log.d(TAG, "facebook:onCancel");
                displayErrorMessage(getContext(), "Login Canceled");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                displayErrorMessage(getContext(), "Unable to login");
            }
        });
    }

    private void openGoogleAccountChooser() {
        showProgress("Opening account chooser please wait...");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void hideProgress() {
        if (mProgress.isShowing()) mProgress.dismiss();
    }

    private void showProgress(String message) {
        mProgress.setMessage(message);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
    }


    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.btn_google) {
            if (isConnected(requireContext())) {
                openGoogleAccountChooser();
            } else {
                displayInfoMessage(getContext(), "No internet Connection");

            }
        } else if (id == R.id.btn_facebook) {
            if (isConnected(requireContext())) {
                loginManager.logInWithReadPermissions(requireActivity(), Arrays.asList("public_profile"));
            } else {
                displayInfoMessage(getContext(), "No internet Connection");
            }
        } else if (id == R.id.btn_create_account) {
            if (isConnected(requireContext())) {
                if (validateInput()) {
                    mViewModel.signUpWithEmailPassword(mBinding.txtEmail.getText().toString().trim(), mBinding.txtPassword.getText().toString().trim());
                } else {
                    Snackbar.make(v, "Fix the errors above to continue", Snackbar.LENGTH_LONG).show();
                }
            } else {
                displayInfoMessage(getContext(), "No internet Connection");
            }
        }
    }

    private boolean validateInput() {
        boolean isValid = true;
        if (TextUtils.isEmpty(mBinding.txtEmail.getText().toString().trim())) {
            isValid = false;
            mBinding.txtEmail.setError("Email cannot be empty");
        }
        if (!isEmailValid(mBinding.txtEmail.getText().toString().trim())) {
            isValid = false;
            mBinding.txtEmail.setError("Invalid email address");

        }

        if (TextUtils.isEmpty(mBinding.txtPassword.getText().toString().trim())) {
            isValid = false;
            mBinding.txtPassword.setError("Password cannot be empty");
        }

        if (TextUtils.isEmpty(mBinding.txtConfirmPassword.getText().toString().trim())) {
            isValid = false;
            mBinding.txtConfirmPassword.setError("Confirm password cannot be empty");
        }

        if (!TextUtils.equals(mBinding.txtPassword.getText().toString().trim(), mBinding.txtConfirmPassword.getText().toString().trim())) {
            isValid = false;
            mBinding.txtConfirmPassword.setError("Password does not match");
        }

        return isValid;

    }
}
