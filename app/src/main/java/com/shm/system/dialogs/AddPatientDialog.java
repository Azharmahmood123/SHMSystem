package com.shm.system.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.shm.system.R;


public class AddPatientDialog extends DialogFragment {

    private final Context context;
    private final OnDialogButtonClickListener onDialogButtonClickListener;

    protected FragmentActivity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (FragmentActivity) context;
        }
    }

    public AddPatientDialog(Context context, OnDialogButtonClickListener onDialogButtonClickListener) {
        this.context = context;
        this.onDialogButtonClickListener = onDialogButtonClickListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.dialog_goto_detail, null);
        final AppCompatEditText etGotoText = view.findViewById(R.id.etGotoText);
        dialogBuilder.setView(view);
        dialogBuilder.setPositiveButton("Search", (dialog, which) -> {
            String text = "";
            Editable editable = etGotoText.getText();
            if (editable != null) {
                text = editable.toString();
            }
            onDialogButtonClickListener.onPositiveButtonClick(text);
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> onDialogButtonClickListener.onNegativeButtonClick());
        AlertDialog dialog = dialogBuilder.create();
        dialog.setTitle("Search Patient");
        return dialog;
    }

    public interface OnDialogButtonClickListener {
        void onPositiveButtonClick(String text);

        void onNegativeButtonClick();
    }
}
