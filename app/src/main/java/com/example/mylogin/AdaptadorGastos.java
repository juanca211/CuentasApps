package com.example.mylogin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdaptadorGastos extends RecyclerView.Adapter<AdaptadorGastos.ViewHolder> {

    private List<Gasto> listaGastos;

    public AdaptadorGastos(List<Gasto> listaGastos) {
        this.listaGastos = listaGastos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Gasto gasto = listaGastos.get(position);
        holder.texto1.setText(gasto.getNombre());
        holder.texto2.setText("$" + gasto.getMonto() + " | " + gasto.getFecha());
    }

    @Override
    public int getItemCount() {
        return listaGastos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView texto1, texto2;
        ViewHolder(View itemView) {
            super(itemView);
            texto1 = itemView.findViewById(android.R.id.text1);
            texto2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
