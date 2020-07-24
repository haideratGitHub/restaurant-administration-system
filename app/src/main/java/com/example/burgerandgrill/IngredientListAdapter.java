package com.example.burgerandgrill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.IViewHolder> {

    private ArrayList<IngredientModel> mExampleList;
    public static class IViewHolder extends RecyclerView.ViewHolder {
        public TextView ingredientName;
        public EditText ingredientQuantity;
        public TextView ingredientQuantityUnit;

        public IViewHolder(View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.ith_ingredient);
            ingredientQuantity = itemView.findViewById(R.id.ith_ingredient_quantity);
            ingredientQuantityUnit = itemView.findViewById(R.id.ith_ingredient_quantity_unit);
        }
    }

    public IngredientListAdapter(ArrayList<IngredientModel> exampleList) {
        mExampleList = exampleList;
    }

    @NonNull
    @Override
    public IngredientListAdapter.IViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list,parent,false);
        IViewHolder ivh = new IViewHolder(v);
        return ivh;
    }


    @Override
    public void onBindViewHolder(@NonNull IngredientListAdapter.IViewHolder holder, int position) {

        IngredientModel currentItem = mExampleList.get(position);
        holder.ingredientName.setText(currentItem.getName());
        //edit text to be
        holder.ingredientQuantityUnit.setText(currentItem.getUnit());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
