package com.example.mylogin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GastosActivity extends AppCompatActivity {
    private EditText nombreGasto, montoGasto;
    private Button botonAgregar, btnVerTotal;
    private RecyclerView recyclerGastos;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<Gasto> listaGastos;
    private AdaptadorGastos adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastos);

        nombreGasto = findViewById(R.id.nombreGasto);
        montoGasto = findViewById(R.id.montoGasto);
        botonAgregar = findViewById(R.id.botonAgregar);
        btnVerTotal = findViewById(R.id.btnVerTotal);
        recyclerGastos = findViewById(R.id.recyclerGastos);

        recyclerGastos.setLayoutManager(new LinearLayoutManager(this));
        listaGastos = new ArrayList<>();
        adaptador = new AdaptadorGastos(listaGastos);
        recyclerGastos.setAdapter(adaptador);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        cargarGastos();

        botonAgregar.setOnClickListener(v -> agregarGasto());

        btnVerTotal.setOnClickListener(v -> {
            Intent intent = new Intent(GastosActivity.this, TotalGastosActivity.class);
            startActivity(intent);
        });

        recyclerGastos.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerGastos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                        mostrarDialogoEliminar(listaGastos.get(position));
                    }
                }));
    }
    private void agregarGasto() {
        String nombre = nombreGasto.getText().toString().trim();
        String montoStr = montoGasto.getText().toString().trim();

        if (nombre.isEmpty() || montoStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        long monto;
        try {
            monto = Long.parseLong(montoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Map<String, Object> gasto = new HashMap<>();
        gasto.put("nombre", nombre);
        gasto.put("monto", monto);
        gasto.put("fecha", fecha);

        db.collection("usuarios")
                .document(mAuth.getCurrentUser().getUid())
                .collection("gastos")
                .add(gasto)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(GastosActivity.this, "Gasto agregado", Toast.LENGTH_SHORT).show();
                    nombreGasto.setText("");
                    montoGasto.setText("");
                    cargarGastos();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(GastosActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void cargarGastos() {
        listaGastos.clear();
        db.collection("usuarios")
                .document(mAuth.getCurrentUser().getUid())
                .collection("gastos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Gasto gasto = new Gasto(
                                doc.getId(),
                                doc.getString("nombre"),
                                doc.getLong("monto"),
                                doc.getString("fecha")
                        );
                        listaGastos.add(gasto);
                    }
                    adaptador.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(GastosActivity.this, "Error al cargar gastos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void mostrarDialogoEliminar(Gasto gasto) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar gasto")
                .setMessage("¿Deseas eliminar \"" + gasto.getNombre() + "\"?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarGasto(gasto))
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminarGasto(Gasto gasto) {
        db.collection("usuarios")
                .document(mAuth.getCurrentUser().getUid())
                .collection("gastos")
                .document(gasto.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Gasto eliminado", Toast.LENGTH_SHORT).show();
                    cargarGastos();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
