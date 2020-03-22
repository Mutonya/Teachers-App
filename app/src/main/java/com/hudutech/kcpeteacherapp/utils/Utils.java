package com.hudutech.kcpeteacherapp.utils;





import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;
import android.widget.Toast;

import com.pchmn.materialchips.model.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class Utils {
    private static final String PREF_UNIQUE_ID = "install_prefs";
    private static String uniqueID;
    private Utils() {}

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



    public static void displayErrorMessage(Context context, String message) {
        Toasty.error(context, message, Toast.LENGTH_LONG).show();
    }

    public static void displaySuccessMessage(Context context, String message) {
        Toasty.success(context, message, Toast.LENGTH_SHORT).show();
    }

   public static void displayInfoMessage(Context context, String message) {
        Toasty.info(context, message, Toast.LENGTH_LONG).show();
    }

}
