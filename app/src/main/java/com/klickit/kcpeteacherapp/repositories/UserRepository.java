package com.klickit.kcpeteacherapp.repositories;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.klickit.kcpeteacherapp.models.TeacherProfile;
import com.klickit.kcpeteacherapp.models.User;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

import static com.klickit.kcpeteacherapp.utils.Utils.getBitmapFromUri;
import static com.klickit.kcpeteacherapp.utils.Utils.getFileNameFromUri;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private static UserRepository instance;
    private Application mApplication;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private MutableLiveData<String> successMsg = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public static synchronized UserRepository getInstance(Application application) {
        if (instance == null) {
            instance = new UserRepository();
            instance.mApplication = application;
        }
        return instance;
    }

    public void saveProfile(TeacherProfile profile, Uri avatarUri){

        successMsg.postValue("");
        errorMsg.postValue("");
        isLoading.postValue(true);
        final DocumentReference mRef = firestore.collection("profiles").document(mCurrentUser.getUid());
        Bitmap bitmapImage = getBitmapFromUri(mApplication.getApplicationContext(), avatarUri);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assert bitmapImage != null;
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        final byte[] thumbnailBytes = baos.toByteArray();
        String fileName = String.format("%s_%s", mCurrentUser.getUid(), getFileNameFromUri(mApplication.getApplicationContext(), avatarUri));

        UploadTask uploadTask = mStorageRef.child("profile_images")
                .child(fileName)
                .putBytes(thumbnailBytes);

       uploadTask.addOnSuccessListener(taskSnapshot -> {
           StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("profile_images/" + fileName);
           mStorageRef.getDownloadUrl().addOnSuccessListener(uri -> {
               String downloadUrl = uri.toString();
               profile.setAvatarUrl(downloadUrl);
               mRef.set(profile)
                       .addOnSuccessListener(aVoid -> {
                           User user = new User(
                                   profile.getUserId(),
                                   profile.getFirstName(),
                                   profile.getLastName(),
                                   profile.getEmail(),
                                   profile.getPhoneNumber(),
                                   profile.getAvatarUrl(),
                                   "",
                                   "",
                                   profile.getSchool(),
                                   "Teacher"
                           );
                           createUser(user);
                            isLoading.postValue(false);
                            successMsg.postValue("Profile created successfully");
                       }).addOnFailureListener(e -> {
                            isLoading.postValue(false);
                            errorMsg.postValue("Unable to create profile, please try again later.");
                       });
           }).addOnFailureListener(e -> {
               isLoading.postValue(false);
               errorMsg.postValue("Unable to created download link for profile photo");
           });
       }).addOnFailureListener(e -> {
           isLoading.postValue(false);
           errorMsg.postValue("Error occurred while uploading your profile photo.");
       });

    }

    private void createUser(@NotNull User user) {
        firestore.collection("users").document(user.getUserId()).set(user);
    }

    public MutableLiveData<TeacherProfile> getProfile(String userId) {
        isLoading.postValue(true);
        MutableLiveData<TeacherProfile> profileLiveData = new MutableLiveData<>();
        DocumentReference mRef = firestore.collection("profiles").document(userId);
        mRef.get().addOnSuccessListener(documentSnapshot -> {
            isLoading.postValue(false);
            TeacherProfile profile = documentSnapshot.toObject(TeacherProfile.class);
            profileLiveData.postValue(profile);
        }).addOnFailureListener(e -> {
            isLoading.postValue(false);
            Log.e(TAG, "getProfile: ", e);
        });
        return profileLiveData;

    }


    public MutableLiveData<String> getSuccessMsg() {
        return successMsg;
    }

    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void resetValues() {
        errorMsg.postValue("");
        successMsg.postValue("");
        isLoading.postValue(false);
    }
}
