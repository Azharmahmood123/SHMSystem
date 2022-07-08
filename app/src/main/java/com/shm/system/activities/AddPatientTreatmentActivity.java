package com.shm.system.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shm.system.R;
import com.shm.system.contants.Globals;
import com.shm.system.dialogs.Dialogs;
import com.shm.system.dialogs.NotifyParentsDialog;
import com.shm.system.entity.TreatmentObject;
import com.shm.system.entity.UserObject;
import com.shm.system.getset.GetterSetter;
import com.shm.system.utils.NetworkUtil;
import com.shm.system.utils.Utils;

public class AddPatientTreatmentActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private AppCompatImageView ivBack;
    private AppCompatTextView tvName, tvEmail, tvCnicNo, tvPhone, tvGuardianName, tvGuardianPhoneNo;
    private LinearLayoutCompat llForFirstAid, llForDoctor;
    private AppCompatEditText etReasonForAccident, etKindOfInjury, etTypeOfDisease, etTreatment;
    private LinearLayoutCompat llAddPatient;
    private LinearLayoutCompat llNotifyParents;

    private ProgressDialog pDialog;
    private UserObject patientObject;
    private String guardianPhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient_treatment);
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

        llForFirstAid = findViewById(R.id.llForFirstAid);
        llForDoctor = findViewById(R.id.llForDoctor);
        etReasonForAccident = findViewById(R.id.etReasonForAccident);
        etKindOfInjury = findViewById(R.id.etKindOfInjury);
        etTypeOfDisease = findViewById(R.id.etTypeOfDisease);
        etTreatment = findViewById(R.id.etTreatment);

        llAddPatient = findViewById(R.id.llAddPatient);
        llNotifyParents = findViewById(R.id.llNotifyParents);
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
        guardianPhoneNo = patientObject.getGuardianPhoneNo();
        setPatientInfo();

        int userType = Utils.getIntPrefs(Globals.PrefConstants.PREF_USER_TYPE);
        if (userType == Globals.UserType.FIRST_AID_PERSON) {
            llForFirstAid.setVisibility(View.VISIBLE);
            llForDoctor.setVisibility(View.GONE);
        } else {
            llForFirstAid.setVisibility(View.GONE);
            llForDoctor.setVisibility(View.VISIBLE);
        }

