package com.shm.system.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.shm.system.R;
import com.shm.system.entity.TreatmentObject;

import java.util.List;

public class PatientsTreatedAdapter extends RecyclerView.Adapter<PatientsTreatedAdapter.PatientsTreatedViewHolder> {

    private List<TreatmentObject> treatmentObjectList;
    private final OnItemClickListener onItemClickListener;

    public PatientsTreatedAdapter(List<TreatmentObject> treatmentObjectList, OnItemClickListener onItemClickListener) {
        this.treatmentObjectList = treatmentObjectList;
        this.onItemClickListener = onItemClickListener;
    }

    public void updateList(List<TreatmentObject> treatmentObjectList) {
        this.treatmentObjectList = treatmentObjectList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PatientsTreatedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treated_patients, parent, false);
        return new PatientsTreatedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientsTreatedViewHolder holder, int position) {
        TreatmentObject treatmentObject = treatmentObjectList.get(position);
        holder.bind(treatmentObject);
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(v, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return treatmentObjectList.size();
    }

    public static class PatientsTreatedViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView tvTreatmentDate;
        private final AppCompatTextView tvTreatedBy;
        private final AppCompatTextView tvPatientName;
        private final LinearLayoutCompat llForFirstAid;
        private final AppCompatTextView tvKindOfInjury;

        private final LinearLayoutCompat llForForDoctor;
        private final AppCompatTextView tvTypeOfDisease;

        public PatientsTreatedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTreatmentDate = itemView.findViewById(R.id.tvTreatmentDate);
            tvTreatedBy = itemView.findViewById(R.id.tvTreatedBy);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            llForFirstAid = itemView.findViewById(R.id.llForFirstAid);
            tvKindOfInjury = itemView.findViewById(R.id.tvKindOfInjury);

            llForForDoctor = itemView.findViewById(R.id.llForForDoctor);
            tvTypeOfDisease = itemView.findViewById(R.id.tvTypeOfDisease);
        }

        void bind(TreatmentObject treatmentObject) {
            tvTreatmentDate.setText(treatmentObject.getTreatmentDate());
            tvPatientName.setText(treatmentObject.getPatientName());
            if (treatmentObject.getFirstAidPersonUserID().length() > 0) {
                String treatedBy = "First Aid : " + treatmentObject.getTreatedByName();
                tvTreatedBy.setText(treatedBy);
                llForFirstAid.setVisibility(View.VISIBLE);
                llForForDoctor.setVisibility(View.GONE);
                tvKindOfInjury.setText(treatmentObject.getKindOfInjury());
            } else {
                String treatedBy = "Doctor : " + treatmentObject.getTreatedByName();
                tvTreatedBy.setText(treatedBy);

                llForFirstAid.setVisibility(View.GONE);
                llForForDoctor.setVisibility(View.VISIBLE);
                tvTypeOfDisease.setText(treatmentObject.getTypeOFDisease());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
