package com.shm.system.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shm.system.R;
import com.shm.system.SignInActivity;
import com.shm.system.adapter.PatientsTreatedAdapter;
import com.shm.system.contants.Globals;
import com.shm.system.dialogs.Dialogs;
import com.shm.system.entity.TreatmentObject;
import com.shm.system.entity.UserObject;
import com.shm.system.getset.GetterSetter;
import com.shm.system.utils.NetworkUtil;
import com.shm.system.utils.PatientComparable;
import com.shm.system.utils.Utils;
import com.shm.system.view.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatientHomeActivity extends AppCompatActivity implements View.OnClickListener, PatientsTreatedAdapter.OnItemClickListener {

    private Toolbar toolbar;
    private AppCompatTextView tvLogout;
    private AppCompatTextView tvName, tvEmail, tvCnicNo, tvPhone, tvGuardianName, tvGuardianPhoneNo;
    private RecyclerView recyclerView;
    private LinearLayoutCompat llLiveDemo, llReports;

    private PatientsTreatedAdapter patientsTreatedAdapter;
    private ProgressDialog pDialog;
    private final List<TreatmentObject> treatmentObjectList = new ArrayList<>();
    private UserObject patientObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);
        findView();
        initializeControls();
        attachListeners();
    }

    private void findView() {
        toolbar = findViewById(R.id.toolbar);
        tvLogout = toolbar.findViewById(R.id.tvLogout);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvCnicNo = findViewById(R.id.tvCnicNo);
        tvPhone = findViewById(R.id.tvPhone);
        tvGuardianName = findViewById(R.id.tvGuardianName);
        tvGuardianPhoneNo = findViewById(R.id.tvGuardianPhoneNo);

        recyclerView = findViewById(R.id.recyclerView);

        llLiveDemo = findViewById(R.id.llLiveDemo);
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

        updateUserInfo();
        userInfoListener();

        recyclerView.setLayoutManager(new LinearLayoutManager(PatientHomeActivity.this));
        patientsTreatedAdapter = new PatientsTreatedAdapter(treatmentObjectList, PatientHomeActivity.this);
        recyclerView.setAdapter(patientsTreatedAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_ten);
        recyclerView.addItemDecoration(new SpacesItemDecoration(1, spacingInPixels, true));

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        loadPatientTreatmentHistoryData();
    }

    private void attachListeners() {
        tvLogout.setOnClickListener(this);
        llLiveDemo.setOnClickListener(this);
        llReports.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == tvLogout) {
            Dialogs.showAlertWithTwoButton(PatientHomeActivity.this,
                    "Are you sure, you want to logout?", "Logout",
                    "Logout", "Cancel",
                    new Dialogs.OnDialogButtonClickListener() {
                        @Override
                        public void onDialogPositiveButtonClick() {
                            FirebaseAuth.getInstance().signOut();
                            Utils.setPrefs(Globals.PrefConstants.PREF_USER_ID, "");
                            Utils.setPrefs(Globals.PrefConstants.PREF_FIRST_NAME, "");
                            Utils.setPrefs(Globals.PrefConstants.PREF_LAST_NAME, "");
                            Utils.setPrefs(Globals.PrefConstants.PREF_EMAIL, "");
                            Utils.setPrefs(Globals.PrefConstants.PREF_CNIC_NO, "");
                            Utils.setPrefs(Globals.PrefConstants.PREF_PHONE_NO, "");
                            Utils.setPrefs(Globals.PrefConstants.PREF_GUARDIAN_NAME, "");
                            Utils.setPrefs(Globals.PrefConstants.PREF_GUARDIAN_PHONE_NO, "");
                            Utils.setPrefs(Globals.PrefConstants.PREF_USER_TYPE, -1);
                            Intent intent = new Intent(PatientHomeActivity.this, SignInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            PatientHomeActivity.this.finish();
                        }

                        @Override
                        public void onDialogNegativeButtonClick() {

                        }
                    });
        } else if (v == llLiveDemo) {
            Intent intent = new Intent(PatientHomeActivity.this, LiveDemoActivity.class);
            startActivity(intent);
        } else if (v == llReports) {
            if (patientObject != null) {
                GetterSetter.setUserObject(patientObject);
                Intent intent = new Intent(PatientHomeActivity.this, ReportsListActivity.class);
                startActivity(intent);
            } else {
                Utils.showToast("Patient detail not found.");
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        TreatmentObject treatmentObject = treatmentObjectList.get(position);
        GetterSetter.setTreatmentObject(treatmentObject);
        Intent intent = new Intent(PatientHomeActivity.this, PatientTreatmentDetailActivity.class);
        startActivity(intent);
    }

    private void updateUserInfo() {
        String patientName = Utils.getStringPrefs(Globals.PrefConstants.PREF_FIRST_NAME) + " " + Utils.getStringPrefs(Globals.PrefConstants.PREF_LAST_NAME);
        String patientEmail = Utils.getStringPrefs(Globals.PrefConstants.PREF_EMAIL);
        String patientCNICNo = Utils.getStringPrefs(Globals.PrefConstants.PREF_CNIC_NO);
        String patientPhoneNo = Utils.getStringPrefs(Globals.PrefConstants.PREF_PHONE_NO);
        String patientGuardianName = Utils.getStringPrefs(Globals.PrefConstants.PREF_GUARDIAN_NAME);
        String patientGuardianPhoneNo = Utils.getStringPrefs(Globals.PrefConstants.PREF_GUARDIAN_PHONE_NO);
        patientObject = new UserObject();
        patientObject.setUserID(Utils.getStringPrefs(Globals.PrefConstants.PREF_USER_ID));
        patientObject.setFirstName(Utils.getStringPrefs(Globals.PrefConstants.PREF_FIRST_NAME));
        patientObject.setLastName(Utils.getStringPrefs(Globals.PrefConstants.PREF_LAST_NAME));
        patientObject.setEmail(Utils.getStringPrefs(Globals.PrefConstants.PREF_EMAIL));
        patientObject.setCnicNo(Utils.getStringPrefs(Globals.PrefConstants.PREF_CNIC_NO));
        patientObject.setPhoneNo(Utils.getStringPrefs(Globals.PrefConstants.PREF_PHONE_NO));
        patientObject.setGuardianName(Utils.getStringPrefs(Globals.PrefConstants.PREF_GUARDIAN_NAME));
        patientObject.setGuardianPhoneNo(Utils.getStringPrefs(Globals.PrefConstants.PREF_GUARDIAN_PHONE_NO));
        patientObject.setUserType(Utils.getIntPrefs(Globals.PrefConstants.PREF_USER_TYPE));

        tvName.setText(patientName);
        tvEmail.setText(patientEmail);
        tvCnicNo.setText(patientCNICNo);
        tvPhone.setText(patientPhoneNo);
        tvGuardianName.setText(patientGuardianName);
        tvGuardianPhoneNo.setText(patientGuardianPhoneNo);
    }

    private void saveUserInfo(UserObject userObject) {
        Utils.setPrefs(Globals.PrefConstants.PREF_USER_ID, userObject.getUserID());
        Utils.setPrefs(Globals.PrefConstants.PREF_FIRST_NAME, userObject.getFirstName());
        Utils.setPrefs(Globals.PrefConstants.PREF_LAST_NAME, userObject.getLastName());
        Utils.setPrefs(Globals.PrefConstants.PREF_EMAIL, userObject.getEmail());
        Utils.setPrefs(Globals.PrefConstants.PREF_CNIC_NO, userObject.getCnicNo());
        Utils.setPrefs(Globals.PrefConstants.PREF_PHONE_NO, userObject.getPhoneNo());
        Utils.setPrefs(Globals.PrefConstants.PREF_GUARDIAN_NAME, userObject.getGuardianName());
        Utils.setPrefs(Globals.PrefConstants.PREF_GUARDIAN_PHONE_NO, userObject.getGuardianPhoneNo());
        Utils.setPrefs(Globals.PrefConstants.PREF_USER_TYPE, userObject.getUserType());
    }

    private void createDialogMethod() {
        pDialog = new ProgressDialog(PatientHomeActivity.this);
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

    private void loadPatientTreatmentHistoryData() {
        if (NetworkUtil.isNetworkAvailable(PatientHomeActivity.this)) {
            createDialogMethod();
            String userID = Utils.getStringPrefs(Globals.PrefConstants.PREF_USER_ID);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("PatientTreatments");
            Query query = databaseReference.orderByChild("patientUserID").equalTo(userID);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    cancelDialogMethod();
                    if (dataSnapshot.exists()) {
                        treatmentObjectList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            TreatmentObject treatmentObject = snapshot.getValue(TreatmentObject.class);
                            if (treatmentObject != null) {
                                Log.d("TAG", treatmentObject.getTreatmentDate());
                                treatmentObjectList.add(treatmentObject);
                            }
                        }
                        if (treatmentObjectList.size() > 0) {
                            Collections.sort(treatmentObjectList, new PatientComparable(false));
                            patientsTreatedAdapter.updateList(treatmentObjectList);
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

    private void userInfoListener() {
        if (NetworkUtil.isNetworkAvailable(PatientHomeActivity.this)) {
            String userID = Utils.getStringPrefs(Globals.PrefConstants.PREF_USER_ID);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(userID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserObject userObject = dataSnapshot.getValue(UserObject.class);
                    if (userObject != null) {
                        saveUserInfo(userObject);
                        updateUserInfo();
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
