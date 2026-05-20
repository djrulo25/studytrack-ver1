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

public class MateriasFragment extends Fragment {

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
        adapter = new MateriaAdapter(materiasList);

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
                            Toast.makeText(getContext(), "Error al cargar materias", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        materiasList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Materia materia = doc.toObject(Materia.class);
                            materia.setId(doc.getId());
                            materiasList.add(materia);
                        }
                        adapter.notifyDataSetChanged();
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
                .setPositiveButton("Guardar", (dialog, id) -> {
                    String nombre = etNombre.getText().toString();
                    String semestre = etSemestre.getText().toString();
                    String profesor = etProfesor.getText().toString();
                    String estado = etEstado.getText().toString();

                    if (!nombre.isEmpty()) {
                        saveMateria(nombre, semestre, profesor, estado);
                    } else {
                        Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());
        
        builder.create().show();
    }

    private void saveMateria(String nombre, String semestre, String profesor, String estado) {
        String userId = mAuth.getCurrentUser().getUid();
        Materia nuevaMateria = new Materia(nombre, semestre, profesor, estado);

        db.collection("usuarios").document(userId).collection("materias")
                .add(nuevaMateria)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Materia agregada", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                });
    }
}