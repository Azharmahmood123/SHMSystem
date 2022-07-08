package com.shm.system.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.ortiz.touchview.TouchImageView;
import com.shm.system.R;
import com.shm.system.entity.ReportsObject;
import com.shm.system.getset.GetterSetter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ReportsViewActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private AppCompatImageView ivBack;
    private TouchImageView ivImageView;
    private AppCompatTextView tvTitle;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_report);
        findView();
        initializeControls();
        attachListeners();
    }

    private void findView() {
        toolbar = findViewById(R.id.toolbar);
        ivBack = toolbar.findViewById(R.id.ivBack);

        ivImageView = findViewById(R.id.ivImageView);
        tvTitle = findViewById(R.id.tvTitle);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initializeControls() {

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
            }
        }

        ReportsObject reportsObject = GetterSetter.getReportsObject();

        if (reportsObject != null) {
            tvTitle.setText(reportsObject.getReportTestName());
            String url = reportsObject.getReportFilePath();
            Picasso.with(ReportsViewActivity.this)
                    .load(url)
                    .into(ivImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
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
}
