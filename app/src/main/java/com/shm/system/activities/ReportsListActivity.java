package com.shm.system.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shm.system.R;
import com.shm.system.adapter.PatientReportsAdapter;
import com.shm.system.contants.Globals;
import com.shm.system.entity.ReportsObject;
import com.shm.system.entity.UserObject;
import com.shm.system.getset.GetterSetter;
import com.shm.system.utils.NetworkUtil;
import com.shm.system.utils.ReportsComparable;
import com.shm.system.utils.Utils;
import com.shm.system.view.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportsListActivity extends AppCompatActivity implements View.OnClickListener, PatientReportsAdapter.OnItemClickListener {

    private Toolbar toolbar;
    private AppCompatImageView ivBack;
    private LinearLayoutCompat llAddPatientReport;
    private RecyclerView recyclerView;

    private UserObject patientObject;
    private final List<ReportsObject> reportsObjectList = new ArrayList<>();
    private PatientReportsAdapter patientReportsAdapter;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_reports);
        findView();
        initializeControls();
        attachListeners();
    }

    private void findView() {
        toolbar = findViewById(R.id.toolbar);
        ivBack = toolbar.findViewById(R.id.ivBack);

        recyclerView = findViewById(R.id.recyclerView);

        llAddPatientReport = findViewById(R.id.llAddPatientReport);

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

        recyclerView.setLayoutManager(new LinearLayoutManager(ReportsListActivity.this));
        patientReportsAdapter = new PatientReportsAdapter(reportsObjectList, ReportsListActivity.this);
        recyclerView.setAdapter(patientReportsAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_ten);
        recyclerView.addItemDecoration(new SpacesItemDecoration(1, spacingInPixels, true));

        int userType = Utils.getIntPrefs(Globals.PrefConstants.PREF_USER_TYPE);
        if (userType == Globals.UserType.DOCTOR) {
            llAddPatientReport.setVisibility(View.VISIBLE);
        } else {
            llAddPatientReport.setVisibility(View.GONE);
        }

        loadPatientReportsData();
    }

    private void attachListeners() {
        ivBack.setOnClickListener(this);
        llAddPatientReport.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            onBackPressed();
        } else if (v == llAddPatientReport) {
            GetterSetter.setUserObject(patientObject);
            Intent intent = new Intent(ReportsListActivity.this, ReportsAddActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        ReportsObject reportsObject = reportsObjectList.get(position);
        GetterSetter.setReportsObject(reportsObject);
        Intent intent = new Intent(ReportsListActivity.this, ReportsViewActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        if (GetterSetter.isShouldReload()) {
            GetterSetter.setShouldReload(false);
            loadPatientReportsData();
        }
        super.onRestart();
    }

    private void loadPatientReportsData() {
        if (NetworkUtil.isNetworkAvailable(ReportsListActivity.this)) {
            createDialogMethod();
            String userID = patientObject.getUserID();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("PatientReports");
            Query query = databaseReference.orderByChild("patientUserID").equalTo(userID);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    cancelDialogMethod();
                    if (dataSnapshot.exists()) {
                        reportsObjectList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ReportsObject reportsObject = snapshot.getValue(ReportsObject.class);
                            if (reportsObject != null) {
                                reportsObjectList.add(reportsObject);
                            }
                        }
                        if (reportsObjectList.size() > 0) {
                            Collections.sort(reportsObjectList, new ReportsComparable(false));
                            patientReportsAdapter.updateList(reportsObjectList);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    cancelDialogMethod();
                }
            });
        } else {
            Utils.showToast("No Internet Connection Found.");
        }
    }

    private void createDialogMethod() {
        pDialog = new ProgressDialog(ReportsListActivity.this);
        pDialog.setMessage("Loading Data. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void cancelDialogMethod() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.cancel();
        }
    }
}
