package com.klickit.kcpeteacherapp.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.klickit.kcpeteacherapp.R;
import com.klickit.kcpeteacherapp.databinding.AboutFragmentBinding;
import com.klickit.kcpeteacherapp.models.TeacherProfile;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class AboutFragment extends Fragment {

    private AboutViewModel mViewModel;
    private AboutFragmentBinding mBinding;

    @NotNull
    public static AboutFragment newInstance(Bundle args) {
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.about_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args!= null) {
            if (args.containsKey("profile")) {
                TeacherProfile profile = args.getParcelable("profile");
                assert profile != null;
                String name = String.format("%s %s(%s)", profile.getFirstName(), profile.getLastName(), profile.getDisplayName());
                mBinding.name.setText(name);
                StringBuilder sb = new StringBuilder();
                int i=1;
                for (String subject : profile.getSubjects()) {
                    sb.append(String.format(Locale.getDefault(),"%d %s\n\n", i, subject));
                    i++;
                }

                String subjects = sb.toString();
                mBinding.tvSubjects.setText(subjects);
                mBinding.tvSchool.setText(profile.getSchool());

                String contactAddress = String.format("Email Address: %s\n\n", profile.getEmail()) +
                        String.format("Phone Number: %s\n", profile.getPhoneNumber());
                mBinding.tvContactAddress.setText(contactAddress);


            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AboutViewModel.class);

    }

}
