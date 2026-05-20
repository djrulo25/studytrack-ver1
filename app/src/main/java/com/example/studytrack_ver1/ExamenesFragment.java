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

public class ExamenesFragment extends Fragment implements ExamenAdapter.OnExamenLongClickListener {

    private RecyclerView rvExamenes;
    private ExamenAdapter adapter;
    private List<Examen> examenList;
    private List<String> nombresMaterias;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public ExamenesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_examenes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvExamenes = view.findViewById(R.id.rvExamenes);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddExamen);

        examenList = new ArrayList<>();
        nombresMaterias = new ArrayList<>();
        adapter = new ExamenAdapter(examenList, this);

        rvExamenes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExamenes.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddExamenDialog());

        listenForExamenes();
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

    private void listenForExamenes() {
        String userId = mAuth.getCurrentUser().getUid();
        
        db.collection("usuarios").document(userId).collection("examenes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Error al cargar exámenes", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }

                        if (value != null) {
                            examenList.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                Examen examen = doc.toObject(Examen.class);
                                examen.setId(doc.getId());
                                examenList.add(examen);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void showAddExamenDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_add_examen, null);
        
        TextInputEditText etNombre = view.findViewById(R.id.etNombreExamen);
        AutoCompleteTextView etMateria = view.findViewById(R.id.etMateriaExamen);
        TextInputEditText etFecha = view.findViewById(R.id.etFechaExamen);
        TextInputEditText etHora = view.findViewById(R.id.etHoraExamen);

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
            String materia = etMateria.getText().toString().trim();
            String fecha = etFecha.getText().toString().trim();
            String hora = etHora.getText().toString().trim();

            if (nombre.isEmpty()) {
                etNombre.setError("El nombre es obligatorio");
                return;
            }

            saveExamen(nombre, materia, fecha, hora);
            dialog.dismiss();
        });
    }

    private void saveExamen(String nombre, String materia, String fecha, String hora) {
        String userId = mAuth.getCurrentUser().getUid();
        Examen nuevoExamen = new Examen(nombre, fecha, hora, materia);

        db.collection("usuarios").document(userId).collection("examenes")
                .add(nuevoExamen)
                .addOnSuccessListener(documentReference -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Examen agregado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onExamenLongClick(Examen examen) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Examen")
                .setMessage("¿Estás seguro de que deseas eliminar " + examen.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    String userId = mAuth.getCurrentUser().getUid();
                    db.collection("usuarios").document(userId).collection("examenes").document(examen.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Examen eliminado", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
