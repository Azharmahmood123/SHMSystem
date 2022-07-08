package com.shm.system.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.shm.system.R;
import com.shm.system.entity.LiveDemoObject;

import java.util.List;

public class PatientLiveDemoAdapter extends RecyclerView.Adapter<PatientLiveDemoAdapter.PatientsTreatedViewHolder> {

    private List<LiveDemoObject> liveDemoObjectList;

    public PatientLiveDemoAdapter(List<LiveDemoObject> liveDemoObjectList) {
        this.liveDemoObjectList = liveDemoObjectList;
    }

    public void updateList(List<LiveDemoObject> liveDemoObjectList) {
        this.liveDemoObjectList = liveDemoObjectList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PatientsTreatedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_live_demo, parent, false);
        return new PatientsTreatedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientsTreatedViewHolder holder, int position) {
        LiveDemoObject liveDemoObject = liveDemoObjectList.get(position);
        holder.bind(liveDemoObject);
    }

    @Override
    public int getItemCount() {
        return liveDemoObjectList.size();
    }

    public static class PatientsTreatedViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView tvHR;
        private final AppCompatTextView tvHUM;
        private final AppCompatTextView tvOXI;
        private final AppCompatTextView tvPOS;
        private final AppCompatTextView tvTEMP;

        public PatientsTreatedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHR = itemView.findViewById(R.id.tvHR);
            tvHUM = itemView.findViewById(R.id.tvHUM);
            tvOXI = itemView.findViewById(R.id.tvOXI);
            tvPOS = itemView.findViewById(R.id.tvPOS);
            tvTEMP = itemView.findViewById(R.id.tvTEMP);
        }

        void bind(LiveDemoObject liveDemoObject) {
            String hrText = liveDemoObject.getHR() + " (bpm)";
            tvHR.setText(hrText);
            String humText = liveDemoObject.getHUM() + " (%)";
            tvHUM.setText(humText);
            String oxiText = liveDemoObject.getOXI() + " (%)";
            tvOXI.setText(oxiText);
            String posText = liveDemoObject.getPOS() + "";
            tvPOS.setText(posText);
            String tempText = liveDemoObject.getTEMP() + " (℉)";
            tvTEMP.setText(tempText);
        }
    }
}
