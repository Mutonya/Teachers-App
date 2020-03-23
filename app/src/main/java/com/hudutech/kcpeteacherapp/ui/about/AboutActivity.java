package com.hudutech.kcpeteacherapp.ui.about;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.hudutech.kcpeteacherapp.R;
import com.hudutech.kcpeteacherapp.adapters.AboutSectionAdapter;
import com.hudutech.kcpeteacherapp.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAboutBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_about);

        AboutSectionAdapter mSectionAdapter = new AboutSectionAdapter(getSupportFragmentManager());
        mBinding.tutorDetailViewpager.setAdapter(mSectionAdapter);
    }
}
