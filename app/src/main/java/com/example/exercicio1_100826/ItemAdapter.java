package com.example.exercicio1_100826;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private final List<Item> itens;
    private final OnItemClickListener listener;

    public ItemAdapter(List<Item> itens, OnItemClickListener listener) {
        this.itens = itens;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_linha, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itens.get(position);
        holder.tvTitulo.setText(item.titulo);
        holder.tvSubtitulo.setText(item.subtitulo);

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvSubtitulo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvSubtitulo = itemView.findViewById(R.id.tvSubtitulo);
        }
    }
}