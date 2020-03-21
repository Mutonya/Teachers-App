package com.hudutech.kcpeteacherapp.ui.accounts;

import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudutech.kcpeteacherapp.R;
import com.hudutech.kcpeteacherapp.databinding.LoginFragmentBinding;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private LoginViewModel mViewModel;
    private LoginFragmentBinding mBinding;
    private  NavController navController;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding.setTerms(HtmlCompat.fromHtml(getString(R.string.terms_and_conditions), HtmlCompat.FROM_HTML_MODE_COMPACT));
        } else {
            mBinding.setTerms(HtmlCompat.fromHtml(getString(R.string.terms_and_conditions), HtmlCompat.FROM_HTML_MODE_COMPACT));
        }


        return mBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController =Navigation.findNavController(view);
        mBinding.btnLogin.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel


    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.btn_login) {
            navController.navigate(R.id.action_nav_login_to_nav_register);
        }
    }
}
