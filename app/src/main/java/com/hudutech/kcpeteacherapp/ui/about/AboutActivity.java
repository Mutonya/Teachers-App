package com.hudutech.kcpeteacherapp.ui.about;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hudutech.kcpeteacherapp.R;
import com.hudutech.kcpeteacherapp.adapters.AboutSectionAdapter;
import com.hudutech.kcpeteacherapp.databinding.ActivityAboutBinding;
import com.hudutech.kcpeteacherapp.models.TeacherProfile;
import com.hudutech.kcpeteacherapp.ui.accounts.AccountsBaseActivity;

import static com.hudutech.kcpeteacherapp.utils.Utils.displayInfoMessage;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutBinding mBinding;
    private AboutViewModel mViewModel;
    private FirebaseUser mCurrentUser;
    private TeacherProfile mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_about);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mViewModel = ViewModelProviders.of(this).get(AboutViewModel.class);
        observeData();
    }

    private void observeData() {
        mViewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                //show progress bar
            } else {
                //hide progress bar.
            }
        });

        mViewModel.getProfile(mCurrentUser.getUid()).observe(this, profile -> {
            if (profile != null) {
                mProfile = profile;
                AboutSectionAdapter mSectionAdapter = new AboutSectionAdapter(getSupportFragmentManager(), profile);
                mBinding.viewPager.setAdapter(mSectionAdapter);

                Glide.with(this)
                        .load(mProfile.getAvatarUrl())
                        .into(mBinding.imgProfile);
                mViewModel.resetValues();

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            //take user to login page.
            startActivity(new Intent(this, AccountsBaseActivity.class));
            finish();
            displayInfoMessage(this, "Login Required.");
        }
    }
}
