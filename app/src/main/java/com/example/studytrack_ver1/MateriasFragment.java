package com.example.studytrack_ver1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MateriasFragment extends Fragment implements MateriaAdapter.OnMateriaLongClickListener {

    private RecyclerView rvMaterias;
    private MateriaAdapter adapter;
    private List<Materia> materiasList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public MateriasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_materias, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvMaterias = view.findViewById(R.id.rvMaterias);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddMateria);

        materiasList = new ArrayList<>();
        adapter = new MateriaAdapter(materiasList, this);

        rvMaterias.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMaterias.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddMateriaDialog());

        listenForMaterias();
    }

    private void listenForMaterias() {
        String userId = mAuth.getCurrentUser().getUid();
        
        // Consultamos la colección "materias" filtrando por el ID del usuario actual
        db.collection("usuarios").document(userId).collection("materias")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Error al cargar materias", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }

                        if (value != null) {
                            materiasList.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                Materia materia = doc.toObject(Materia.class);
                                materia.setId(doc.getId());
                                materiasList.add(materia);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void showAddMateriaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_add_materia, null);
        
        TextInputEditText etNombre = view.findViewById(R.id.etNombreMateria);
        TextInputEditText etSemestre = view.findViewById(R.id.etSemestre);
        TextInputEditText etProfesor = view.findViewById(R.id.etProfesor);
        TextInputEditText etEstado = view.findViewById(R.id.etEstado);

        builder.setView(view)
                .setPositiveButton("Guardar", null) // Set to null to override later
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());
        
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String semestre = etSemestre.getText().toString().trim();
            String profesor = etProfesor.getText().toString().trim();
            String estado = etEstado.getText().toString().trim();

            if (nombre.isEmpty()) {
                etNombre.setError("El nombre es obligatorio");
                return;
            }
            
            saveMateria(nombre, semestre, profesor, estado);
            dialog.dismiss();
        });
    }

    private void saveMateria(String nombre, String semestre, String profesor, String estado) {
        String userId = mAuth.getCurrentUser().getUid();
        Materia nuevaMateria = new Materia(nombre, semestre, profesor, estado);

        db.collection("usuarios").document(userId).collection("materias")
                .add(nuevaMateria)
                .addOnSuccessListener(documentReference -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Materia agregada", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onMateriaLongClick(Materia materia) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Materia")
                .setMessage("¿Estás seguro de que deseas eliminar " + materia.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    String userId = mAuth.getCurrentUser().getUid();
                    db.collection("usuarios").document(userId).collection("materias").document(materia.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Materia eliminada", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}