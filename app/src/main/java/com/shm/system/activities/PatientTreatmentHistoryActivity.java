package com.shm.system.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shm.system.R;
import com.shm.system.adapter.PatientsTreatedAdapter;
import com.shm.system.entity.TreatmentObject;
import com.shm.system.entity.UserObject;
import com.shm.system.getset.GetterSetter;
import com.shm.system.utils.Utils;
import com.shm.system.view.SpacesItemDecoration;

import java.util.List;

public class PatientTreatmentHistoryActivity extends AppCompatActivity implements View.OnClickListener, PatientsTreatedAdapter.OnItemClickListener {

    private Toolbar toolbar;
    private AppCompatImageView ivBack;
    private AppCompatTextView tvName, tvEmail, tvCnicNo, tvPhone, tvGuardianName, tvGuardianPhoneNo;
    private LinearLayoutCompat llLiveDemo, llAddPatient, llReports;
    private RecyclerView recyclerView;

    private UserObject patientObject;
    private List<TreatmentObject> treatmentObjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_treatment_history);
        findView();
        initializeControls();
        attachListeners();
    }

    private void findView() {
        toolbar = findViewById(R.id.toolbar);
        ivBack = toolbar.findViewById(R.id.ivBack);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvCnicNo = findViewById(R.id.tvCnicNo);
        tvPhone = findViewById(R.id.tvPhone);
        tvGuardianName = findViewById(R.id.tvGuardianName);
        tvGuardianPhoneNo = findViewById(R.id.tvGuardianPhoneNo);

        recyclerView = findViewById(R.id.recyclerView);

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

        patientObject = GetterSetter.getUserObject();
        treatmentObjectList = GetterSetter.getTreatmentObjectList();

        setPatientInfo();

        recyclerView.setLayoutManager(new LinearLayoutManager(PatientTreatmentHistoryActivity.this));
        PatientsTreatedAdapter patientsTreatedAdapter = new PatientsTreatedAdapter(treatmentObjectList, PatientTreatmentHistoryActivity.this);
        recyclerView.setAdapter(patientsTreatedAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_ten);
        recyclerView.addItemDecoration(new SpacesItemDecoration(1, spacingInPixels, true));

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
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
            Intent intent = new Intent(PatientTreatmentHistoryActivity.this, LiveDemoActivity.class);
            startActivity(intent);
        } else if (v == llAddPatient) {
            if (patientObject != null) {
                GetterSetter.setUserObject(patientObject);
                Intent intent = new Intent(PatientTreatmentHistoryActivity.this, AddPatientTreatmentActivity.class);
                startActivity(intent);
            } else {
                Utils.showToast("Patient detail not found.");
            }
        } else if (v == llReports) {
            if (patientObject != null) {
                GetterSetter.setUserObject(patientObject);
                Intent intent = new Intent(PatientTreatmentHistoryActivity.this, ReportsListActivity.class);
                startActivity(intent);
            } else {
                Utils.showToast("Patient detail not found.");
            }
        }
    }

    private void setPatientInfo() {
        String name = patientObject.getFirstName() + " " + patientObject.getLastName();
        tvName.setText(name);
        tvEmail.setText(patientObject.getEmail());
        tvCnicNo.setText(patientObject.getCnicNo());
        tvPhone.setText(patientObject.getPhoneNo());
        tvGuardianName.setText(patientObject.getGuardianName());
        tvGuardianPhoneNo.setText(patientObject.getGuardianPhoneNo());
    }

    @Override
    public void onItemClick(View view, int position) {
        TreatmentObject treatmentObject = treatmentObjectList.get(position);
        GetterSetter.setTreatmentObject(treatmentObject);
        Intent intent = new Intent(PatientTreatmentHistoryActivity.this, PatientTreatmentDetailActivity.class);
        startActivity(intent);
    }
}
