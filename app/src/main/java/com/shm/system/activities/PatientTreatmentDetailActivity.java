package com.shm.system.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shm.system.R;
import com.shm.system.contants.Globals;
import com.shm.system.entity.TreatmentObject;
import com.shm.system.entity.UserObject;
import com.shm.system.getset.GetterSetter;
import com.shm.system.utils.NetworkUtil;
import com.shm.system.utils.Utils;

public class PatientTreatmentDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private AppCompatImageView ivBack;
    private AppCompatTextView tvTreatmentDate, tvTreatedBy, tvPatientName, tvReasonForAccident, tvKindOfInjury,
            tvTypeOfDisease, tvTreatment;
    private LinearLayoutCompat llForFirstAid, llForDoctor;
    private LinearLayoutCompat llLiveDemo, llAddPatient, llReports;

    private TreatmentObject treatmentObject;
    private UserObject patientObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_treatment_detail);
        findView();
        initializeControls();
        attachListeners();
    }

    private void findView() {
        toolbar = findViewById(R.id.toolbar);
        ivBack = toolbar.findViewById(R.id.ivBack);

        tvTreatmentDate = findViewById(R.id.tvTreatmentDate);
        tvTreatedBy = findViewById(R.id.tvTreatedBy);
        tvPatientName = findViewById(R.id.tvPatientName);

        llForFirstAid = findViewById(R.id.llForFirstAid);
        tvReasonForAccident = findViewById(R.id.tvReasonForAccident);
        tvKindOfInjury = findViewById(R.id.tvKindOfInjury);

        llForDoctor = findViewById(R.id.llForDoctor);
        tvTypeOfDisease = findViewById(R.id.tvTypeOfDisease);
        tvTreatment = findViewById(R.id.tvTreatment);

        llLiveDemo = findViewById(R.id.llLiveDemo);
        llAddPatient = findViewById(R.id.llAddPatient);
        llReports = findViewById(R.id.llReports);
    }

    private void initializeControls() {

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
            }
        }

        treatmentObject = GetterSetter.getTreatmentObject();
        setTreatmentValues();
        userInfoListener();

        int userType = Utils.getIntPrefs(Globals.PrefConstants.PREF_USER_TYPE);
        if (userType == Globals.UserType.PATIENT) {
            llAddPatient.setVisibility(View.GONE);
        } else {
            llAddPatient.setVisibility(View.VISIBLE);
        }

    }

    private void attachListeners() {
        ivBack.setOnClickListener(this);
        llLiveDemo.setOnClickListener(this);
        llAddPatient.setOnClickListener(this);
        llReports.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            onBackPressed();
        } else if (v == llLiveDemo) {
            Intent intent = new Intent(PatientTreatmentDetailActivity.this, LiveDemoActivity.class);
            startActivity(intent);
        } else if (v == llAddPatient) {
            if (patientObject != null) {
                GetterSetter.setUserObject(patientObject);
                Intent intent = new Intent(PatientTreatmentDetailActivity.this, AddPatientTreatmentActivity.class);
                startActivity(intent);
            } else {
                Utils.showToast("Patient detail not found.");
            }
        } else if (v == llReports) {
            if (patientObject != null) {
                GetterSetter.setUserObject(patientObject);
                Intent intent = new Intent(PatientTreatmentDetailActivity.this, ReportsListActivity.class);
                startActivity(intent);
            } else {
                Utils.showToast("Patient detail not found.");
            }
        }
    }

    private void setTreatmentValues() {
        tvTreatmentDate.setText(treatmentObject.getTreatmentDate());
        tvPatientName.setText(treatmentObject.getPatientName());
        if (treatmentObject.getFirstAidPersonUserID().length() > 0) {
            String treatedByName = "First Aid : " + treatmentObject.getTreatedByName();
            tvTreatedBy.setText(treatedByName);
            llForFirstAid.setVisibility(View.VISIBLE);
            llForDoctor.setVisibility(View.GONE);
            tvReasonForAccident.setText(treatmentObject.getReasonForAccident());
            tvKindOfInjury.setText(treatmentObject.getKindOfInjury());
        } else {
            String treatedByName = "Doctor : " + treatmentObject.getTreatedByName();
            tvTreatedBy.setText(treatedByName);
            llForFirstAid.setVisibility(View.GONE);
            llForDoctor.setVisibility(View.VISIBLE);
            tvTypeOfDisease.setText(treatmentObject.getTypeOFDisease());
        }
        tvTreatment.setText(treatmentObject.getTreatmentDetail());
    }

    private void userInfoListener() {
        if (NetworkUtil.isNetworkAvailable(PatientTreatmentDetailActivity.this)) {
            String userID = treatmentObject.getPatientUserID();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(userID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        patientObject = dataSnapshot.getValue(UserObject.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Utils.showToast("No Internet Connection Found.");
        }
    }
}
