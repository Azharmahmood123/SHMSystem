package com.shm.system.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shm.system.R;
import com.shm.system.contants.Globals;
import com.shm.system.entity.ReportsObject;
import com.shm.system.entity.UserObject;
import com.shm.system.getset.GetterSetter;
import com.shm.system.utils.NetworkUtil;
import com.shm.system.utils.Utils;

import java.util.UUID;

public class ReportsAddActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private AppCompatImageView ivBack;
    private AppCompatTextView tvName, tvEmail, tvCnicNo, tvPhone, tvGuardianName, tvGuardianPhoneNo;
    private AppCompatEditText etTestName;
    private AppCompatTextView tvSelectedFile;
    private LinearLayoutCompat llAddFile;
    private LinearLayoutCompat llAddReport;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    private UserObject patientObject;
    private ProgressDialog pDialog;
    private String fileName = "";
    private String testName = "";
    private Uri filePath;
    private String uploadedFilePath = "";

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient_reports);
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

        etTestName = findViewById(R.id.etTestName);
        tvSelectedFile = findViewById(R.id.tvSelectedFile);
        llAddFile = findViewById(R.id.llAddFile);

        llAddReport = findViewById(R.id.llAddReport);
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
        setPatientInfo();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        int userType = Utils.getIntPrefs(Globals.PrefConstants.PREF_USER_TYPE);
        llAddReport.setVisibility(userType == Globals.UserType.DOCTOR ? View.VISIBLE : View.GONE);
    }

    private void attachListeners() {
        ivBack.setOnClickListener(this);
        llAddFile.setOnClickListener(this);
        llAddReport.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            onBackPressed();
        } else if (v == llAddFile) {
            SelectImage();
        } else if (v == llAddReport) {
            if (NetworkUtil.isNetworkAvailable(ReportsAddActivity.this)) {
                addPatientReport();
            } else {
                Utils.showToast("No Internet Connection Found.");
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

    private void createDialogMethod() {
        pDialog = new ProgressDialog(ReportsAddActivity.this);
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

    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            fileName = getFileName(filePath);
        } else {
            filePath = null;
            fileName = "";
        }
        tvSelectedFile.setText(fileName);
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void addPatientReport() {
        testName = "";
        Editable testNameEditable = etTestName.getText();
        if (testNameEditable != null) {
            testName = testNameEditable.toString();
        }
        if (TextUtils.isEmpty(testName)) {
            Utils.showToast("Please enter test name.");
            etTestName.requestFocus();
            return;
        }

        if (fileName.length() == 0) {
            Utils.showToast("Please please select file to upload.");
            return;
        }

        uploadImage();
    }

    private void uploadImage() {
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String chileInfo = "images/" + UUID.randomUUID().toString();
            // Defining the child of storageReference
            StorageReference ref = storageReference.child(chileInfo);

            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                progressDialog.dismiss();
                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(uri -> {
                            uploadedFilePath = uri.toString();
                            addPatientReportAfterUpload();
                        });
                    }
                }
            }).addOnFailureListener(e -> {
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(ReportsAddActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            });
        }
    }

    private void addPatientReportAfterUpload() {
        createDialogMethod();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("PatientReports");
        String reportID = databaseReference.push().getKey();
        if (reportID != null) {
            ReportsObject reportsObject = new ReportsObject();
            reportsObject.setReportID(reportID);
            reportsObject.setPatientUserID(patientObject.getUserID());
            reportsObject.setReportFileName(fileName);
            reportsObject.setReportTestName(testName);
            reportsObject.setReportFilePath(uploadedFilePath);
            String date = Utils.getDateTime(Globals.FORMAT_MODIFIED_DATE);
            reportsObject.setReportDate(date);
            databaseReference.child(reportID).setValue(reportsObject);
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Utils.showToast("Patient report added.");
                    cancelDialogMethod();
                    GetterSetter.setShouldReload(true);
                    ReportsAddActivity.this.finish();
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
}
