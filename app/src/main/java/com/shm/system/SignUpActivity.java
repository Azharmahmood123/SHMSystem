package com.shm.system;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shm.system.activities.DoctorHomeActivity;
import com.shm.system.activities.FirstAidPersonHomeActivity;
import com.shm.system.activities.PatientHomeActivity;
import com.shm.system.contants.Globals;
import com.shm.system.entity.UserObject;
import com.shm.system.utils.NetworkUtil;
import com.shm.system.utils.Utils;


public class  SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    protected AppCompatImageView ivBack;
    private AppCompatEditText etFirstName, etLastName, etEmail, etPassword, etCNICNo, etPhoneNo, etGuardianName, etGuardianPhoneNo;
    private RadioGroup rgUserType;
    private LinearLayoutCompat llSignUp;
    private LinearLayoutCompat llGuardian;

    private ProgressDialog pDialog;
    private FirebaseAuth mAuth;
    private String firstName = "", lastName = "", email = "", password = "", cnicNo = "", phoneNo = "", guardianName = "", guardianPhoneNo = "";
    private int userType = -1;
    private boolean firstTimeCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findView();
        initializeControls();
        attachListeners();
    }

    private void findView() {
        toolbar = findViewById(R.id.toolbar);
        ivBack = findViewById(R.id.ivBack);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etCNICNo = findViewById(R.id.etCNICNo);
        etPhoneNo = findViewById(R.id.etPhoneNo);
        llGuardian = findViewById(R.id.llGuardian);
        etGuardianName = findViewById(R.id.etFatherName);
        etGuardianPhoneNo = findViewById(R.id.etFatherPhoneNo);

        rgUserType = findViewById(R.id.rgUserType);

        llSignUp = findViewById(R.id.llSignUp);
    }

    private void initializeControls() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
            }
        }

        mAuth = FirebaseAuth.getInstance();

