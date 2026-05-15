package com.example.studytrack_ver1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PerfilFragment extends Fragment {

    private FirebaseAuth mAuth;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Vincular las vistas
        TextView tvName = view.findViewById(R.id.tvUserName);
        TextView tvEmail = view.findViewById(R.id.tvUserEmail);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Mostrar datos del usuario
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            tvName.setText(name != null && !name.isEmpty() ? name : "Usuario");
            tvEmail.setText(email != null ? email : "Sin correo");
        }

        // Configurar botón de cerrar sesión
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            
            // Redirigir al login y limpiar el historial de actividades
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}