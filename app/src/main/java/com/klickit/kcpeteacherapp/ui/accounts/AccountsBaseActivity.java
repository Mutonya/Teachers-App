package com.klickit.kcpeteacherapp.ui.accounts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.klickit.kcpeteacherapp.R;


public class AccountsBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_base);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //check if user already logged in and redirect the user automatically
    }
}
