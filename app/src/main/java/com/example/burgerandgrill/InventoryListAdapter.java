package com.example.burgerandgrill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InventoryListAdapter extends RecyclerView.Adapter<InventoryListAdapter.IViewHolder> {

    private ArrayList<IngredientModel> mExampleList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onAddQuantityClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    public static class IViewHolder extends RecyclerView.ViewHolder {
        public TextView ingredientName;
        public TextView ingredientQuantity;
        public TextView ingredientQuantityUnit;
        public Button addQuantity;

        public IViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.ith_ingredient_inventory);
            ingredientQuantity = itemView.findViewById(R.id.ith_ingredient_quantity_inventory);
            ingredientQuantityUnit = itemView.findViewById(R.id.ith_ingredient_quantity_unit_inventory);
            addQuantity = itemView.findViewById(R.id.add_quantity_inventory);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }

                }
            });
            addQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onAddQuantityClick(position);
                        }
                    }
                }
            });

        }
    }

    public InventoryListAdapter(ArrayList<IngredientModel> exampleList) {
        mExampleList = exampleList;
    }
    @NonNull
    @Override
    public InventoryListAdapter.IViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_list,parent,false);
        InventoryListAdapter.IViewHolder ivh = new InventoryListAdapter.IViewHolder(v,mListener);
        return ivh;
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryListAdapter.IViewHolder holder, int position) {
        IngredientModel currentItem = mExampleList.get(position);
        holder.ingredientName.setText(currentItem.getName());
        holder.ingredientQuantity.setText(currentItem.getQuantity());
        holder.ingredientQuantityUnit.setText(currentItem.getUnit());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

}
