package com.example.burgerandgrill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ExampleViewHolder> {
    private ArrayList<MenuItem> mExampleList;
    private OrderListAdapter.OnItemClickListener mlistener;


    public interface OnItemClickListener{
        void onItemClick(int position);
        void onAddToOrderListClick(int position);
        void onRemoveFromOrderListClick(int position);
    }


    public void setOnItemClickListener(OrderListAdapter.OnItemClickListener listener){
        mlistener = listener;
    }
    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public Button addToOrderList;
        public Button removeFromOrderList;

        public ExampleViewHolder(View itemView, final OrderListAdapter.OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
            addToOrderList = itemView.findViewById(R.id.add_to_order);
            removeFromOrderList = itemView.findViewById(R.id.remove_from_order);


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
            addToOrderList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onAddToOrderListClick(position);
                        }
                    }
                }
            });
            removeFromOrderList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onRemoveFromOrderListClick(position);
                        }
                    }
                }
            });
        }
    }


    public OrderListAdapter(ArrayList<MenuItem> exampleList) {
        mExampleList = exampleList;

    }

    @Override
    public OrderListAdapter.ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        OrderListAdapter.ExampleViewHolder evh = new OrderListAdapter.ExampleViewHolder(v,mlistener);
        return evh;
    }

    @Override
    public void onBindViewHolder(OrderListAdapter.ExampleViewHolder holder, int position) {
        MenuItem currentItem = mExampleList.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getText1());
        if(!currentItem.getSize().equals("None")){
            holder.mTextView2.setText("Rs. " + currentItem.getText2() + " - " + currentItem.getSize());
        }
        else{
            holder.mTextView2.setText("Rs. " + currentItem.getText2());
        }

    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
