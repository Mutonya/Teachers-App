package com.hudutech.kcpeteacherapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hudutech.kcpeteacherapp.R;
import com.hudutech.kcpeteacherapp.ui.about.AboutActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView btnProfile = root.findViewById(R.id.btn_profile);
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AboutActivity.class));
        });

        return root;
    }
}