//        etFirstName.setText("Azhar");
//        etLastName.setText("Mahmood");
//        etEmail.setText("azhar.mahmood.25dec@gmail.com");
//        etPassword.setText("112233");
//        etCNICNo.setText("12");
//        etPhoneNo.setText("123456789");
//        etGuardianName.setText("Ghulam Rasool");
//        etGuardianPhoneNo.setText("123456789");
    }

    private void attachListeners() {
        ivBack.setOnClickListener(this);
        llSignUp.setOnClickListener(this);
        rgUserType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbPatient) {
                llGuardian.setVisibility(View.VISIBLE);
                userType = Globals.UserType.PATIENT;
            } else if (checkedId == R.id.rbDoctor) {
                llGuardian.setVisibility(View.GONE);
                userType = Globals.UserType.DOCTOR;
            } else if (checkedId == R.id.rbFirstAid) {
                llGuardian.setVisibility(View.GONE);
                userType = Globals.UserType.FIRST_AID_PERSON;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            onBackPressed();
        } else if (v == llSignUp) {
            if (NetworkUtil.isNetworkAvailable(SignUpActivity.this)) {
                signUpMethod();
            } else {
                Utils.showToast("No Internet Connection Found.");
            }
        }
    }

    private void signUpMethod() {
        Editable firstNameEditable, lastNameEditable, emailEditable, passwordEditable, cnicEditable, phoneNoEditable;
        Editable guardianNameEditable, guardianPhoneNoEditable;

        firstNameEditable = etFirstName.getText();
        if (firstNameEditable != null) {
            firstName = firstNameEditable.toString().trim();
        }
        if (TextUtils.isEmpty(firstName)) {
            Utils.showToast("Please Enter First Name.");
            etFirstName.requestFocus();
            return;
        }
        lastNameEditable = etLastName.getText();
        if (lastNameEditable != null) {
            lastName = lastNameEditable.toString().trim();
        }
        if (TextUtils.isEmpty(lastName)) {
            Utils.showToast("Please Enter Last Name.");
            etLastName.requestFocus();
            return;
        }
        emailEditable = etEmail.getText();
        if (emailEditable != null) {
            email = emailEditable.toString().trim();
        }
        if (TextUtils.isEmpty(email)) {
            Utils.showToast("Please Enter Email");
            etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignUpActivity.this, "Please Enter Valid Email Address", Toast.LENGTH_LONG).show();
            etEmail.requestFocus();
            return;
        }
        passwordEditable = etPassword.getText();
        if (passwordEditable != null) {
            password = passwordEditable.toString().trim();
        }
        if (TextUtils.isEmpty(password)) {
            Utils.showToast("Please Enter Password");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(SignUpActivity.this, "Password Should be at Least 6 Digits", Toast.LENGTH_LONG).show();
            etPassword.requestFocus();
            return;
        }
        cnicEditable = etCNICNo.getText();
        if (cnicEditable != null) {
            cnicNo = cnicEditable.toString().trim();
        }
        if (TextUtils.isEmpty(cnicNo)) {
            Utils.showToast("Please Enter CNIC No.");
            etCNICNo.requestFocus();
            return;
        }
        phoneNoEditable = etPhoneNo.getText();
        if (phoneNoEditable != null) {
            phoneNo = phoneNoEditable.toString().trim();
        }
        if (TextUtils.isEmpty(phoneNo)) {
            Utils.showToast("Please Enter Phone No.");
            etPhoneNo.requestFocus();
            return;
        }

        if (userType == -1) {
            Utils.showToast("Please Select User Type.");
            return;
        }

        if (userType == Globals.UserType.PATIENT) {
            guardianNameEditable = etGuardianName.getText();
            if (guardianNameEditable != null) {
                guardianName = guardianNameEditable.toString().trim();
            }
            if (TextUtils.isEmpty(guardianName)) {
                Utils.showToast("Please Enter Father/Guardian Name.");
                etGuardianName.requestFocus();
                return;
            }

            guardianPhoneNoEditable = etGuardianPhoneNo.getText();
            if (guardianPhoneNoEditable != null) {
                guardianPhoneNo = guardianPhoneNoEditable.toString().trim();
            }
            if (TextUtils.isEmpty(guardianPhoneNo)) {
                Utils.showToast("Please Enter Father/Guardian Phone No.");
                etGuardianPhoneNo.requestFocus();
                return;
            }
        }

        createDialogMethod();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    if (firebaseUser != null) {
                        registerUser(firebaseUser.getUid());
                    }
                }
            } else {
                cancelDialogMethod();
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Utils.showToast("This email address already exists.");
                } else {
                    Utils.showToast("Something went wrong, Please try again!");
                }
            }
        });
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

    private void registerUser(String userID) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        UserObject userObject = new UserObject();
        userObject.setUserID(userID);
        userObject.setFirstName(firstName);
        userObject.setLastName(lastName);
        userObject.setEmail(email);
        userObject.setCnicNo(cnicNo);
        userObject.setPhoneNo(phoneNo);
        userObject.setGuardianName(guardianName);
        userObject.setGuardianPhoneNo(guardianPhoneNo);
        userObject.setUserType(userType);
        databaseReference.child(userID).setValue(userObject);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (!firstTimeCalled) {
                    firstTimeCalled = true;
                    cancelDialogMethod();
                    Utils.showToast("User created successfully.");
                    saveUserInfo(userObject);
                    Intent intent;
                    if (userObject.getUserType() == Globals.UserType.PATIENT) {
                        intent = new Intent(SignUpActivity.this, PatientHomeActivity.class);
                    } else if (userObject.getUserType() == Globals.UserType.DOCTOR) {
                        intent = new Intent(SignUpActivity.this, DoctorHomeActivity.class);
                    } else {
                        intent = new Intent(SignUpActivity.this, FirstAidPersonHomeActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    SignUpActivity.this.finish();
                }
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

    private void createDialogMethod() {
        pDialog = new ProgressDialog(SignUpActivity.this);
        pDialog.setMessage("Sign Up. Please wait...");
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
