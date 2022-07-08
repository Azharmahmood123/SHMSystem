package com.shm.system.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.shm.system.R;
import com.shm.system.entity.ReportsObject;
import com.shm.system.entity.TreatmentObject;

import java.util.List;

public class PatientReportsAdapter extends RecyclerView.Adapter<PatientReportsAdapter.PatientsTreatedViewHolder> {

    private List<ReportsObject> reportsObjectList;
    private final OnItemClickListener onItemClickListener;

    public PatientReportsAdapter(List<ReportsObject> reportsObjectList, OnItemClickListener onItemClickListener) {
        this.reportsObjectList = reportsObjectList;
        this.onItemClickListener = onItemClickListener;
    }

    public void updateList(List<ReportsObject> treatmentObjectList) {
        this.reportsObjectList = treatmentObjectList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PatientsTreatedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_reports, parent, false);
        return new PatientsTreatedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientsTreatedViewHolder holder, int position) {
        ReportsObject reportsObject = reportsObjectList.get(position);
        holder.bind(reportsObject);
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(v, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return reportsObjectList.size();
    }

    public static class PatientsTreatedViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView tvReportDate;
        private final AppCompatTextView tvTestName;

        public PatientsTreatedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReportDate = itemView.findViewById(R.id.tvReportDate);
            tvTestName = itemView.findViewById(R.id.tvTestName);
        }

        void bind(ReportsObject reportsObject) {
            tvReportDate.setText(reportsObject.getReportDate());
            tvTestName.setText(reportsObject.getReportTestName());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
