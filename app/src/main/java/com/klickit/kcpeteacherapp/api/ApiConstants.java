package com.klickit.kcpeteacherapp.api;

import com.klickit.kcpeteacherapp.BuildConfig;

public final class ApiConstants {

    //SMS GATEWAY CONFIGS
    public static final String API_BASE_URL = "https://org.ideasms.co.ke/";
    public static final String SMS_PARTNER_ID = BuildConfig.SMS_PARTNER_ID;
    public static final String SMS_SENDER_ID = BuildConfig.SMS_SENDER_ID;
    public static final String SMS_API_KEY = BuildConfig.SMS_API_KEY;

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final int CONNECT_TIMEOUT = 60 * 1000;
    /**
     * Connection Read timeout duration
     */
    public static final int READ_TIMEOUT = 60 * 1000;
    /**
     * Connection write timeout duration
     */
    public static final int WRITE_TIMEOUT = 60 * 1000;
    /**
     * Base URL
     */


    private ApiConstants() {
        /*private constructor Avoid instantiating this class by mistake.*/
    }

}
