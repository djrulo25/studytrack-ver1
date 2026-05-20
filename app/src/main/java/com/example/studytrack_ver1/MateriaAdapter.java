package com.example.studytrack_ver1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MateriaAdapter extends RecyclerView.Adapter<MateriaAdapter.MateriaViewHolder> {

    private List<Materia> materiasList;
    private OnMateriaLongClickListener longClickListener;

    public interface OnMateriaLongClickListener {
        void onMateriaLongClick(Materia materia);
    }

    public MateriaAdapter(List<Materia> materiasList, OnMateriaLongClickListener longClickListener) {
        this.materiasList = materiasList;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public MateriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_materia, parent, false);
        return new MateriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriaViewHolder holder, int position) {
        Materia materia = materiasList.get(position);
        holder.tvNombre.setText(materia.getNombre());
        holder.tvProfesor.setText(materia.getProfesor());
        holder.tvSemestre.setText("Semestre: " + materia.getSemestre());
        holder.tvEstado.setText(materia.getEstado());

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onMateriaLongClick(materia);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return materiasList.size();
    }

    public static class MateriaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvProfesor, tvSemestre, tvEstado;

        public MateriaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvMateriaNombre);
            tvProfesor = itemView.findViewById(R.id.tvProfesor);
            tvSemestre = itemView.findViewById(R.id.tvSemestre);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}