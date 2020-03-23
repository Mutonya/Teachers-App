package com.hudutech.kcpeteacherapp.ui.accounts;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.data.StreamLocalUriFetcher;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.hudutech.kcpeteacherapp.R;
import com.hudutech.kcpeteacherapp.databinding.ProfileFragmentBinding;
import com.hudutech.kcpeteacherapp.models.TeacherProfile;
import com.hudutech.kcpeteacherapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.M;
import static com.hudutech.kcpeteacherapp.utils.Utils.displayErrorMessage;
import static com.hudutech.kcpeteacherapp.utils.Utils.displaySuccessMessage;
import static com.hudutech.kcpeteacherapp.utils.Utils.getSkillsList;
import static com.hudutech.kcpeteacherapp.utils.Utils.isEmailValid;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int OPEN_IMAGE = 200;
    private ProfileViewModel mViewModel;
    private ProfileFragmentBinding mBinding;
    private NavController navController;
    private String[] storagePermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ProgressDialog mProgress;

    private Uri selectedPhotoUri;
    private String gender;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false);
        return mBinding.getRoot();
    }

    @RequiresApi(api = M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgress = new ProgressDialog(requireContext());
        navController = Navigation.findNavController(view);
        mBinding.chipsInput.setFilterableList(getSkillsList());
        mBinding.imgUserProfile.setOnClickListener(this);
        mBinding.btnContinue.setOnClickListener(this);

        mBinding.genderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_male) {
                gender = mBinding.radioMale.getText().toString();
            }

            if (checkedId == R.id.radio_female) {
                gender = mBinding.radioFemale.getText().toString();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        mViewModel.getSuccessMsg().observe(getViewLifecycleOwner(), s -> {
            if (!s.isEmpty()) {
                displaySuccessMessage(requireContext(), s);
                mViewModel.resetValues();
                navController.navigate(R.id.action_profileFragment_to_accountPendingFragment);
            }
        });

        mViewModel.getErrorMsg().observe(getViewLifecycleOwner(), s->{
            if (!s.isEmpty()) {
                displayErrorMessage(requireContext(), s);
            }
        });

        mViewModel.getIsLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                showProgress("Saving profile please wait..");
            } else{
                hideProgress();
            }
        });
    }

    @RequiresApi(api = M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (shouldShowRequestPermissionRationale(permissions[i])) {
                        requestStoragePermissions();
                    }
                    return;
                } else {
                    openImageChooser();
                }
            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            selectedPhotoUri = uri;
            assert uri != null;
            Glide.with(requireActivity())
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mBinding.imgUserProfile);


        }
    }

    @RequiresApi(api = M)
    private void requestStoragePermissions() {
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : storagePermissions) {
            if (requireActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), PERMISSION_REQUEST_CODE);
    }

    @RequiresApi(api = M)
    private boolean areStoragePermissionsGranted() {
        for (String permission : storagePermissions) {
            if (requireActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }


    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select A Photo"), OPEN_IMAGE);
    }


    @RequiresApi(api = M)
    @Override
    public void onClick(View v) {
        final int id = v.getId();

        if (id == R.id.img_user_profile) {
            if (areStoragePermissionsGranted()) {
                openImageChooser();
            } else {
                requestStoragePermissions();

            }
        } else if (id == R.id.btn_continue){
            if (validateInput()) {
                saveProfile();
            } else {
                Snackbar.make(v, "Fix the errors above to continue", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void saveProfile() {
        TeacherProfile profile = new TeacherProfile(
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                mBinding.txtFirstname.getText().toString().trim(),
                mBinding.txtLastname.getText().toString().trim(),
                mBinding.txtPhoneNumber.getText().toString().trim(),
                mBinding.txtEmail.getText().toString().trim(),
                mBinding.spinnerSalutation.getSelectedItem().toString().trim(),
                "",
                mBinding.txtSchool.getText().toString(),
                Utils.chipsSubjectToList(mBinding.chipsInput.getSelectedChipList()),
                false

        );

        mViewModel.saveProfile(profile, selectedPhotoUri);
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (TextUtils.isEmpty(mBinding.txtFirstname.getText().toString())) {
            isValid = false;
            mBinding.txtFirstname.setError("Required");
        }

        if (TextUtils.isEmpty(mBinding.txtLastname.getText().toString())) {
            isValid = false;
            mBinding.txtLastname.setError("Required");
        }

        if (TextUtils.isEmpty(mBinding.txtEmail.getText().toString())) {
            isValid = false;
            mBinding.txtEmail.setError("Required");
        }

        if (!isEmailValid(mBinding.txtEmail.getText().toString())) {
            isValid = false;
            mBinding.txtEmail.setError("Enter valid email");
        }

        if (TextUtils.isEmpty(mBinding.txtPhoneNumber.getText().toString())) {
            isValid = false;
            mBinding.txtPhoneNumber.setError("Required");
        }

        if (mBinding.chipsInput.getSelectedChipList().size() == 0) {
            isValid = false;
            displayErrorMessage(requireContext(), "Enter at least one subject");
        }

        if (mBinding.spinnerSalutation.getSelectedItemPosition() == 0) {
            isValid = false;
            displayErrorMessage(requireContext(), "Select appropriate salutation");
        }

        if (gender.isEmpty()) {
            isValid = false;
            displayErrorMessage(requireContext(), "Select gender");
        }


        return isValid;
    }

    private void hideProgress() {
        if (mProgress.isShowing()) mProgress.dismiss();
    }

    private void showProgress(String message) {
        mProgress.setMessage(message);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
    }


}