//        etReasonForAccident.setText("Bike Accident");
//        etKindOfInjury.setText("Head Injury");
//        etTreatment.setText("BP checkup, Other Treatment.");
    }

    private void attachListeners() {
        ivBack.setOnClickListener(this);
        llAddPatient.setOnClickListener(this);
        llNotifyParents.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == llAddPatient) {
            if (NetworkUtil.isNetworkAvailable(this)) {
                addPatientTreatment();
            } else {
                Utils.showToast("No Internet Connection Found.");
            }
        } else if (v == llNotifyParents) {
            btnCheckSMSMethod();
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.SEND_SMS
    };

    public void btnCheckSMSMethod() {
        if (shouldAskPermissions()) {
            int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } else {
                checkSMS();
            }
        } else {
            checkSMS();
        }
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSMS();
            } else {
                Dialogs.showAlertWithOneButton(this, "Functionality Limited",
                        "You will not able to notify via sms.", getString(android.R.string.ok),
                        false, null);
            }
        }
    }

    private void checkSMS() {
        Dialogs.showAlertWithTwoButton(AddPatientTreatmentActivity.this,
                "Are you sure, you want to notify patient guardian about accident?",
                "Notify Parents", "Notify", "Cancel",
                new Dialogs.OnDialogButtonClickListener() {
                    @Override
                    public void onDialogPositiveButtonClick() {
                        NotifyParentsDialog notifyParentsDialog = new NotifyParentsDialog(AddPatientTreatmentActivity.this,
                                guardianPhoneNo, new NotifyParentsDialog.OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveButtonClick(String text) {
                                if (text.length() > 0) {
                                    sendSMS(guardianPhoneNo, text);
                                } else {
                                    Utils.showToast("Please enter message");
                                }
                            }

                            @Override
                            public void onNegativeButtonClick() {

                            }
                        });
                        notifyParentsDialog.show(getSupportFragmentManager(), "");

                    }

                    @Override
                    public void onDialogNegativeButtonClick() {

                    }
                });
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
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

    private void addPatientTreatment() {
        String reasonForAccident = "";
        String kindOfInjury = "";
        String typeOfDisease = "";
        int userType = Utils.getIntPrefs(Globals.PrefConstants.PREF_USER_TYPE);
        if (userType == Globals.UserType.FIRST_AID_PERSON) {
            Editable reasonForAccidentEditable = etReasonForAccident.getText();
            if (reasonForAccidentEditable != null) {
                reasonForAccident = reasonForAccidentEditable.toString();
            }
            if (TextUtils.isEmpty(reasonForAccident)) {
                Utils.showToast("Please enter reason for accident");
                etReasonForAccident.requestFocus();
                return;
            }
            Editable kindOfInjuryEditable = etKindOfInjury.getText();
            if (kindOfInjuryEditable != null) {
                kindOfInjury = kindOfInjuryEditable.toString();
            }
            if (TextUtils.isEmpty(kindOfInjury)) {
                Utils.showToast("Please enter kind of injury");
                etKindOfInjury.requestFocus();
                return;
            }
        } else {
            Editable typeOFDiseaseEditable = etTypeOfDisease.getText();
            if (typeOFDiseaseEditable != null) {
                typeOfDisease = typeOFDiseaseEditable.toString();
            }
            if (TextUtils.isEmpty(typeOfDisease)) {
                Utils.showToast("Please enter type of disease");
                etTypeOfDisease.requestFocus();
                return;
            }
        }

        String treatmentDetail = "";
        Editable treatmentDetailEditable = etTreatment.getText();
        if (treatmentDetailEditable != null) {
            treatmentDetail = treatmentDetailEditable.toString();
        }
        if (TextUtils.isEmpty(treatmentDetail)) {
            Utils.showToast("Please enter treatment detail done on site.");
            etTreatment.requestFocus();
            return;
        }


        createDialogMethod();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("PatientTreatments");
        String treatmentID = databaseReference.push().getKey();
        if (treatmentID != null) {
            TreatmentObject treatmentObject = new TreatmentObject();
            treatmentObject.setTreatmentID(treatmentID);
            String treatmentDate = Utils.getDateTime(Globals.FORMAT_MODIFIED_DATE);
            treatmentObject.setTreatmentDate(treatmentDate);
            treatmentObject.setPatientUserID(patientObject.getUserID());
            String patientName = patientObject.getFirstName() + " " + patientObject.getLastName();
            treatmentObject.setPatientName(patientName);

            if (userType == Globals.UserType.FIRST_AID_PERSON) {
                treatmentObject.setFirstAidPersonUserID(Utils.getStringPrefs(Globals.PrefConstants.PREF_USER_ID));
                treatmentObject.setDoctorUserID("");
                treatmentObject.setReasonForAccident(reasonForAccident);
                treatmentObject.setKindOfInjury(kindOfInjury);
            } else {
                treatmentObject.setDoctorUserID(Utils.getStringPrefs(Globals.PrefConstants.PREF_USER_ID));
                treatmentObject.setFirstAidPersonUserID("");
                treatmentObject.setTypeOFDisease(typeOfDisease);
            }
            String treatedByName = Utils.getStringPrefs(Globals.PrefConstants.PREF_FIRST_NAME) + " " + Utils.getStringPrefs(Globals.PrefConstants.PREF_LAST_NAME);
            treatmentObject.setTreatedByName(treatedByName);

            treatmentObject.setTreatmentDetail(treatmentDetail);
            databaseReference.child(treatmentID).setValue(treatmentObject);
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Utils.showToast("Patient treatment added.");
                    cancelDialogMethod();
                    GetterSetter.setShouldReload(true);
                    AddPatientTreatmentActivity.this.finish();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    cancelDialogMethod();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    cancelDialogMethod();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    cancelDialogMethod();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    cancelDialogMethod();
                }
            });
        }
    }

    private void createDialogMethod() {
        pDialog = new ProgressDialog(AddPatientTreatmentActivity.this);
        pDialog.setMessage("Posting Data. Please wait...");
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
