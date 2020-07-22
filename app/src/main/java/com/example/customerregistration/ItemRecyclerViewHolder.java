package com.example.customerregistration;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemRecyclerViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;

    TextView name_tv;

    TextView address_tv;

    public ItemRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.avatar);
        name_tv=itemView.findViewById(R.id.cos_name);
        address_tv=itemView.findViewById(R.id.cos_address);
    }
}
