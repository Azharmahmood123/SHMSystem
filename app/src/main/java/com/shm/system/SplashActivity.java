package com.shm.system;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.shm.system.activities.DoctorHomeActivity;
import com.shm.system.activities.PatientHomeActivity;
import com.shm.system.activities.FirstAidPersonHomeActivity;
import com.shm.system.contants.Globals;
import com.shm.system.utils.Utils;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::startNextActivity, 1000);
    }

    @Override
    public void onBackPressed() {

    }

    private void startNextActivity() {
        String userID = Utils.getStringPrefs(Globals.PrefConstants.PREF_USER_ID);
        if (userID.length() > 0) {
            int userType = Utils.getIntPrefs(Globals.PrefConstants.PREF_USER_TYPE);
            Intent intent;
            if (userType == Globals.UserType.PATIENT) {
                intent = new Intent(SplashActivity.this, PatientHomeActivity.class);
            } else if (userType == Globals.UserType.DOCTOR) {
                intent = new Intent(SplashActivity.this, DoctorHomeActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, FirstAidPersonHomeActivity.class);
            }
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
