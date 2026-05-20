package com.example.studytrack_ver1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TareasFragment extends Fragment implements TareaAdapter.OnTareaClickListener {

    private RecyclerView rvTareas;
    private TareaAdapter adapter;
    private List<Tarea> tareaList;
    private List<String> nombresMaterias;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public TareasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tareas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvTareas = view.findViewById(R.id.rvTareas);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddTarea);

        tareaList = new ArrayList<>();
        nombresMaterias = new ArrayList<>();
        adapter = new TareaAdapter(tareaList, this);

        rvTareas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTareas.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddTareaDialog());

        listenForTareas();
        fetchMaterias();
    }

    private void fetchMaterias() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).collection("materias")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    nombresMaterias.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        String nombre = doc.getString("nombre");
                        if (nombre != null) nombresMaterias.add(nombre);
                    }
                });
    }

    private void listenForTareas() {
        String userId = mAuth.getCurrentUser().getUid();
        
        db.collection("usuarios").document(userId).collection("tareas")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Error al cargar tareas", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }

                        if (value != null) {
                            tareaList.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                Tarea tarea = doc.toObject(Tarea.class);
                                tarea.setId(doc.getId());
                                tareaList.add(tarea);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void showAddTareaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_add_tarea, null);
        
        TextInputEditText etNombre = view.findViewById(R.id.etNombreTarea);
        TextInputEditText etDescripcion = view.findViewById(R.id.etDescripcionTarea);
        AutoCompleteTextView etMateria = view.findViewById(R.id.etMateriaTarea);
        TextInputEditText etFecha = view.findViewById(R.id.etFechaTarea);

        // Configurar el adaptador para el autocompletado de materias
        ArrayAdapter<String> materiaAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, nombresMaterias);
        etMateria.setAdapter(materiaAdapter);

        builder.setView(view)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());
        
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String materia = etMateria.getText().toString().trim();
            String fecha = etFecha.getText().toString().trim();

            if (nombre.isEmpty()) {
                etNombre.setError("El nombre es obligatorio");
                return;
            }

            saveTarea(nombre, descripcion, materia, fecha);
            dialog.dismiss();
        });
    }

    private void saveTarea(String nombre, String descripcion, String materia, String fecha) {
        String userId = mAuth.getCurrentUser().getUid();
        Tarea nuevaTarea = new Tarea(nombre, descripcion, fecha, materia, false);

        db.collection("usuarios").document(userId).collection("tareas")
                .add(nuevaTarea)
                .addOnSuccessListener(documentReference -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Tarea agregada", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onTareaCheckChanged(Tarea tarea, boolean isChecked) {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).collection("tareas").document(tarea.getId())
                .update("completada", isChecked)
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Error al actualizar estado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onTareaLongClick(Tarea tarea) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Tarea")
                .setMessage("¿Estás seguro de que deseas eliminar " + tarea.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    String userId = mAuth.getCurrentUser().getUid();
                    db.collection("usuarios").document(userId).collection("tareas").document(tarea.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Tarea eliminada", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
