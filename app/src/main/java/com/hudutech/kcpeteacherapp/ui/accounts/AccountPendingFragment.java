package com.hudutech.kcpeteacherapp.ui.accounts;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudutech.kcpeteacherapp.R;

public class AccountPendingFragment extends Fragment {

    private AccountPendingViewModel mViewModel;

    public static AccountPendingFragment newInstance() {
        return new AccountPendingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_pending_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AccountPendingViewModel.class);
        // TODO: Use the ViewModel
    }

}
