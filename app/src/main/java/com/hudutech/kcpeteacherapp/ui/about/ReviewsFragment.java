package com.hudutech.kcpeteacherapp.ui.about;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hudutech.kcpeteacherapp.R;
import com.hudutech.kcpeteacherapp.adapters.ReviewsAdapter;
import com.hudutech.kcpeteacherapp.databinding.ReviewsFragmentBinding;

import java.util.Objects;

public class ReviewsFragment extends Fragment {

    private ReviewsViewModel mViewModel;
    private ReviewsFragmentBinding mBinding;
    private ReviewsAdapter mAdapter;

    public static ReviewsFragment newInstance(Bundle args) {
        ReviewsFragment fragment = new ReviewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.reviews_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mViewModel = ViewModelProviders.of(this).get(ReviewsViewModel.class);

        mViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                mBinding.progressBar.setVisibility(View.VISIBLE);
            } else {
                mBinding.progressBar.setVisibility(View.GONE);
            }
        });

        mViewModel.getReviews(Objects.requireNonNull(mCurrentUser).getUid()).observe(getViewLifecycleOwner(), starReviews -> {
            if (starReviews.size() > 0) {
                //set adapter with data.
                mBinding.tvNoData.setVisibility(View.GONE);
                mAdapter.setData(starReviews);
            } else {
                mBinding.tvNoData.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new ReviewsAdapter(requireContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(),
                ((LinearLayoutManager) layoutManager).getOrientation());
        mBinding.reviewsRecyclerView.setLayoutManager(layoutManager);
        mBinding.reviewsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.reviewsRecyclerView.addItemDecoration(dividerItemDecoration);
        mBinding.reviewsRecyclerView.setAdapter(mAdapter);
        mBinding.reviewsRecyclerView.setHasFixedSize(true);
    }

}
