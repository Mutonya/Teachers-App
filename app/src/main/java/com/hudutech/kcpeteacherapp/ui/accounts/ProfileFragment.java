package com.hudutech.kcpeteacherapp.ui.accounts;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hudutech.kcpeteacherapp.R;
import com.hudutech.kcpeteacherapp.databinding.ProfileFragmentBinding;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.M;
import static com.hudutech.kcpeteacherapp.utils.Utils.getSkillsList;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE =100 ;
    private static final int OPEN_IMAGE = 200;
    private ProfileViewModel mViewModel;
    private ProfileFragmentBinding mBinding;
    private NavController navController;
    private String[] storagePermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Uri selectedPhotoUri;

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
        navController = Navigation.findNavController(view);
        mBinding.chipsInput.setFilterableList(getSkillsList());
       mBinding.imgUserProfile.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
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
                }else {
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
            if (areStoragePermissionsGranted()){
                openImageChooser();
            }else{
                requestStoragePermissions();

            }
        }
    }
}
