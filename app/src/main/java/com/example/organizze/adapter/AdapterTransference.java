package com.example.organizze.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.organizze.R;
import com.example.organizze.model.Transference;

import java.util.List;

/**
 * Created by Jamilton Damasceno
 */

public class AdapterTransference extends RecyclerView.Adapter<AdapterTransference.MyViewHolder> {

    List<Transference> transferences;
    Context context;

    public AdapterTransference(List<Transference> transferences, Context context) {
        this.transferences = transferences;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_transference, parent, false);
        return new MyViewHolder(listItem);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Transference transference = transferences.get(position);

        holder.title.setText(transference.getDescription());
        holder.value.setText(String.valueOf(transference.getValue()));
        holder.category.setText(transference.getCategory());

        holder.value.setTextColor(context.getResources().getColor(R.color.colorAccentReceita));
        if (transference.getType().equals("d")) {
            holder.value.setTextColor(context.getResources().getColor(R.color.colorAccentDespesa));
            holder.value.setText("-" + transference.getValue());
        }
    }


    @Override
    public int getItemCount() {
        return transferences.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, value, category;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textAdapterTitulo);
            value = itemView.findViewById(R.id.textAdapterValor);
            category = itemView.findViewById(R.id.textAdapterCategoria);
        }

    }

}
