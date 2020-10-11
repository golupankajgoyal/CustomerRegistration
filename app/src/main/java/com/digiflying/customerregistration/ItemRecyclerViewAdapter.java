package com.digiflying.customerregistration;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewHolder> {


    private ArrayList<ItemData> customers=new ArrayList<>();
    private Context context=null;

    public ItemRecyclerViewAdapter(Context mContext,ArrayList<ItemData> list){
        customers=list;
        context=mContext;
    }

    @NonNull
    @Override
    public ItemRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.item_list,parent,false);
        ItemRecyclerViewHolder viewHolder=new ItemRecyclerViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ItemRecyclerViewHolder holder, int position) {
        String url = customers.get(position).getImageUrl();
        Glide.with(context)
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(holder.imageView);
        holder.name_tv.setText(customers.get(position).getName());
        holder.address_tv.setText(customers.get(position).getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("customer", customers.get(position));//where customer is an instance of ItemData object

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                            Pair.create(holder.imageView,
                                    holder.imageView.getTransitionName()));
                    context.startActivity(intent,options.toBundle());
                }else {
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public void updateList(ArrayList<ItemData> list){
        customers = list;
        notifyDataSetChanged();
    }
}
