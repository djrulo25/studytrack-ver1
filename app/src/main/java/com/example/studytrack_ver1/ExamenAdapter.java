package com.example.studytrack_ver1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExamenAdapter extends RecyclerView.Adapter<ExamenAdapter.ExamenViewHolder> {

    private List<Examen> examenList;
    private OnExamenLongClickListener longClickListener;

    public interface OnExamenLongClickListener {
        void onExamenLongClick(Examen examen);
    }

    public ExamenAdapter(List<Examen> examenList, OnExamenLongClickListener longClickListener) {
        this.examenList = examenList;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ExamenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_examen, parent, false);
        return new ExamenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamenViewHolder holder, int position) {
        Examen examen = examenList.get(position);
        holder.tvNombre.setText(examen.getNombre());
        holder.tvMateria.setText(examen.getMateriaNombre());
        holder.tvFecha.setText(examen.getFecha());
        holder.tvHora.setText(examen.getHora());

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onExamenLongClick(examen);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return examenList.size();
    }

    static class ExamenViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvMateria, tvFecha, tvHora;

        public ExamenViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreExamen);
            tvMateria = itemView.findViewById(R.id.tvMateriaExamen);
            tvFecha = itemView.findViewById(R.id.tvFechaExamen);
            tvHora = itemView.findViewById(R.id.tvHoraExamen);
        }
    }
}
