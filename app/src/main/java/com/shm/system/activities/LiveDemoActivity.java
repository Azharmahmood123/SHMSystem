package com.shm.system.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shm.system.R;
import com.shm.system.adapter.PatientLiveDemoAdapter;
import com.shm.system.entity.LiveDemoObject;
import com.shm.system.utils.NetworkUtil;
import com.shm.system.utils.Utils;
import com.shm.system.view.SnappingLinearLayoutManager;
import com.shm.system.view.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class LiveDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private AppCompatImageView ivBack;
    private RecyclerView recyclerView;

    private PatientLiveDemoAdapter patientLiveDemoAdapter;
    private final List<LiveDemoObject> liveDemoObjectList = new ArrayList<>();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_demo);
        findView();
        initializeControls();
        attachListeners();
    }

    private void findView() {
        toolbar = findViewById(R.id.toolbar);
        ivBack = toolbar.findViewById(R.id.ivBack);

        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initializeControls() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
            }
        }

        SnappingLinearLayoutManager mLayoutManager = new SnappingLinearLayoutManager(LiveDemoActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(LiveDemoActivity.this));
        patientLiveDemoAdapter = new PatientLiveDemoAdapter(liveDemoObjectList);
        recyclerView.setAdapter(patientLiveDemoAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_ten);
        recyclerView.addItemDecoration(new SpacesItemDecoration(1, spacingInPixels, true));

        loadLiveData();
    }

    private void attachListeners() {
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            onBackPressed();
        }
    }

    private void createDialogMethod() {
        pDialog = new ProgressDialog(LiveDemoActivity.this);
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

    private void loadLiveData() {
        if (NetworkUtil.isNetworkAvailable(LiveDemoActivity.this)) {
            createDialogMethod();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("ESP32");
            Query query = databaseReference.child("123456").limitToLast(10);
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    cancelDialogMethod();
                    LiveDemoObject liveDemoObject = snapshot.getValue(LiveDemoObject.class);
                    if (liveDemoObject != null) {
                        liveDemoObjectList.add(liveDemoObject);
                        patientLiveDemoAdapter.notifyItemInserted(liveDemoObjectList.size());
                        recyclerView.smoothScrollToPosition(liveDemoObjectList.size());
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
        } else {
            Utils.showToast("No Internet Connection Found.");
        }
    }
}
