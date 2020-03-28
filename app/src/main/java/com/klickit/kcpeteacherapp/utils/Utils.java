package com.klickit.kcpeteacherapp.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.pchmn.materialchips.model.Chip;
import com.pchmn.materialchips.model.ChipInterface;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class Utils {
    private static final String PREF_UNIQUE_ID = "install_prefs";
    private static String uniqueID;
    private static final String TAG = "Utils";
    private Utils() {}

    public static String sanitizePhoneNumber(String phone) {
        String validPhoneNumber = "";

        if (phone.length() == 10 && phone.startsWith("0")) {
            StringBuilder builder = new StringBuilder(phone);
            String phoneWithoutLeadingZero = builder.deleteCharAt(0).toString();
            validPhoneNumber = String.format("254%s", phoneWithoutLeadingZero);
        }
        if (phone.length() == 13 && phone.startsWith("+")) {
            StringBuilder builder = new StringBuilder(phone);
            validPhoneNumber = builder.deleteCharAt(0).toString();

        } else if (phone.length() == 12 && phone.startsWith("254")) {
            validPhoneNumber = phone;
        }

        return validPhoneNumber;
    }
    public static ArrayList<Chip> getSkillsList() {
        ArrayList<Chip> skillChips = new ArrayList<>();
        skillChips.add(new Chip("Maths", "Mathematics"));
        skillChips.add(new Chip("English", "English"));
        skillChips.add(new Chip("Kiswahili", "Kiswahili"));
        skillChips.add(new Chip("CRE", "CRE"));
        skillChips.add(new Chip("Business Studies", "Business Studies"));
        skillChips.add(new Chip("Social Studies", "Social Studies"));
        skillChips.add(new Chip("Science", "Science"));
        return skillChips;
    }

    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);

            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
                editor.commit();
            }
        }

        return uniqueID;
    }

    static String isFirstInstall(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                PREF_UNIQUE_ID, Context.MODE_PRIVATE);

        return sharedPrefs.getString(PREF_UNIQUE_ID, null);
    }



    public static String getFileNameFromUri(Context context, Uri uri) {
        String fileName = null;
        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.

        try (Cursor cursor = context.getContentResolver()
                .query(uri, null, null, null, null, null)) {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                fileName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + fileName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null.  But since an
                // int can't be null in Java, the behavior is implementation-specific,
                // which is just a fancy term for "unpredictable".  So as
                // a rule, check if it's null before assigning to an int.  This will
                // happen often:  The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        }

        return fileName;
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");

            assert parcelFileDescriptor != null;
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


    public static void displayErrorMessage(Context context, String message) {
        Toasty.error(context, message, Toast.LENGTH_LONG).show();
    }

    public static void displaySuccessMessage(Context context, String message) {
        Toasty.success(context, message, Toast.LENGTH_SHORT).show();
    }

   public static void displayInfoMessage(Context context, String message) {
        Toasty.info(context, message, Toast.LENGTH_LONG).show();
    }

    public static List<String> chipsSubjectToList(List<? extends ChipInterface> chips) {
        List<String> subjects = new ArrayList<>();
        for (ChipInterface chip : chips) {
            subjects.add(chip.getInfo());
        }

        return subjects;
    }




}
