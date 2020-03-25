package com.hudutech.kcpeteacherapp.ui.accounts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseUser;
import com.hudutech.kcpeteacherapp.MainActivity;
import com.hudutech.kcpeteacherapp.R;
import com.hudutech.kcpeteacherapp.databinding.LoginFragmentBinding;

import java.util.Arrays;

import static com.hudutech.kcpeteacherapp.utils.Utils.displayErrorMessage;
import static com.hudutech.kcpeteacherapp.utils.Utils.displayInfoMessage;
import static com.hudutech.kcpeteacherapp.utils.Utils.displaySuccessMessage;
import static com.hudutech.kcpeteacherapp.utils.Utils.isConnected;
import static com.hudutech.kcpeteacherapp.utils.Utils.isEmailValid;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LoginFragment";
    private LoginViewModel mViewModel;
    private LoginFragmentBinding mBinding;
    private NavController navController;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1000;
    private LoginManager loginManager;
    private ProgressDialog mProgress;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false);
       // mBinding.tvLoginTerms.setMovementMethod(LinkMovementMethod.getInstance());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding.setTerms(HtmlCompat.fromHtml(getString(R.string.terms_and_conditions), HtmlCompat.FROM_HTML_MODE_COMPACT));
        } else {
            mBinding.setTerms(HtmlCompat.fromHtml(getString(R.string.terms_and_conditions), HtmlCompat.FROM_HTML_MODE_COMPACT));
        }

        mBinding.tvLoginTerms.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.tvLoginTerms.setClickable(true);

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
        mBinding.btnLogin.setOnClickListener(this);
        mBinding.btnFacebook.setOnClickListener(this);
        mBinding.btnGoogle.setOnClickListener(this);
        mBinding.tvDontHaveAccount.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        mViewModel.getSuccessMsg().observe(getViewLifecycleOwner(), s -> {
            if (!s.isEmpty()) {
                displaySuccessMessage(requireContext(), s);
               FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
               onAuthSuccess(mCurrentUser);

            }
        });

        mViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                showProgress("Authenticating please wait..");
            } else {
                hideProgress();
            }
        });


        mViewModel.getErrorMsg().observe(getViewLifecycleOwner(), s -> {
            if (!s.isEmpty()) {
                displayErrorMessage(requireContext(), s);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        checkIfUserAlreadyLoggedIn();
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

                mViewModel.loginWithGoogle(account);
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
                mViewModel.loginWithFacebook(loginResult.getAccessToken());
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



    @Override
    public void onClick(View v) {
        final int id = v.getId();


        if (id == R.id.btn_login) {
            if (isConnected(requireContext())) {
                if (validateInput()) {
                    mViewModel.loginWithEmailPassword(mBinding.txtEmail.getText().toString(), mBinding.txtPassword.getText().toString());
                } else {
                    Snackbar.make(v, "Fix errors above to continue", Snackbar.LENGTH_LONG).show();
                }
            } else {
                displayInfoMessage(getContext(), "No internet Connection");

            }
        } else if (id == R.id.btn_facebook) {
            if (isConnected(requireContext())) {
                loginManager.logInWithReadPermissions(requireActivity(), Arrays.asList("public_profile"));
            } else {
                displayInfoMessage(getContext(), "No internet Connection");
            }
        } else if (id == R.id.btn_google) {
            openGoogleAccountChooser();
        } else if (id == R.id.tv_dont_have_account) {
            navController.navigate(R.id.action_nav_login_to_nav_register);
        }
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



    private void checkIfUserAlreadyLoggedIn() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() !=null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void onAuthSuccess(FirebaseUser mCurrentUser) {
        showProgress("Checking profile");
        mViewModel.getProfile(mCurrentUser.getUid()).observe(getViewLifecycleOwner(), profile -> {
            if (profile !=null) {
                if (profile.isApproved()) {
                    mViewModel.resetValues();
                    hideProgress();
                    startActivity(new Intent(requireActivity(), MainActivity.class));
                    requireActivity().finish();

                } else {
                    hideProgress();
                    mViewModel.resetValues();
                    navController.navigate(R.id.action_nav_login_to_accountPendingFragment);

                }
            } else {
                mViewModel.resetValues();
                hideProgress();
                navController.navigate(R.id.action_nav_login_to_profileFragment);

            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;
        if(TextUtils.isEmpty(mBinding.txtEmail.getText().toString().trim())) {
            isValid = false;
            mBinding.txtEmail.setError("Email Required");
        }

        if (!isEmailValid(mBinding.txtEmail.getText().toString().trim())) {
            isValid = false;
            mBinding.txtEmail.setError("Invalid email");
        }

        if(TextUtils.isEmpty(mBinding.txtPassword.getText().toString())) {
            isValid = false;
            mBinding.txtPassword.setError("Password Required");
        }

        return isValid;
    }

}
