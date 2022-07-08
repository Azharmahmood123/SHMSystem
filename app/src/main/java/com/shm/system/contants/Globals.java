package com.shm.system.contants;

public class Globals {

    public static String FORMAT_MODIFIED_DATE = "yyyy-MM-dd HH:mm:ss";

    public static class PrefConstants {
        public static String APP_PREFS = "shm_system_prefs";

        public static String PREF_USER_ID = "pref_user_id";
        public static String PREF_FIRST_NAME = "pref_first_name";
        public static String PREF_LAST_NAME = "pref_last_name";
        public static String PREF_EMAIL = "pref_email";
        public static String PREF_CNIC_NO = "pref_cnic_no";
        public static String PREF_PHONE_NO = "pref_phone_no";
        public static String PREF_GUARDIAN_NAME = "pref_guardian_name";
        public static String PREF_GUARDIAN_PHONE_NO = "pref_guardian_phone_no";
        public static String PREF_USER_TYPE = "pref_user_type";
    }

    public static class UserType {
        public static int PATIENT = 1;
        public static int DOCTOR = 2;
        public static int FIRST_AID_PERSON = 3;
    }
}
