package com.hudutech.kcpeteacherapp.repositories;

import android.app.Application;

public class ReviewsRepository {
    private static ReviewsRepository instance;
    private Application mApplication;
    public static ReviewsRepository getInstance(Application application) {
        if (instance == null) {
            instance = new ReviewsRepository();
            instance.mApplication = application;
        }
        return instance;
    }


}
