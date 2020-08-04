package com.example.burgerandgrill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SalesListAdapter extends RecyclerView.Adapter<SalesListAdapter.ExampleViewHolder> {
    private ArrayList<SalesModel> mExampleList;
    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView mSaleType;
        public TextView mSaleDate;
        public TextView mSaleBill;
        public ExampleViewHolder(View itemView) {
            super(itemView);
            mSaleType = itemView.findViewById(R.id.sale_item_type);
            mSaleDate = itemView.findViewById(R.id.sale_item_date);
            mSaleBill = itemView.findViewById(R.id.sale_item_bill);
        }
    }


    public SalesListAdapter(ArrayList<SalesModel> exampleList) {
        mExampleList = exampleList;

    }


    @Override
    public SalesListAdapter.ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_item, parent, false);
        SalesListAdapter.ExampleViewHolder evh = new SalesListAdapter.ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(SalesListAdapter.ExampleViewHolder holder, int position) {
        SalesModel currentItem = mExampleList.get(position);

        holder.mSaleType.setText(currentItem.getSaleType());
        holder.mSaleDate.setText(currentItem.saleDate);
        holder.mSaleBill.setText("Rs. " + currentItem.getSaleBill());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
