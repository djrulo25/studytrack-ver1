package com.example.studytrack_ver1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    private List<Tarea> tareaList;
    private OnTareaClickListener listener;

    public interface OnTareaClickListener {
        void onTareaCheckChanged(Tarea tarea, boolean isChecked);
        void onTareaLongClick(Tarea tarea);
    }

    public TareaAdapter(List<Tarea> tareaList, OnTareaClickListener listener) {
        this.tareaList = tareaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tarea = tareaList.get(position);
        holder.tvNombre.setText(tarea.getNombre());
        holder.tvMateria.setText(tarea.getMateriaNombre());
        holder.tvFecha.setText(tarea.getFechaEntrega());
        
        holder.cbCompletada.setOnCheckedChangeListener(null);
        holder.cbCompletada.setChecked(tarea.isCompletada());
        
        holder.cbCompletada.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onTareaCheckChanged(tarea, isChecked);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onTareaLongClick(tarea);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return tareaList.size();
    }

    static class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvMateria, tvFecha;
        CheckBox cbCompletada;

        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreTarea);
            tvMateria = itemView.findViewById(R.id.tvMateriaTarea);
            tvFecha = itemView.findViewById(R.id.tvFechaTarea);
            cbCompletada = itemView.findViewById(R.id.cbCompletada);
        }
    }
}
