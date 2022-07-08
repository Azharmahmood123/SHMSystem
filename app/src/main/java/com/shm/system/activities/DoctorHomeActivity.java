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
import com.shm.system.dialogs.AddPatientDialog;
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

public class DoctorHomeActivity extends AppCompatActivity implements View.OnClickListener, PatientsTreatedAdapter.OnItemClickListener {

    private Toolbar toolbar;
    private AppCompatTextView tvLogout;

    private AppCompatTextView tvName, tvEmail, tvCnicNo, tvPhone;
    private LinearLayoutCompat llAddPatient;
    private RecyclerView recyclerView;

    private PatientsTreatedAdapter patientsTreatedAdapter;
    private ProgressDialog pDialog;
    private final List<TreatmentObject> treatmentObjectList = new ArrayList<>();
    private UserObject patientObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);
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

        recyclerView = findViewById(R.id.recyclerView);

        llAddPatient = findViewById(R.id.llAddPatient);
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

        recyclerView.setLayoutManager(new LinearLayoutManager(DoctorHomeActivity.this));
        patientsTreatedAdapter = new PatientsTreatedAdapter(treatmentObjectList, DoctorHomeActivity.this);
        recyclerView.setAdapter(patientsTreatedAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_ten);
        recyclerView.addItemDecoration(new SpacesItemDecoration(1, spacingInPixels, true));
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        loadDoctorTreatedPatients();
    }

    private void attachListeners() {
        tvLogout.setOnClickListener(this);
        llAddPatient.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == tvLogout) {
            Dialogs.showAlertWithTwoButton(DoctorHomeActivity.this, "Are you sure, you want to logout?", "Logout",
                    "Logout", "Cancel", new Dialogs.OnDialogButtonClickListener() {
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
                            Intent intent = new Intent(DoctorHomeActivity.this, SignInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            DoctorHomeActivity.this.finish();
                        }

                        @Override
                        public void onDialogNegativeButtonClick() {

                        }
                    });
        } else if (v == llAddPatient) {
            AddPatientDialog addPatientDialog = new AddPatientDialog(DoctorHomeActivity.this, new AddPatientDialog.OnDialogButtonClickListener() {
                @Override
                public void onPositiveButtonClick(String text) {
                    if (text.length() > 0)
                        checkUserAlreadyAvailable(text);
                    else
                        Utils.showToast("Please Enter CNIC No.");
                }

                @Override
                public void onNegativeButtonClick() {

                }
            });
            addPatientDialog.show(getSupportFragmentManager(), "Add_Patient");
        }
    }

    @Override
    protected void onRestart() {
        if (GetterSetter.isShouldReload()) {
            GetterSetter.setShouldReload(false);
            loadDoctorTreatedPatients();
        }
        super.onRestart();
    }

    @Override
    public void onItemClick(View view, int position) {
        TreatmentObject treatmentObject = treatmentObjectList.get(position);
        GetterSetter.setTreatmentObject(treatmentObject);
        Intent intent = new Intent(DoctorHomeActivity.this, PatientTreatmentDetailActivity.class);
        startActivity(intent);
    }

    private void updateUserInfo() {
        String patientName = Utils.getStringPrefs(Globals.PrefConstants.PREF_FIRST_NAME) + " " + Utils.getStringPrefs(Globals.PrefConstants.PREF_LAST_NAME);
        String patientEmail = Utils.getStringPrefs(Globals.PrefConstants.PREF_EMAIL);
        String patientCNICNo = Utils.getStringPrefs(Globals.PrefConstants.PREF_CNIC_NO);
        String patientPhoneNo = Utils.getStringPrefs(Globals.PrefConstants.PREF_PHONE_NO);
        tvName.setText(patientName);
        tvEmail.setText(patientEmail);
        tvCnicNo.setText(patientCNICNo);
        tvPhone.setText(patientPhoneNo);
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
        pDialog = new ProgressDialog(DoctorHomeActivity.this);
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

    private void loadDoctorTreatedPatients() {
        if (NetworkUtil.isNetworkAvailable(DoctorHomeActivity.this)) {
            createDialogMethod();
            String userID = Utils.getStringPrefs(Globals.PrefConstants.PREF_USER_ID);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("PatientTreatments");
            Query query = databaseReference.orderByChild("doctorUserID").equalTo(userID);
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
        if (NetworkUtil.isNetworkAvailable(DoctorHomeActivity.this)) {
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

    private void checkUserAlreadyAvailable(String cnicNo) {
        if (NetworkUtil.isNetworkAvailable(DoctorHomeActivity.this)) {
            createDialogMethod();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
            Query query = databaseReference.orderByChild("cnicNo").equalTo(cnicNo);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                        if (snapshot != null) {
                            patientObject = snapshot.getValue(UserObject.class);
                            if (patientObject != null) {
                                if (patientObject.getUserType() == Globals.UserType.PATIENT) {
                                    checkPatientTreatmentHistory();
                                } else {
                                    cancelDialogMethod();
                                    Utils.showToast("No Record found with given CNIC No.");
                                }
                            } else {
                                cancelDialogMethod();
                                Utils.showToast("No Record found with given CNIC No.");
                            }
                        } else {
                            cancelDialogMethod();
                            Utils.showToast("No Record found with given CNIC No.");
                        }
                    } else {
                        cancelDialogMethod();
                        Utils.showToast("No Record found with given CNIC No.");
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

    private void checkPatientTreatmentHistory() {
        if (NetworkUtil.isNetworkAvailable(DoctorHomeActivity.this)) {
            String userID = patientObject.getUserID();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("PatientTreatments");
            Query query = databaseReference.orderByChild("patientUserID").equalTo(userID);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    cancelDialogMethod();
                    if (dataSnapshot.exists()) {
                        List<TreatmentObject> treatmentObjects = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            TreatmentObject treatmentObject = snapshot.getValue(TreatmentObject.class);
                            if (treatmentObject != null) {
                                treatmentObjects.add(treatmentObject);
                            }
                        }
                        if (treatmentObjects.size() > 0) {
                            GetterSetter.setUserObject(patientObject);
                            GetterSetter.setTreatmentObjectList(treatmentObjects);
                            Intent intent = new Intent(DoctorHomeActivity.this, PatientTreatmentHistoryActivity.class);
                            startActivity(intent);
                        } else {
                            showNoHistoryFoundDialog();
                        }
                    } else {
                        showNoHistoryFoundDialog();
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

    private void showNoHistoryFoundDialog() {
        Dialogs.showAlertWithTwoButton(DoctorHomeActivity.this,
                "No previous treatment history found, Do you want to add new treatment history?",
                "Add New Treatment", "Add New", "Cancel",
                new Dialogs.OnDialogButtonClickListener() {
                    @Override
                    public void onDialogPositiveButtonClick() {
                        GetterSetter.setUserObject(patientObject);
                        Intent intent = new Intent(DoctorHomeActivity.this, AddPatientTreatmentActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onDialogNegativeButtonClick() {

                    }
                });
    }
}
