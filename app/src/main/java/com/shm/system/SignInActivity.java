package com.shm.system;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shm.system.activities.DoctorHomeActivity;
import com.shm.system.activities.FirstAidPersonHomeActivity;
import com.shm.system.activities.PatientHomeActivity;
import com.shm.system.contants.Globals;
import com.shm.system.entity.UserObject;
import com.shm.system.utils.NetworkUtil;
import com.shm.system.utils.Utils;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatEditText etCNICNo, etEmail, etPassword;
    private LinearLayoutCompat llSignIn;
    private AppCompatTextView tvSignUp;

    private ProgressDialog pDialog;
    private FirebaseAuth mAuth;

    private String email = "";
    private String password = "";
    private String cnicNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        findView();
        initializeControls();
        attachListeners();
    }

    private void findView() {
        etCNICNo = findViewById(R.id.etCNICNo);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        llSignIn = findViewById(R.id.llSignIn);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void initializeControls() {
        mAuth = FirebaseAuth.getInstance();

//        etEmail.setText("azhar.mahmood.25dec1@gmail.com");
//        etPassword.setText("112233");
//        etCNICNo.setText("123");
    }

    private void attachListeners() {
        llSignIn.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == llSignIn) {
            if (NetworkUtil.isNetworkAvailable(SignInActivity.this)) {
                callLoginMethod();
            } else {
                Utils.showToast("No Internet Connection Found.");
            }
        } else if (v == tvSignUp) {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    }

    private void callLoginMethod() {
        Editable emailEditable = etEmail.getText();
        if (emailEditable != null) {
            email = etEmail.getText().toString().trim();
        }
        if (TextUtils.isEmpty(email)) {
            Utils.showToast("Please Enter Email");
            etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignInActivity.this, "Please Enter Valid Email Address", Toast.LENGTH_LONG).show();
            etEmail.requestFocus();
            return;
        }
        Editable passwordEditable = etPassword.getText();
        if (passwordEditable != null) {
            password = passwordEditable.toString().trim();
        }
        if (TextUtils.isEmpty(password)) {
            Utils.showToast("Please Enter Password");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(SignInActivity.this, "Password Should be at Least 6 Digits", Toast.LENGTH_LONG).show();
            etPassword.requestFocus();
            return;
        }

        Editable cnicEditable = etCNICNo.getText();
        if (cnicEditable != null) {
            cnicNo = cnicEditable.toString().trim();
        }
        if (TextUtils.isEmpty(cnicNo)) {
            Utils.showToast("Please Enter CNIC No.");
            etCNICNo.requestFocus();
            return;
        }

        createDialogMethod();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AuthResult authResult = task.getResult();
                if (authResult != null) {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        getUserInfo(firebaseUser.getUid());
                    }
                }
            } else {
                cancelDialogMethod();
                Utils.showToast("Wrong credentials, Please try again!");
            }
        });
    }

    private void getUserInfo(String userID) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cancelDialogMethod();
                if (dataSnapshot.exists()) {
                    UserObject userObject = dataSnapshot.getValue(UserObject.class);
                    if (userObject != null) {
                        if (userObject.getCnicNo().equalsIgnoreCase(cnicNo)) {
                            saveUserInfo(userObject);
                            Intent intent;
                            if (userObject.getUserType() == Globals.UserType.PATIENT) {
                                intent = new Intent(SignInActivity.this, PatientHomeActivity.class);
                            } else if (userObject.getUserType() == Globals.UserType.DOCTOR) {
                                intent = new Intent(SignInActivity.this, DoctorHomeActivity.class);
                            } else {
                                intent = new Intent(SignInActivity.this, FirstAidPersonHomeActivity.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            SignInActivity.this.finish();
                        } else {
                            Utils.showToast("wrong CNIC No entered.");
                            mAuth.signOut();
                        }
                    } else {
                        mAuth.signOut();
                        Utils.showToast("wrong CNIC No entered.");
                    }
                } else {
                    mAuth.signOut();
                    Utils.showToast("wrong CNIC No entered.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                cancelDialogMethod();
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

    private void createDialogMethod() {
        pDialog = new ProgressDialog(SignInActivity.this);
        pDialog.setMessage("Signing in. Please wait...");
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
